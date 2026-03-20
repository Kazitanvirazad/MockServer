package com.server.core.constants;

/**
 * @author Kazi Tanvir Azad
 */
public final class CommonConstants {
    private CommonConstants() {
        throw new AssertionError("Initialization of this class is not allowed");
    }

    public static final int DEFAULT_RESPONSE_CODE = 200;
    public static final int METHOD_NOT_ALLOWED_HTTP_CODE = 405;
    public static final int DEFAULT_ID_LENGTH = 8;
    public static final long DEFAULT_RESPONSE_LENGTH = 0L;
    public static final String EMPTY_STRING = "";
    public static final String EMPTY_SPACE = " ";
    public static final String EQUALS_CHAR = "=";
    public static final String UNDERSCORE = "_";
    public static final String HYPHEN = "-";
    public static final String COMMA = ",";
    public static final String SEMI_COLON = ";";
    public static final String COLON = ":";
    public static final String DEFAULT_PATH = "/";
    public static final String SQL_COMMENT = "--";
    public static final String SQL_QUERY = "(?)";
    public static final String BLOCK_COMMENT_START = "/*";
    public static final String BLOCK_COMMENT_END = "*/";
    public static final String SQL_QUERY_SEPARATOR = "\\|~\\|~\\|";
    public static final String SECURE_RANDOM_ALGORITHM = "SHA1PRNG";
    public static final String SQL_DDL_QUERY_FILE_PATH = "/data/queries/ddl.sql";
    public static final String SQL_PRAGMA_ENABLE_FOREIGN_KEY_QUERY = "PRAGMA foreign_keys = ON";
    public static final String COOKIE_HEADER_KEY = "set-cookie";
    public static final String DOMAIN = "Domain";
    public static final String EXPIRES = "Expires";
    public static final String HTTPONLY = "HttpOnly";
    public static final String MAX_AGE = "Max-Age";
    public static final String PARTITIONED = "Partitioned";
    public static final String PATH = "Path";
    public static final String SAME_SITE = "SameSite";
    public static final String SECURE = "Secure";
    public static final String JSON_FILE_EXTENSION = "json";
    public static final String EXPORT_DIRECTORY_SELECTOR_TITLE = "Select directory to export collections";
    public static final String IMPORT_FILE_SELECTOR_TITLE = "Select collection file to import";
    public static final String RESPONSE_BINARY_FILE_SELECTOR_TITLE = "Select response file";
    public static final String EXPORT_DIRECTORY_PATH_DEFAULT_TEXT = "Path: ";
    public static final String IMPORT_FILE_PATH_DEFAULT_TEXT = "File Name: ";
    public static final String ACTIVE = "ACTIVE";
    public static final String INACTIVE = "INACTIVE";
}
