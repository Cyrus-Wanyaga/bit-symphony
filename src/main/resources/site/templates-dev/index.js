const observeWSChanges = async () => {
    const ws = new WebSocket("ws://localhost:8086/hmr", ['hmr']);

    ws.addEventListener("open", (event) => {
        console.log("Connected to webserver");
    });
    
    ws.addEventListener("message", (event) => {
        if (event.data === "reload") {
            window.location.reload();
        }
    });
};

observeWSChanges();