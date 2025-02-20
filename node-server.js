const os = require('os');
const WebSocket = require('ws');

const wsConnect = async () => {
    ws = new WebSocket('ws://localhost:8086/memory', ['memory']);
    ws.onopen = () => {
        console.log('WebSocket connection opened');
        startMemoryMonitoring(ws);
    };
    ws.onmessage = (event) => {
        console.log('Node JS received message ' + event.data);
    };
    ws.onclose = () => {
        console.log('WebSocket connection closed');
    };
    ws.onerror = (error) => {
        console.error(`WebSocket error: ${JSON.stringify(error)}`);
    }
}

const startMemoryMonitoring = (ws) => {
    const interval = 5000; // 5 seconds (adjust as needed)

    setInterval(() => {
        const totalMemory = os.totalmem();
        const freeMemory = os.freemem();
        const usedMemory = totalMemory - freeMemory;

        const memoryStats = {
            total: formatMemory(totalMemory),
            free: formatMemory(freeMemory),
            used: formatMemory(usedMemory),
            timestamp: Date.now() // Add a timestamp for charting
        };

        if (ws.readyState === WebSocket.OPEN) {
            ws.send(JSON.stringify(memoryStats)); // Send as JSON
            console.log("Memory stats sent:", memoryStats); // Log for debugging
        } else {
            console.log("WebSocket is not open. Stats not sent.");
            ws.close();
        }

    }, interval);
};


function formatMemory(bytes) {
    const units = ['B', 'KB', 'MB', 'GB', 'TB'];
    let unitIndex = 0;
    let size = bytes;

    while (size >= 1024 && unitIndex < units.length - 1) {
        size /= 1024;
        unitIndex++;
    }

    return `${size.toFixed(2)} ${units[unitIndex]}`;
}


// Call wsConnect to initiate the connection and start monitoring
wsConnect();