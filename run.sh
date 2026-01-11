#!/bin/bash
mkdir -p bin
javac -d bin src/offlineoj/core/*.java src/offlineoj/ui/*.java src/offlineoj/Main.java
if [ $? -eq 0 ]; then
    echo "Compilation Successful. Running..."
    java -cp bin offlineoj.Main
else
    echo "Compilation Failed."
fi
