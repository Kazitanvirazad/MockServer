package com.server.app.control;

import com.server.app.model.view.ServerTableData;
import com.server.app.server.ServerManager;
import javafx.scene.control.TableCell;
import org.apache.commons.lang3.ObjectUtils;

/**
 * author: Kazi Tanvir Azad
 */
public class ServerTableStatusFontColorTableCell extends TableCell<ServerTableData, String> {
    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
            setStyle(null);
        } else {
            setText(item);
            ServerTableData serverTableData = getTableRow().getItem();
            if (ObjectUtils.isNotEmpty(serverTableData)
                    && ObjectUtils.isNotEmpty(serverTableData.getServerObjectProperty())
                    && ObjectUtils.isNotEmpty(serverTableData.getServerObjectProperty().getServerId())) {
                String serverId = serverTableData.getServerObjectProperty().getServerId();
                if (ServerManager.INSTANCE.isServerActive(serverId)) {
                    setStyle("""
                            -fx-mid-text-color: green;
                            -fx-light-text-color: blue;
                            -fx-font-weight: bold;""");
                } else {
                    setStyle("""
                            -fx-mid-text-color: red;
                            -fx-light-text-color: blue;
                            -fx-font-weight: bold;""");
                }
            }
        }
    }
}
