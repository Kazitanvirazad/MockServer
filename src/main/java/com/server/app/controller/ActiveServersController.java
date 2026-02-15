package com.server.app.controller;

import com.server.app.event.handler.TableRowCopyKeyEventHandler;
import com.server.app.model.data.Server;
import com.server.app.model.view.ServerTableData;
import com.server.app.server.ServerManager;
import com.server.app.service.ServerService;
import com.server.app.service.Service;
import com.server.app.util.CustomKeyCode;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyEvent;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.server.app.util.AppUtil.closeWindowButtonEvent;
import static com.server.app.util.AppUtil.triggerConfirmationPrompt;
import static com.server.app.util.AppUtil.triggerErrorAlert;

/**
 * author: Kazi Tanvir Azad
 */
public class ActiveServersController implements Initializable {
    private final ServerService serverService;

    public ActiveServersController() {
        this.serverService = Service.INSTANCE.getServerService();
    }

    @FXML
    private TableView<ServerTableData> activeServerTable;
    @FXML
    private TableColumn<ServerTableData, String> serverNameCol;
    @FXML
    private TableColumn<ServerTableData, String> serverUrlCol;
    @FXML
    private TableColumn<ServerTableData, String> serverPortCol;

    @FXML
    private void handleStopServerButton(ActionEvent event) {
        ServerTableData selectedServerTableData = activeServerTable.getSelectionModel().getSelectedItem();
        if (ObjectUtils.isEmpty(selectedServerTableData)) {
            triggerErrorAlert("No server is selected", "Select a server to stop");
            return;
        }
        Server selectedServer = selectedServerTableData.getServerObjectProperty();
        ServerManager.INSTANCE.stopServer(selectedServer, true);
        if (!ServerManager.INSTANCE.hasAnyActiveServer()) {
            closeWindowButtonEvent(event);
            return;
        }
        // refreshing active servers table rows
        refreshActiveServerTableRows();
    }

    @FXML
    private void handleStopAllServerButton(ActionEvent event) {
        ButtonType promptResult = triggerConfirmationPrompt("Stop all servers?", "Confirm to stop all active servers");
        if (ButtonType.OK == promptResult) {
            ServerManager.INSTANCE.stopAllServers(true);
            closeWindowButtonEvent(event);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Allowing only single row selection in activeServerTable
        activeServerTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        // Adding key press event handler for activeServerTable row
        activeServerTable.setOnKeyPressed(keyEvent -> {
            // Adding 'Copy' event handler for activeServerTable row
            if (CustomKeyCode.INSTANCE.getCopyKeycodeCombination().match(keyEvent)) {
                EventHandler<KeyEvent> keyEventHandler = new TableRowCopyKeyEventHandler();
                keyEventHandler.handle(keyEvent);
            }
        });

        // setting server table cell value factory for all columns
        serverNameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getServerObjectProperty().getServerName()));
        serverUrlCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getServerObjectProperty().getUrlEndpoint()));
        serverPortCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getServerObjectProperty().getPort())));

        // setting all active servers to server table
        refreshActiveServerTableRows();
    }

    private void refreshActiveServerTableRows() {
        ObservableSet<String> activeServerIds = ServerManager.INSTANCE.getActiveServerIds();
        List<ServerTableData> activeServers = activeServerIds.stream()
                .filter(StringUtils::isNotBlank)
                .map(serverService::getServerById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(SimpleObjectProperty::new)
                .map(ServerTableData::new)
                .toList();
        activeServerTable.setItems(FXCollections.observableList(activeServers));
    }
}
