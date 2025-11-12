# ⚡BitSymphony

*A symphony of algorithms, performance, and precision - in pure Java.*

---

## 🎯 Overview

**BitSymphony** is a modular Java-based benchmarking and analysis suite that demonstrates how small changes in code or algorithmic design can create *large differences* in system performance. 

Build entirely with **vanilla Java** - no Maven, no Gradle - BitSymphony showcases how to: 
- Build and deploy using **only Java libraries**.
- Measure **execution time**, **CPU usage**, **memory**, and **disk I/O**.
- Compare algorithms and strategies for **I/O, computation, and concurrency**. 
- Visualize results via an **embedded website** powered by **Alpine.js** and **Pebble templates**.
- Enjoy a seamless developer experience using **Node.js-based HMR (Hot Module Reload) for instant front-end updates. 

BitSymphony is both a technical playground and a learning project - designed to help developers *feel* the effect of optimization while proving engineering craftsmanship. 

---

## 🧩 Core Features

### 🧱 1. Binary Only Build
BitSymphony is built and executed purely through Java's built in compiler and runtime: 
```bash
javac -d out $(find src -name "*.java")
java -cp out com.techsol.Main
```

### ⚙️ 2. Configurable Performance Tests
#### File I/O Benchmarks
Measure read/write performance:
- Write random data in **full** vs **chunked** mode. 
- Observe **disk I/O speed** and caching impact over time. 
- Compare large file reads using **BufferedReader** vs direct streaming. 

#### Computation Benchmarks
Compare how different algorithms and computation strategies affect performance:
- Filtering (primes, evens,odds, thresholds).
- Aggregation (max, min, average).
- Sorting using multiple algorithms.
- Chunk-based vs single-pass processing.

#### Concurrency & Server Simulation
Simulate parallel workloads:
- Single-threaded vs multithreaded request handling.
- Lock contention on shared resources.
- Stress tests that reveal how the JVM and CPU respond under load.

### 🧮 3. Resource Monitoring
Each test can record:
- CPU usage
- Memory allocation
- Disk I/O rate
Metrics are gathered via `ResourceMonitor` and stored for analysis. 

### 🗃️ 4. Embedded SQLite Database
BitSymphony includes a **self-contained SQLite database**.

All results, metrics, and configurations are saved locally - no setup required. 

### 🌐 5. Built-in Web Interface
The embedded HTTP server serves a clean, reactive dashboard for interaction and visualization. 

Key technologies:
- **Alpine.js** - Lightweight reactive UI for dynamic components. 
- **Pebble template engine** - Effecient server-side HTML rendering. 
- **Node.js + chokidar + ws** - Hot Module Reload (HMR) system that instantly reflects front-end changes during development. 
- **Custom Java web server** - Routes, APIs, and static file handling all built from scratch

### 🧠 6. Intelligent Development Mode
BitSymphony includes a bash (Windows 🪟 and UNIX🐧) script that: 
- Compiles annotated Java code (`HTTPath`, `HTTPPathProcessor`)
- Detects Node.js, installs dependencies if missing(`chokidar`, `ws`)
- Launches: 
-- The Java server in development mode
-- A Node.js file watcher for hot-reload
- Gracefully manages process shutdowns

---

## 🧑🏾‍💻 Author
### Cyrus Wanyaga Githogori
Developer | Systems Engineer | Performance Enthusiast

Every algorithm plays a note. Together, they form the BitSymphony. 

---

## 📜 License
MIT License - free for personal and educational use. 




