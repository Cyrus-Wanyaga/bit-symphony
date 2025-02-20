
// window.Chart.register(
//     Chart.scales.TimeScale,  // Register time scale
// );

export function createChart(ctx) {
    const chartData = {
        datasets: [{
            label: 'Total Memory',
            data: [],
            borderColor: 'blue',
            fill: false
        }, {
            label: 'Used Memory',
            data: [],
            borderColor: 'red',
            fill: false
        }, {
            label: 'Free Memory',
            data: [],
            borderColor: 'green',
            fill: false
        }],
        labels: [] // Timestamps for the x-axis
    };

    return new window.Chart(ctx, {
        type: 'line',
        data: chartData,
        options: {
            responsive: true,
            scales: {
                x: {
                    type: 'category', // Use time scale for x-axis
                    // time: {
                    //     unit: 'second' // Adjust time unit as needed
                    // }
                },
                y: {
                    beginAtZero: false // Or true, depending on your data
                }
            },
            plugins: {
                legend: {
                    onClick: (event, legendItem) => {
                        const datasetIndex = legendItem.datasetIndex;
                        const isHidden = chart.data.datasets[datasetIndex].hidden;

                        chart.data.datasets.forEach((dataset, index) => {
                            if (index !== datasetIndex) {
                                dataset.hidden = !isHidden; // Toggle visibility of other datasets
                            }
                        });

                        chart.update(); // Update the chart
                    }
                }
            }
        }
    });
}

export function updateChart(chart, memoryData) {
    if (!chart) {
        createChart();
    }

    const formattedTime = new Date(memoryData.timestamp).toLocaleTimeString();

    chart.data.labels.push(formattedTime); // Use Date objects for time scale
    chart.data.datasets[0].data.push(memoryData.total);
    chart.data.datasets[1].data.push(memoryData.used);
    chart.data.datasets[2].data.push(memoryData.free);

    chart.update();
}