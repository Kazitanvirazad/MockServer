package com.server.app.model.view;

import com.server.app.model.data.Header;
import javafx.beans.property.SimpleObjectProperty;

/**
 * author: Kazi Tanvir Azad
 */
public class HeaderTableData implements TableData {
    private SimpleObjectProperty<Header> headerSimpleObjectProperty;

    public HeaderTableData(SimpleObjectProperty<Header> headerSimpleObjectProperty) {
        this.headerSimpleObjectProperty = headerSimpleObjectProperty;
    }

    public Header getHeaderSimpleObjectProperty() {
        return headerSimpleObjectProperty.get();
    }

    public SimpleObjectProperty<Header> headerSimpleObjectPropertyProperty() {
        return headerSimpleObjectProperty;
    }

    public void setHeaderSimpleObjectProperty(Header headerSimpleObjectProperty) {
        this.headerSimpleObjectProperty.set(headerSimpleObjectProperty);
    }

    @Override
    public String getClipboardData() {
        return headerSimpleObjectProperty.get().toString();
    }
}
