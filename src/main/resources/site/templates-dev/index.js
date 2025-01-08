const observeWSChanges = async () => {
    const ws = new WebSocket("ws://localhost:8086/hmr", ['hmr']);

    ws.addEventListener("open", (event) => {
        console.log("Connected to webserver");
    })
};

observeWSChanges();