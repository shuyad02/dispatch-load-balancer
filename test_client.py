import urllib.request
import json

base_url = "http://localhost:8080"

# 1. Health check
res = urllib.request.urlopen(f"{base_url}/actuator/health")
print("1. Health check:", res.read().decode())

# 2. Add vehicles from PDF sample
vehicles_data = {
  "vehicles": [
    { "vehicleId": "VEH001", "capacity": 100, "currentLatitude": 28.7041, "currentLongitude": 77.1025, "currentAddress": "Karol Bagh, Delhi, India" },
    { "vehicleId": "VEH002", "capacity": 80,  "currentLatitude": 28.5355, "currentLongitude": 77.3910, "currentAddress": "Sector 18, Noida, Uttar Pradesh, India" },
    { "vehicleId": "VEH003", "capacity": 120, "currentLatitude": 28.4595, "currentLongitude": 77.0266, "currentAddress": "Cyber Hub, Gurgaon, Haryana, India" },
    { "vehicleId": "VEH004", "capacity": 90,  "currentLatitude": 28.6139, "currentLongitude": 77.2090, "currentAddress": "Connaught Place, Delhi, India" },
    { "vehicleId": "VEH005", "capacity": 110, "currentLatitude": 28.7041, "currentLongitude": 77.1025, "currentAddress": "Karol Bagh, Delhi, India" }
  ]
}
req = urllib.request.Request(f"{base_url}/api/dispatch/vehicles", data=json.dumps(vehicles_data).encode("utf-8"), headers={"Content-Type": "application/json"})
res = urllib.request.urlopen(req)
print("2. Vehicles Response:", res.read().decode())

# 3. Add orders from PDF sample
orders_data = {
  "orders": [
    { "orderId": "ORD001", "latitude": 28.6139, "longitude": 77.2090, "address": "Connaught Place, Delhi, India", "packageWeight": 15, "priority": "HIGH" },
    { "orderId": "ORD002", "latitude": 28.6139, "longitude": 77.2090, "address": "Connaught Place, Delhi, India", "packageWeight": 10, "priority": "MEDIUM" },
    { "orderId": "ORD003", "latitude": 28.7041, "longitude": 77.1025, "address": "Karol Bagh, Delhi, India", "packageWeight": 20, "priority": "LOW" },
    { "orderId": "ORD004", "latitude": 28.5355, "longitude": 77.3910, "address": "Sector 18, Noida, Uttar Pradesh, India", "packageWeight": 25, "priority": "HIGH" },
    { "orderId": "ORD005", "latitude": 28.4595, "longitude": 77.0266, "address": "Cyber Hub, Gurgaon, Haryana, India", "packageWeight": 30, "priority": "MEDIUM" },
    { "orderId": "ORD006", "latitude": 28.6139, "longitude": 77.2090, "address": "Connaught Place, Delhi, India", "packageWeight": 40, "priority": "LOW" },
    { "orderId": "ORD007", "latitude": 28.7041, "longitude": 77.1025, "address": "Karol Bagh, Delhi, India", "packageWeight": 20, "priority": "HIGH" },
    { "orderId": "ORD008", "latitude": 28.5355, "longitude": 77.3910, "address": "Sector 18, Noida, Uttar Pradesh, India", "packageWeight": 25, "priority": "HIGH" },
    { "orderId": "ORD009", "latitude": 28.4595, "longitude": 77.0266, "address": "Cyber Hub, Gurgaon, Haryana, India", "packageWeight": 15, "priority": "MEDIUM" },
    { "orderId": "ORD010", "latitude": 28.6139, "longitude": 77.2090, "address": "Connaught Place, Delhi, India", "packageWeight": 30, "priority": "LOW" }
  ]
}
req = urllib.request.Request(f"{base_url}/api/dispatch/orders", data=json.dumps(orders_data).encode("utf-8"), headers={"Content-Type": "application/json"})
res = urllib.request.urlopen(req)
print("3. Orders Response:", res.read().decode())

# 4. Get Dispatch Plan
res = urllib.request.urlopen(f"{base_url}/api/dispatch/plan")
plan = json.loads(res.read().decode())
print("\n4. OPTIMIZED DISPATCH PLAN OUTPUT:")
print(json.dumps(plan, indent=2))
