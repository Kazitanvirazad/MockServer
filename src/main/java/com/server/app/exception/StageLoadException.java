package com.server.app.exception;

import java.io.Serial;

/**
 * author: Kazi Tanvir Azad
 */
public class StageLoadException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 6148562279409959577L;

    public StageLoadException() {
    }

    public StageLoadException(Throwable cause) {
        super(cause);
    }

    public StageLoadException(String message) {
        super(message);
    }

    public StageLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    public StageLoadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
