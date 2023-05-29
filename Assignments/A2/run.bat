@echo off
set OUTPUT_DIR=Code
javac -d %OUTPUT_DIR% Code/*.java
java -cp %OUTPUT_DIR% Main
del %OUTPUT_DIR%\*.class
pause