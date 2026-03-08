-- Create Collection Table
CREATE TABLE IF NOT EXISTS collection (
    collection_id TEXT PRIMARY KEY,
    collection_name TEXT NOT NULL UNIQUE,
    createdOn DATETIME NOT NULL DEFAULT (datetime('now','localtime')),
    modifiedOn DATETIME NOT NULL DEFAULT (datetime('now','localtime'))
)
|~|~|
-- Create Server Table
CREATE TABLE IF NOT EXISTS server (
    server_id TEXT PRIMARY KEY,
    server_name TEXT NOT NULL,
    url_endpoint TEXT DEFAULT '/',
    response_code INT DEFAULT 200,
    method TEXT DEFAULT 'GET',
    delay BIGINT DEFAULT 0,
    port INT NOT NULL,
    response_data TEXT DEFAULT '',
    response_binary_path TEXT DEFAULT '',
    is_default_response_binary INT DEFAULT 0,
    headers TEXT,
    cookies TEXT,
    createdOn DATETIME NOT NULL DEFAULT (datetime('now','localtime')),
    modifiedOn DATETIME NOT NULL DEFAULT (datetime('now','localtime')),
    collection_id TEXT,
    FOREIGN KEY (collection_id) REFERENCES collection(collection_id)
)
|~|~|
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