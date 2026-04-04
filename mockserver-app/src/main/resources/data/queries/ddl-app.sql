-- Create Server Restart Table
CREATE TABLE IF NOT EXISTS server_restart (
    server_id TEXT
)
|~|~|
-- Create Settings Table
CREATE TABLE IF NOT EXISTS settings (
    id INT PRIMARY KEY,
    config_json_text TEXT NOT NULL
)