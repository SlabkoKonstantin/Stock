echo off

rem stock-commons
echo -------------------------------------
echo           stock-commons
echo -------------------------------------
cd .\stock-commons\
call mvn clean
call mvn install
cd ..\

rem stock-moex
echo -------------------------------------
echo           stock-moex
echo -------------------------------------
cd .\stock-moex\
call mvn clean
call mvn install
cd ..\

rem stock-candlestick
echo -------------------------------------
echo           stock-candlestick
echo -------------------------------------
cd .\stock-candlestick\
call mvn clean
call mvn install
cd ..\

rem stock-strategies/stock-strategy-common
echo -------------------------------------
echo       stock-strategy-common
echo -------------------------------------
cd .\stock-strategies\stock-strategy-common
call mvn clean
call mvn install
cd ..\..\

rem stock-strategies/stock-strategy-loader
echo -------------------------------------
echo        stock-strategy-loader
echo -------------------------------------
cd .\stock-strategies\stock-strategy-loader\
call mvn clean
call mvn install
cd ..\..\

rem stock-strategies/stock-strategy-peak
echo -------------------------------------
echo        stock-strategy-peak
echo -------------------------------------
cd .\stock-strategies\stock-strategy-peak
call mvn clean
call mvn package
call xcopy .\target\stock-strategy-peak-1.0.0.jar ..\..\stock-advisor\strategies\stock-strategy-peak-1.0.0.jar /Y
cd ..\..\

rem stock-strategies/stock-strategy-macd
echo -------------------------------------
echo        stock-strategy-macd
echo -------------------------------------
cd .\stock-strategies\stock-strategy-macd
call mvn clean
call mvn package
call xcopy .\target\stock-strategy-macd-1.0.0.jar ..\..\stock-advisor\strategies\stock-strategy-macd-1.0.0.jar /Y
cd ..\..\

rem stock-advisor
echo -------------------------------------
echo           stock-advisor
echo -------------------------------------
cd .\stock-advisor\
call mvn clean
call mvn package

pause