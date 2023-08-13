Generate the executable jar using following command:
jar -cvmf manifest.txt LotteryManagementSystemApp.jar manifest.txt -C bin . lib/ app-version.json

Run the app using following command:
java -jar LotteryManagementSystemApp.jar