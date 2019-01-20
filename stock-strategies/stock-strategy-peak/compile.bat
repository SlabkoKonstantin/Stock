call mvn clean
call mvn package
call xcopy .\target\stock-strategy-peak-1.0.0.jar ..\..\stock-advisor\strategies\stock-strategy-peak-1.0.0.jar /Y
pause