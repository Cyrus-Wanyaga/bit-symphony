// filewatcher.js
const chokidar = require('chokidar');
const path = require('path');
const fs = require('fs/promises');
const WebSocket = require('ws');
let ws;

// Get directories from command line arguments
const HTML_DIR = './src/main/resources/site/templates-dev';
const PEB_DIR = './src/main/resources/site/templates';

if (!HTML_DIR || !PEB_DIR) {
    console.error('Usage: node filewatcher.js <HTML_DIR> <PEB_DIR>');
    process.exit(1);
}

const wsConnect = async () => {
    ws = new WebSocket('ws://localhost:8086/hmr', ['hmr']);
    ws.onopen = () => {
        console.log('WebSocket connection opened');
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

// Initialize watcher
const watcher = chokidar.watch(HTML_DIR, {
    persistent: true,
    ignoreInitial: true,
    awaitWriteFinish: {
        stabilityThreshold: 100,
        pollInterval: 100
    }
});

// Function to ensure directory exists
async function ensureDir(dirPath) {
    try {
        await fs.mkdir(dirPath, { recursive: true });
    } catch (err) {
        if (err.code !== 'EEXIST') throw err;
    }
}

// Function to handle file operations
async function handleFile(filePath, eventType) {
    try {
        // Only process HTML, CSS, and JS files
        if (!/\.(html|css|js)$/.test(filePath)) return;

        const relativePath = path.relative(HTML_DIR, filePath);
        let targetPath = path.join(PEB_DIR, relativePath);
        const targetDir = path.dirname(targetPath);

        console.log(`${eventType}: ${filePath}`);

        if (eventType === 'unlink') {
            try {
                await fs.unlink(targetPath);
                console.log(`Deleted: ${targetPath}`);
            } catch (err) {
                if (err.code !== 'ENOENT') console.error(`Error deleting file: ${err}`);
            }
            return;
        }

        await ensureDir(targetDir);
        if (filePath.includes(".html")) {
            await fs.copyFile(filePath, targetPath.replace(".html", ".peb"));
        } else {
            await fs.copyFile(filePath, targetPath);
        }

        if (ws) {
            ws.send('reload');
        }
        console.log(`Processed: ${targetPath.replace(".html", ".peb")}`);
    } catch (err) {
        console.error(`Error processing file ${filePath}: ${err}`);
    }
}

// Set up event handlers
watcher
    .on('add', path => handleFile(path, 'add'))
    .on('change', path => handleFile(path, 'change'))
    .on('unlink', path => handleFile(path, 'unlink'))
    .on('error', error => console.error(`Watcher error: ${error}`));

console.log(`Watching ${HTML_DIR} for changes...`);

wsConnect();