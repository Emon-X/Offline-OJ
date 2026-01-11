#!/bin/bash

# 1. Clean and Compile
echo "Cleaning and Compiling..."
rm -rf bin
mkdir -p bin
javac -d bin src/offlineoj/core/*.java src/offlineoj/ui/*.java src/offlineoj/Main.java

if [ $? -ne 0 ]; then
    echo "Compilation Failed!"
    exit 1
fi

# 2. Create JAR
echo "Creating JAR..."
jar cfm OfflineOj.jar MANIFEST.MF -C bin .

# 3. Prepare Distribution Folder
echo "Creating Distribution Folder 'dist'..."
rm -rf dist
mkdir -p dist/problems

# 4. Copy Assets
cp OfflineOj.jar dist/
cp -r problems/* dist/problems/

# 5. Create Helper Scripts
# Windows Batch Script
echo '@echo off
java -jar OfflineOj.jar
pause' > dist/start.bat

# Linux/Mac Shell Script
echo '#!/bin/bash
java -jar OfflineOj.jar' > dist/start.sh
chmod +x dist/start.sh

# User Instructions
echo 'Offline Online Judge Simulator
------------------------------
How to Run:
1. Windows: Double-click "start.bat"
2. Linux/Mac: Run "./start.sh" or "java -jar OfflineOj.jar" in terminal.

Requirements:
- Java JDK installed (java and javac commands must work)
- GCC/G++ installed (for C/C++ support)

Troubleshooting:
- If nothing happens, open a terminal/cmd, drag "start.bat" or "start.sh" into it, and hit enter to see errors.
' > dist/README.txt

echo "Deployment Ready!"
echo "Go to 'dist/' folder and run: java -jar OfflineOj.jar"
echo "Note: Ensure gcc, g++, and java are in the system PATH."
