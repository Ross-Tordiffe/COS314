@echo off
javac -d bin Code*.java
jar cfe Main.jar Main -C bin .
java -jar Main.jar
del Main.jar
del bin\*.class
