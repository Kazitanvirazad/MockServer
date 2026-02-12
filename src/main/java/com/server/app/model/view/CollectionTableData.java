package com.server.app.model.view;

import com.server.app.model.data.Collection;
import javafx.beans.property.SimpleObjectProperty;

public class CollectionTableData implements TableData {
    private SimpleObjectProperty<Collection> collectionObjectProperty;

    public CollectionTableData(SimpleObjectProperty<Collection> collectionObjectProperty) {
        this.collectionObjectProperty = collectionObjectProperty;
    }

    public Collection getCollectionObjectProperty() {
        return collectionObjectProperty.get();
    }

    public SimpleObjectProperty<Collection> collectionObjectPropertyProperty() {
        return collectionObjectProperty;
    }

    public void setCollectionObjectProperty(Collection collectionObjectProperty) {
        this.collectionObjectProperty.set(collectionObjectProperty);
    }

    @Override
    public String getClipboardData() {
        return getCollectionObjectProperty().getCollectionName();
    }
}
