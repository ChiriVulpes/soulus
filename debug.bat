call powershell ".\gradlew.bat %1 --debug-jvm | tee compile_log.txt"
timeout 20
exit