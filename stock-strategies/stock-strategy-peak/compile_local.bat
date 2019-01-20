call mvn clean
call mvn package
call xcopy .\target\stock-strategy-peak-1.0.0.jar ..\..\stock-advisor\target\strategies\stock-strategy-peak-1.0.0.jar /Y
pause