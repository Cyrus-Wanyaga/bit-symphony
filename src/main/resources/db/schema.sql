CREATE TABLE IF NOT EXISTS app_config (
    key TEXT PRIMARY KEY,
    value TEXT
);

INSERT INTO app_config (key, value)
VALUES ('initial_run', 'true'),
       ('default_directory', 'files'),
       ('chunk_size', '1024'),
       ('test_interval', '1000'),
       ('username', null);

CREATE TABLE IF NOT EXISTS test_session (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    test_group TEXT NOT NULL,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    total_runs INTEGER DEFAULT 0,
    description TEXT
);

CREATE TABLE IF NOT EXISTS test_result (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    test_name TEXT NOT NULL,
    algorithm_name TEXT,
    start_time TEXT,
    end_time TEXT,
    duration_ms REAL,
    cpu_usage REAL,
    memory_usage REAL,
    disk_io REAL,
    extra_info TEXT,
    session_id INTEGER,
    FOREIGN KEY (session_id) REFERENCES test_session(id)
);
