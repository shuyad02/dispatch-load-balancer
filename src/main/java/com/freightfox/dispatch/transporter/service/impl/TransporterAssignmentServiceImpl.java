package com.freightfox.dispatch.transporter.service.impl;

import com.freightfox.dispatch.dto.response.ApiResponse;
import com.freightfox.dispatch.transporter.dto.*;
import com.freightfox.dispatch.transporter.service.TransporterAssignmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class TransporterAssignmentServiceImpl implements TransporterAssignmentService {

    private final List<LaneDto> storedLanes = new ArrayList<>();
    private final List<TransporterDto> storedTransporters = new ArrayList<>();

    @Override
    public synchronized ApiResponse saveInputData(TransportersInputRequest request) {
        this.storedLanes.clear();
        this.storedTransporters.clear();

        if (request.getLanes() != null) {
            this.storedLanes.addAll(request.getLanes());
        }
        if (request.getTransporters() != null) {
            this.storedTransporters.addAll(request.getTransporters());
        }

        log.info("Saved {} lanes and {} transporters", storedLanes.size(), storedTransporters.size());
        return ApiResponse.builder()
                .status("success")
                .message("Input data saved successfully.")
                .build();
    }

    @Override
    public synchronized TransportersInputRequest getCurrentData() {
        return TransportersInputRequest.builder()
                .lanes(new ArrayList<>(storedLanes))
                .transporters(new ArrayList<>(storedTransporters))
                .build();
    }

    @Override
    public synchronized AssignmentResponse getOptimizedAssignment(AssignmentRequest request) {
        if (storedLanes.isEmpty() || storedTransporters.isEmpty()) {
            throw new IllegalArgumentException("No lanes or transporters available. Please submit input data first.");
        }

        int maxK = request.getMaxTransporters();
        if (maxK <= 0) {
            throw new IllegalArgumentException("maxTransporters must be at least 1");
        }

        Set<Long> allLaneIds = new HashSet<>();
        for (LaneDto lane : storedLanes) {
            allLaneIds.add(lane.getId());
        }

        // Map: transporterId -> (laneId -> quote)
        Map<Long, Map<Long, Double>> quotesMap = new HashMap<>();
        List<Long> transporterIds = new ArrayList<>();
        for (TransporterDto t : storedTransporters) {
            transporterIds.add(t.getId());
            Map<Long, Double> lq = new HashMap<>();
            if (t.getLaneQuotes() != null) {
                for (LaneQuoteDto q : t.getLaneQuotes()) {
                    lq.put(q.getLaneId(), q.getQuote());
                }
            }
            quotesMap.put(t.getId(), lq);
        }

        // Find candidate transporter subsets of size <= maxK that cover ALL lanes
        List<SubsetSolution> validSolutions = new ArrayList<>();

        // Generate combinations for each size k = 1..min(maxK, transporterIds.size())
        int upperLimit = Math.min(maxK, transporterIds.size());
        for (int k = 1; k <= upperLimit; k++) {
            List<List<Long>> combinations = new ArrayList<>();
            combine(transporterIds, k, 0, new ArrayList<>(), combinations);

            for (List<Long> subset : combinations) {
                SubsetSolution sol = evaluateSubset(subset, allLaneIds, quotesMap);
                if (sol != null) {
                    validSolutions.add(sol);
                }
            }
        }

        if (validSolutions.isEmpty()) {
            throw new IllegalArgumentException("Cannot cover all lanes with up to " + maxK + " transporters. Please increase maxTransporters or provide more transporter quotes.");
        }

        // Primary: Cost Minimization (lowest total cost)
        // Secondary: Maximize Transporter Usage (higher count of selected transporters up to maxK)
        validSolutions.sort((a, b) -> {
            int costCmp = Double.compare(a.totalCost, b.totalCost);
            if (costCmp != 0) {
                return costCmp;
            }
            return Integer.compare(b.selectedTransporters.size(), a.selectedTransporters.size());
        });

        SubsetSolution best = validSolutions.get(0);

        Number totalCostNum = (Math.abs(best.totalCost - Math.round(best.totalCost)) < 0.001)
                ? Math.round(best.totalCost)
                : Math.round(best.totalCost * 100.0) / 100.0;

        return AssignmentResponse.builder()
                .status("success")
                .totalCost(totalCostNum)
                .assignments(best.assignments)
                .selectedTransporters(best.selectedTransporters)
                .build();
    }

    private static class SubsetSolution {
        double totalCost;
        List<Long> selectedTransporters;
        List<LaneAssignmentDto> assignments;

        SubsetSolution(double totalCost, List<Long> selectedTransporters, List<LaneAssignmentDto> assignments) {
            this.totalCost = totalCost;
            this.selectedTransporters = selectedTransporters;
            this.assignments = assignments;
        }
    }

    private SubsetSolution evaluateSubset(List<Long> subset, Set<Long> allLaneIds, Map<Long, Map<Long, Double>> quotesMap) {
        double totalCost = 0.0;
        List<LaneAssignmentDto> assignments = new ArrayList<>();
        Set<Long> actuallyUsedTransporters = new HashSet<>();

        // Sort lanes for deterministic ordering
        List<Long> sortedLanes = new ArrayList<>(allLaneIds);
        Collections.sort(sortedLanes);

        for (Long laneId : sortedLanes) {
            double minQuote = Double.MAX_VALUE;
            Long bestTransporter = null;

            for (Long tId : subset) {
                Map<Long, Double> tQuotes = quotesMap.get(tId);
                if (tQuotes != null && tQuotes.containsKey(laneId)) {
                    double q = tQuotes.get(laneId);
                    if (q < minQuote) {
                        minQuote = q;
                        bestTransporter = tId;
                    }
                }
            }

            if (bestTransporter == null) {
                // This subset cannot cover this lane
                return null;
            }

            totalCost += minQuote;
            actuallyUsedTransporters.add(bestTransporter);
            assignments.add(LaneAssignmentDto.builder()
                    .laneId(laneId)
                    .transporterId(bestTransporter)
                    .build());
        }

        // Return solution with subset of selected transporters
        List<Long> selected = new ArrayList<>(subset);
        Collections.sort(selected);
        return new SubsetSolution(totalCost, selected, assignments);
    }

    private void combine(List<Long> items, int k, int start, List<Long> current, List<List<Long>> result) {
        if (current.size() == k) {
            result.add(new ArrayList<>(current));
            return;
        }
        for (int i = start; i < items.size(); i++) {
            current.add(items.get(i));
            combine(items, k, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }

    @Override
    public ApiResponse loadTestCase1() {
        List<LaneDto> lanes = List.of(
                new LaneDto(1L, "Mumbai", "Delhi"),
                new LaneDto(2L, "Delhi", "Bangalore"),
                new LaneDto(3L, "Chennai", "Kolkata"),
                new LaneDto(4L, "Pune", "Hyderabad"),
                new LaneDto(5L, "Ahmedabad", "Jaipur")
        );

        List<TransporterDto> transporters = List.of(
                new TransporterDto(1L, "Transporter T1", List.of(
                        new LaneQuoteDto(1L, 20835.0), new LaneQuoteDto(2L, 10512.0),
                        new LaneQuoteDto(3L, 22105.0), new LaneQuoteDto(4L, 42481.0),
                        new LaneQuoteDto(5L, 19862.0)
                )),
                new TransporterDto(2L, "Transporter T2", List.of(
                        new LaneQuoteDto(1L, 48844.0), new LaneQuoteDto(2L, 31326.0),
                        new LaneQuoteDto(3L, 18640.0), new LaneQuoteDto(4L, 45828.0),
                        new LaneQuoteDto(5L, 18297.0)
                )),
                new TransporterDto(3L, "Transporter T3", List.of(
                        new LaneQuoteDto(1L, 39020.0), new LaneQuoteDto(2L, 20648.0),
                        new LaneQuoteDto(3L, 31438.0), new LaneQuoteDto(4L, 36447.0),
                        new LaneQuoteDto(5L, 12789.0)
                )),
                new TransporterDto(4L, "Transporter T4", List.of(
                        new LaneQuoteDto(1L, 14400.0), new LaneQuoteDto(2L, 44514.0),
                        new LaneQuoteDto(3L, 14316.0), new LaneQuoteDto(4L, 10678.0),
                        new LaneQuoteDto(5L, 13032.0)
                )),
                new TransporterDto(5L, "Transporter T5", List.of(
                        new LaneQuoteDto(1L, 11601.0), new LaneQuoteDto(2L, 19760.0),
                        new LaneQuoteDto(3L, 40870.0), new LaneQuoteDto(4L, 20635.0),
                        new LaneQuoteDto(5L, 26421.0)
                )),
                new TransporterDto(6L, "Transporter T6", List.of(
                        new LaneQuoteDto(1L, 35095.0), new LaneQuoteDto(2L, 12494.0),
                        new LaneQuoteDto(3L, 17808.0), new LaneQuoteDto(4L, 36210.0),
                        new LaneQuoteDto(5L, 39444.0)
                )),
                new TransporterDto(7L, "Transporter T7", List.of(
                        new LaneQuoteDto(1L, 26070.0), new LaneQuoteDto(2L, 41098.0),
                        new LaneQuoteDto(3L, 20932.0), new LaneQuoteDto(4L, 16897.0),
                        new LaneQuoteDto(5L, 27938.0)
                ))
        );

        return saveInputData(new TransportersInputRequest(new ArrayList<>(lanes), new ArrayList<>(transporters)));
    }

    @Override
    public ApiResponse loadTestCase2() {
        List<LaneDto> lanes = List.of(
                new LaneDto(1L, "Chandigarh", "Shimla"),
                new LaneDto(2L, "Agra", "Kanpur"),
                new LaneDto(3L, "Varanasi", "Gorakhpur"),
                new LaneDto(4L, "Amritsar", "Ludhiana"),
                new LaneDto(5L, "Coimbatore", "Madurai")
        );

        List<TransporterDto> transporters = List.of(
                new TransporterDto(1L, "Transporter X", List.of(
                        new LaneQuoteDto(1L, 1000.0), new LaneQuoteDto(2L, 1500.0)
                )),
                new TransporterDto(2L, "Transporter Y", List.of(
                        new LaneQuoteDto(3L, 2000.0), new LaneQuoteDto(4L, 1800.0)
                )),
                new TransporterDto(3L, "Transporter Z", List.of(
                        new LaneQuoteDto(5L, 1200.0)
                )),
                new TransporterDto(4L, "Transporter W", List.of(
                        new LaneQuoteDto(1L, 1100.0), new LaneQuoteDto(4L, 1750.0)
                ))
        );

        return saveInputData(new TransportersInputRequest(new ArrayList<>(lanes), new ArrayList<>(transporters)));
    }
}
