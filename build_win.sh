#!/bin/bash

javac_custom="C:/Users/cgithogori/softwares/java/jdk-17.0.12/bin/javac.exe"
java_custom="C:/Users/cgithogori/softwares/java/jdk-17.0.12/bin/java.exe"

echo $java_custom

# Function to check if Node.js is installed
check_node() {
    if command -v node >/dev/null 2>&1; then
        node_version=$(node --version)
        echo "Node.js is installed (${node_version})"
        return 0
    else
        echo "Node.js is not installed"
        return 1
    fi
}

# Function to check if chokidar is installed
check_chokidar() {
    if npm list chokidar >/dev/null 2>&1; then
        return 0
    else
        echo "Installing chokidar..."
        npm install chokidar
        return $?
    fi
}

# Function to check if ws is installed
check_ws() {
    if npm list ws >/dev/null 2>&1; then
        return 0
    else
        echo "Installing ws..."
        npm install ws
        return $?
    fi
}

compile_java() {
    echo "Compiling Java classes..."
    $javac_custom -d bin-annotations -cp "lib/*" src/main/java/com/techsol/web/annotations/HTTPPath.java
    $javac_custom -d bin-processor -cp "lib/*;bin-annotations" src/main/java/com/techsol/web/annotations/HTTPPathProcessor.java
    $javac_custom -d bin -cp "bin-processor;bin-annotations;lib/*" -processor com.techsol.web.annotations.HTTPPathProcessor $(find src/main/java -name "*.java")
}

compile_java

if [[ "${DEV_MODE}" == "true" ]] || [[ "$1" == "--dev" ]]; then
    echo "Development mode detected"

    # Check for Node.js and chokidar
    if check_node && check_chokidar && check_ws; then
        # Start Java application
        $java_custom -Ddev.mode=true -cp "bin;lib/*;src/main/resources" com.techsol.Main &
        JAVA_PID=$!

        sleep 15

        echo "Starting Node.js file watcher..."
        # Start Node.js watcher in background
        node filewatcher.js "${HTML_DIR}" "${PEB_DIR}" &
        NODE_PID=$!

        node node-server.js &
        NODE_PID_1=$!

        trap "echo 'Shutting down processes...'; kill $JAVA_PID $NODE_PID; exit" SIGINT SIGTERM

        # Wait for either process to exit
        wait $JAVA_PID $NODE_PID

        # Clean up any remaining processes
        kill $JAVA_PID $NODE_PID $NODE_PID_1 2>/dev/null
    else
        echo "Falling back to Java implementation..."
        $java_custom -Ddev.mode=true -cp "bin;lib/*;src/main/resources" com.techsol.Main
    fi
else
    # Production mode - use Java implementation
    $java_custom -cp "bin;lib/*;src/main/resources" com.techsol.Main
fi

# java -Ddev.mode=true -cp "bin:lib/*:src/main/resources" com.techsol.Main
