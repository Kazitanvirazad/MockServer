package com.server.app.model.view;

import com.server.app.model.data.Cookie;
import javafx.beans.property.SimpleObjectProperty;

/**
 * author: Kazi Tanvir Azad
 */
public class CookieTableData implements TableData {
    private SimpleObjectProperty<Cookie> cookieSimpleObjectProperty;

    public CookieTableData(SimpleObjectProperty<Cookie> cookieSimpleObjectProperty) {
        this.cookieSimpleObjectProperty = cookieSimpleObjectProperty;
    }

    public Cookie getCookieSimpleObjectProperty() {
        return cookieSimpleObjectProperty.get();
    }

    public SimpleObjectProperty<Cookie> cookieSimpleObjectPropertyProperty() {
        return cookieSimpleObjectProperty;
    }

    public void setCookieSimpleObjectProperty(Cookie cookieSimpleObjectProperty) {
        this.cookieSimpleObjectProperty.set(cookieSimpleObjectProperty);
    }

    @Override
    public String getClipboardData() {
        return cookieSimpleObjectProperty.get().value();
    }
}
