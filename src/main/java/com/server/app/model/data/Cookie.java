package com.server.app.model.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.server.app.dto.CookieDto;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import static com.server.app.constants.ApplicationConstants.DEFAULT_PATH;
import static com.server.app.constants.ApplicationConstants.DOMAIN;
import static com.server.app.constants.ApplicationConstants.EMPTY_SPACE;
import static com.server.app.constants.ApplicationConstants.EQUALS_CHAR;
import static com.server.app.constants.ApplicationConstants.EXPIRES;
import static com.server.app.constants.ApplicationConstants.HTTPONLY;
import static com.server.app.constants.ApplicationConstants.MAX_AGE;
import static com.server.app.constants.ApplicationConstants.PARTITIONED;
import static com.server.app.constants.ApplicationConstants.PATH;
import static com.server.app.constants.ApplicationConstants.SAME_SITE;
import static com.server.app.constants.ApplicationConstants.SECURE;
import static com.server.app.constants.ApplicationConstants.SEMI_COLON;
import static com.server.app.util.AppUtil.generateUniqueAlphanumericId;
import static com.server.app.util.AppUtil.triggerErrorAlert;

/**
 * @author Kazi Tanvir Azad
 */
public class Cookie implements Serializable {
    @Serial
    private static final long serialVersionUID = 8650284304480177017L;
    private final transient String cookieViewUUID;
    private boolean httpOnly;
    private boolean partitioned;
    private boolean secure;
    private Long maxAge;
    private String name;
    private String value;
    private String domain;
    private String expires;
    private String path;
    private SameSite sameSite;

    public Cookie(CookieDto cookieDto) {
        this(cookieDto.domain(), cookieDto.expires(), cookieDto.httpOnly(), cookieDto.maxAge(), cookieDto.name(),
                cookieDto.partitioned(), cookieDto.path(), cookieDto.sameSite(), cookieDto.secure(), cookieDto.value());
    }

    public Cookie(String domain, String expires, boolean httpOnly, Long maxAge, String name,
                  boolean partitioned, String path, String sameSite, boolean secure, String value) {
        this();
        this.domain = domain;
        this.expires = expires;
        this.httpOnly = httpOnly;
        this.maxAge = maxAge;
        this.name = name;
        this.partitioned = partitioned;
        if (null != path) this.path = path;
        try {
            this.sameSite = SameSite.valueOf(sameSite);
        } catch (Exception ignore) {
        }
        this.secure = secure;
        this.value = value;
    }

    public Cookie(String domain, String expires, boolean httpOnly, Long maxAge, String name,
                  boolean partitioned, String path, SameSite sameSite, boolean secure, String value) {
        this(domain, expires, httpOnly, maxAge, name, partitioned, path,
                (null != sameSite) ? sameSite.name() : null, secure, value);
    }

    public Cookie() {
        this.path = DEFAULT_PATH;
        Optional<String> uid = generateUniqueAlphanumericId();
        if (uid.isEmpty()) {
            triggerErrorAlert("Something went wrong while creating Cookie!",
                    "Please try again later.");
            throw new RuntimeException("UUID7 generate failed while creating Cookie!");
        }
        this.cookieViewUUID = uid.get();
    }

    public String value() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(name).append(EQUALS_CHAR).append(value);
        if (null != domain) {
            stringBuilder.append(SEMI_COLON).append(EMPTY_SPACE).append(DOMAIN).append(EQUALS_CHAR).append(domain);
        }
        if (null != expires) {
            stringBuilder.append(SEMI_COLON).append(EMPTY_SPACE).append(EXPIRES).append(EQUALS_CHAR).append(expires);
        }
        if (httpOnly) {
            stringBuilder.append(SEMI_COLON).append(EMPTY_SPACE).append(HTTPONLY);
        }
        if (null != maxAge) {
            stringBuilder.append(SEMI_COLON).append(EMPTY_SPACE).append(MAX_AGE).append(EQUALS_CHAR).append(maxAge);
        }
        if (partitioned) {
            stringBuilder.append(SEMI_COLON).append(EMPTY_SPACE).append(PARTITIONED);
        }
        if (null != path) {
            stringBuilder.append(SEMI_COLON).append(EMPTY_SPACE).append(PATH).append(EQUALS_CHAR).append(path);
        }
        if (null != sameSite) {
            stringBuilder.append(SEMI_COLON).append(EMPTY_SPACE).append(SAME_SITE)
                    .append(EQUALS_CHAR).append(sameSite.getSameSiteValue());
        }
        if (secure) {
            stringBuilder.append(SEMI_COLON).append(EMPTY_SPACE).append(SECURE);
        }
        return stringBuilder.toString();
    }

    public enum SameSite implements Serializable {
        STRICT("Strict"), LAX("Lax"), NONE("None");
        private final String sameSiteValue;

        SameSite(String sameSiteValue) {
            this.sameSiteValue = sameSiteValue;
        }

        // to be used only for print method
        public String getSameSiteValue() {
            return sameSiteValue;
        }
    }

    public void setMaxAge(Long maxAge) {
        this.maxAge = maxAge;
    }

    public Long getMaxAge() {
        return maxAge;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }

    public boolean isHttpOnly() {
        return httpOnly;
    }

    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPartitioned() {
        return partitioned;
    }

    public void setPartitioned(boolean partitioned) {
        this.partitioned = partitioned;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @JsonInclude
    public String getSameSite() {
        if (null != sameSite)
            return sameSite.name();
        return null;
    }

    public void setSameSite(String sameSite) {
        try {
            this.sameSite = SameSite.valueOf(sameSite);
        } catch (Exception ignore) {
        }
    }

    public void setSameSite(SameSite sameSite) {
        this.sameSite = sameSite;
    }

    public boolean isSecure() {
        return secure;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Cookie cookie)) return false;
        return Objects.equals(cookieViewUUID, cookie.cookieViewUUID);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(cookieViewUUID);
    }
}
