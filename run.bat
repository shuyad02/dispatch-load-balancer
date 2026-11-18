@echo off
set JAVA_HOME=C:\Program Files\Android\Android Studio\jbr
set PATH=%JAVA_HOME%\bin;%PATH%
echo ====================================================
echo Starting FreightFox Backend Services on Port 8081...
echo Swagger UI: http://localhost:8081/swagger-ui.html
echo ====================================================
java -jar "%~dp0target\dispatch-load-balancer-1.0.0.jar"
pause
