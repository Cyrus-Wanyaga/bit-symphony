#!/bin/bash

C:/Users/cgithogori/softwares/java/jdk-17.0.12/bin/javac -d bin -cp "lib/*" $(find src/main/java -name "*.java")

C:/Users/cgithogori/softwares/java/jdk-17.0.12/bin/java -cp "bin;lib/*;src/main/resources" com.techsol.Main