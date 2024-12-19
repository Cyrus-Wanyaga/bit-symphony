CREATE TABLE IF NOT EXISTS app_config (
    key TEXT PRIMARY KEY,
    value TEXT
);

INSERT INTO app_config (key, value)
VALUES ('initial_run', 'true'),
       ('default_directory', 'files'),
       ('chunk_size', '1024');
