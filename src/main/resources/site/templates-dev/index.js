import { createChart, updateChart } from "./chart-util.js";

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

const drawMemoryChart = () => {
    let chart;

    const ws = new WebSocket("ws://localhost:8086/memory", ['memory']);

    ws.addEventListener("open", (event) => {
        console.log("Connected to webserver");
    });

    ws.addEventListener("message", (event) => {
        const memoryData = JSON.parse(event.data);
        if (!chart) {
            const ctx = document.getElementById('memoryChart').getContext('2d');
            chart = createChart(ctx);
        }

        updateChart(chart, memoryData);
    });

    // function createChart() {
    //     chart = new Chart(ctx, {
    //         type: 'line',
    //         data: chartData,
    //         options: {
    //             responsive: true,
    //             scales: {
    //                 x: {
    //                     type: 'time', // Use time scale for x-axis
    //                     time: {
    //                         unit: 'second' // Adjust time unit as needed
    //                     }
    //                 },
    //                 y: {
    //                     beginAtZero: false // Or true, depending on your data
    //                 }
    //             },
    //             plugins: {
    //                 legend: {
    //                     onClick: (event, legendItem) => {
    //                         const datasetIndex = legendItem.datasetIndex;
    //                         const isHidden = chart.data.datasets[datasetIndex].hidden;

    //                         chart.data.datasets.forEach((dataset, index) => {
    //                             if (index !== datasetIndex) {
    //                                 dataset.hidden = !isHidden; // Toggle visibility of other datasets
    //                             }
    //                         });

    //                         chart.update(); // Update the chart
    //                     }
    //                 }
    //             }
    //         }
    //     });
    // }

    // function updateChart(memoryData) {
    //     if (!chart) {
    //         createChart();
    //     }

    //     chart.data.labels.push(new Date(memoryData.timestamp)); // Use Date objects for time scale
    //     chart.data.datasets[0].data.push(memoryData.total);
    //     chart.data.datasets[1].data.push(memoryData.used);
    //     chart.data.datasets[2].data.push(memoryData.free);

    //     chart.update();
    // }
}

drawMemoryChart();