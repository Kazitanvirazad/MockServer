package com.server.app.event.handler;

import com.server.app.model.view.TableData;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TableView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyEvent;
import org.apache.commons.collections4.CollectionUtils;

/**
 * author: Kazi Tanvir Azad
 */
public class TableRowCopyKeyEventHandler implements EventHandler<KeyEvent> {

    @Override
    public void handle(KeyEvent keyEvent) {
        if (keyEvent.getSource() instanceof TableView) {
            // copy to clipboard
            copySelectionToClipboard((TableView<TableData>) keyEvent.getSource());
            // consuming handled event
            keyEvent.consume();
        }
    }

    private void copySelectionToClipboard(TableView<TableData> tableView) {
        ObservableList<TableData> selectedTableData = tableView.getSelectionModel().getSelectedItems();
        if (!CollectionUtils.isEmpty(selectedTableData)) {
            StringBuilder stringBuilder = new StringBuilder();
            selectedTableData.forEach(keyValue -> stringBuilder
                    .append(keyValue.getClipboardData())
                    .append("\n")
            );
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putString(stringBuilder.toString());
            clipboard.setContent(clipboardContent);
        }
    }
}
