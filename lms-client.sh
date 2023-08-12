#!/bin/bash

# Check if Java is installed
java -version >/dev/null 2>&1
if [ $? -ne 0 ]; then
    echo "Java is not installed. Please install Java and try again."
    exit 1
fi

# Check JVM memory settings
MIN_MEMORY="256M"
MAX_MEMORY="1024M"

java -Xms$MIN_MEMORY -Xmx$MAX_MEMORY -version >/dev/null 2>&1
if [ $? -ne 0 ]; then
    echo "JVM does not have sufficient memory. Adjust memory settings and try again."
    exit 1
fi

# Run the application JAR
java -jar LotteryManagementSystemApp.jar

# Exit with the exit code of the Java application
exit $?
