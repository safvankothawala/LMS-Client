@echo off

rem Check if Java is installed
java -version 2>&1 > nul
if %errorlevel% neq 0 (
    echo Java is not installed. Please install Java and try again.
    exit /b 1
)

rem Check JVM memory settings
set "MIN_MEMORY=256M"
set "MAX_MEMORY=1024M"

java -Xms%MIN_MEMORY% -Xmx%MAX_MEMORY% -version 2>&1 > nul
if %errorlevel% neq 0 (
    echo JVM does not have sufficient memory. Adjust memory settings and try again.
    exit /b 1
)

rem Run the application JAR
java -jar LotteryManagementSystemApp.jar

rem Exit with the exit code of the Java application
exit /b %errorlevel%
