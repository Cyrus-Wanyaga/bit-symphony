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

// document.querySelectorAll('script[type="text/css"]').forEach(async (script) => {
//     let cssPath = script.textContent.trim();
//     let response = await fetch(cssPath);
//     let cssText = await response.text();
//     let style = document.createElement("style");
//     style.innerHTML = cssText;
//     document.head.appendChild(style);
// });

// document.addEventListener("DOMContentLoaded", () => {
//     document.body.classList.add("styles-loaded");
// });