package com.server.app.controller;

import com.server.app.control.ButtonImageViewTableCell;
import com.server.app.control.ServerTableStatusFontColorTableCell;
import com.server.app.event.handler.TableRowCopyKeyEventHandler;
import com.server.app.fxml.loader.ActiveServersStageLoader;
import com.server.app.fxml.loader.CollectionFormStageLoader;
import com.server.app.fxml.loader.ExportCollectionStageLoader;
import com.server.app.fxml.loader.ImportCollectionStageLoader;
import com.server.app.fxml.loader.ServerFormStageLoader;
import com.server.app.fxml.loader.SettingsStageLoader;
import com.server.app.fxml.loader.StageLoader;
import com.server.app.model.data.Collection;
import com.server.app.model.data.Server;
import com.server.app.model.view.CollectionTableData;
import com.server.app.model.view.ServerTableData;
import com.server.app.server.ServerManager;
import com.server.app.service.CollectionService;
import com.server.app.service.ServerService;
import com.server.app.service.Service;
import com.server.app.util.AppUtil;
import com.server.app.util.CustomKeyCode;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.server.app.constants.ApplicationConstants.ACTIVE;
import static com.server.app.constants.ApplicationConstants.APP_COLLECTION_FORM_TITLE;
import static com.server.app.constants.ApplicationConstants.APP_EXPORT_COLLECTION_TITLE;
import static com.server.app.constants.ApplicationConstants.APP_IMPORT_COLLECTION_TITLE;
import static com.server.app.constants.ApplicationConstants.APP_SERVER_FORM_EDIT_TITLE;
import static com.server.app.constants.ApplicationConstants.APP_SERVER_FORM_TITLE;
import static com.server.app.constants.ApplicationConstants.APP_SETTING_TITLE;
import static com.server.app.constants.ApplicationConstants.DELETE_BUTTON_IMAGE_PATH;
import static com.server.app.constants.ApplicationConstants.EDIT_COLLECTION_FORM_TITLE;
import static com.server.app.constants.ApplicationConstants.INACTIVE;
import static com.server.app.util.AppUtil.bringExistingActiveWindowToFrontOrElse;
import static com.server.app.util.AppUtil.triggerConfirmationPrompt;
import static com.server.app.util.AppUtil.triggerErrorAlert;
import static javafx.beans.binding.Bindings.isEmpty;
import static javafx.beans.binding.Bindings.size;
import static javafx.beans.binding.Bindings.when;

/**
 * @author Kazi Tanvir Azad
 */
public class MainAppController implements Initializable {
    private static final Logger log = LogManager.getLogger(MainAppController.class);
    private final CollectionService collectionService;
    private final ServerService serverService;

    public MainAppController() {
        this.collectionService = Service.INSTANCE.getCollectionService();
        this.serverService = Service.INSTANCE.getServerService();
    }

    @FXML
    private MenuItem importCollectionMenuItem;
    @FXML
    private MenuItem exportCollectionMenuItem;
    @FXML
    private MenuItem settingsMenuItem;
    @FXML
    private MenuItem createCollectionMenuItem;
    @FXML
    private MenuItem closeAppMenuItem;
    @FXML
    private MenuItem activeServersMenuItem;
    @FXML
    private HBox activeServerHBox;
    @FXML
    private Text activeServerCountText;
    @FXML
    private TableView<ServerTableData> serverTable;
    @FXML
    private TableColumn<ServerTableData, String> serverNameCol;
    @FXML
    private TableColumn<ServerTableData, String> serverUrlCol;
    @FXML
    private TableColumn<ServerTableData, String> serverMethodCol;
    @FXML
    private TableColumn<ServerTableData, String> serverStatusCodeCol;
    @FXML
    private TableColumn<ServerTableData, String> serverDelayCol;
    @FXML
    private TableColumn<ServerTableData, String> serverPortCol;
    @FXML
    private TableColumn<ServerTableData, String> serverStatusCol;
    @FXML
    private TableColumn<ServerTableData, StackPane> serverDeleteCol;
    @FXML
    private TableView<CollectionTableData> collectionTable;
    @FXML
    private TableColumn<CollectionTableData, String> collectionNameCol;
    @FXML
    private TableColumn<CollectionTableData, StackPane> collectionDeleteCol;

    @FXML
    private void addCollectionEvent(ActionEvent event) {
        createCollection(event);
    }

    @FXML
    private void createServerEvent(ActionEvent event) {
        if (CollectionUtils.isEmpty(collectionTable.getItems())) {
            triggerErrorAlert("Collections Empty!", "Add Collection to create Server");
        } else {
            // create new server
            bringExistingActiveWindowToFrontOrElse(() -> {
                StageLoader<ServerFormController> stageLoader;
                if (!collectionTable.getSelectionModel().isEmpty()) {
                    Collection selectedCollection = collectionTable.getSelectionModel().getSelectedItem()
                            .getCollectionObjectProperty();
                    stageLoader = new ServerFormStageLoader(selectedCollection);
                } else {
                    stageLoader = new ServerFormStageLoader();
                }
                stageLoader.loadStage();
                ServerFormController controller = stageLoader.getController();
                controller.getServerInput()
                        .ifPresent(server -> {
                            Optional<Server> newServerOptional = serverService.createServer(server);
                            newServerOptional.ifPresent(srvr -> {
                                selectCollection(srvr.getCollectionId());
                                selectServer(srvr);
                            });
                        });
            }, APP_SERVER_FORM_TITLE, APP_SERVER_FORM_EDIT_TITLE);
        }
    }

    @FXML
    private void startServerEvent(ActionEvent event) {
        ServerTableData selectedServerTableData = serverTable.getSelectionModel().getSelectedItem();
        if (Objects.isNull(selectedServerTableData)) {
            triggerErrorAlert("No server is selected", "Select a server to start");
            return;
        }
        Server selectedServer = selectedServerTableData.getServerObjectProperty();
        if (Objects.isNull(selectedServer)) {
            triggerErrorAlert("Invalid server selection", "Select a server to start");
            return;
        }
        ServerManager.INSTANCE.startServer(selectedServer, false);
        selectCollection(selectedServer.getCollectionId());
        selectServer(selectedServer);
    }

    @FXML
    private void stopServerEvent(ActionEvent event) {
        ServerTableData selectedServerTableData = serverTable.getSelectionModel().getSelectedItem();
        if (Objects.isNull(selectedServerTableData)) {
            triggerErrorAlert("No server is selected", "Select a server to stop");
            return;
        }
        Server selectedServer = selectedServerTableData.getServerObjectProperty();
        ServerManager.INSTANCE.stopServer(selectedServer, false);
        if (Objects.nonNull(selectedServer)) {
            selectCollection(selectedServer.getCollectionId());
            selectServer(selectedServer);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // set action and general UI click events
        setMenuItemEvents();
        // initializing Collections View Table
        initializeCollectionTable();
        // initializing Servers View Table
        initializeServerTable();
        // setting active server view
        initializeActiveServerView();
    }

    private void setMenuItemEvents() {
        // setting File>Close MenuItem action event
        closeAppMenuItem.setOnAction(AppUtil::exitApplication);
        // setting Options>Create Collection MenuItem action event
        createCollectionMenuItem.setOnAction(this::createCollection);
        // setting Options>Active Servers MenuItem action event
        activeServersMenuItem.setOnAction(event -> {
            if (ServerManager.INSTANCE.hasAnyActiveServer()) {
                initializeActiveServerManager(event);
            }
        });
        // setting Options>Active Servers MenuItem disable property binding
        activeServersMenuItem.disableProperty().bind(isEmpty(ServerManager.INSTANCE.getActiveServerIds()));
        // setting File>Import Collections MenuItem action event
        importCollectionMenuItem.setOnAction(event ->
                bringExistingActiveWindowToFrontOrElse(() -> {
                    // get currently selected collection
                    Collection selectedCollection = null;
                    CollectionTableData selectedCollectionTableData = collectionTable.getSelectionModel().getSelectedItem();
                    // get currently selected collection
                    Server selectedServer = null;
                    ServerTableData serverTableData = serverTable.getSelectionModel().getSelectedItem();
                    if (Objects.nonNull(selectedCollectionTableData) &&
                            Objects.nonNull(selectedCollectionTableData.getCollectionObjectProperty())) {
                        selectedCollection = selectedCollectionTableData.getCollectionObjectProperty();
                    }
                    if (Objects.nonNull(serverTableData) &&
                            Objects.nonNull(serverTableData.getServerObjectProperty())) {
                        selectedServer = serverTableData.getServerObjectProperty();
                    }
                    StageLoader<ImportCollectionController> importCollectionStageLoader = new ImportCollectionStageLoader();
                    importCollectionStageLoader.loadStage();
                    // refresh collection table
                    collectionTable.setItems(FXCollections.observableList(collectionService.getCollectionTableData()));
                    if (Objects.nonNull(selectedCollection)) {
                        selectCollection(selectedCollection);
                        if (Objects.nonNull(selectedServer)) {
                            selectServer(selectedServer);
                        }
                    }
                }, APP_IMPORT_COLLECTION_TITLE));
        // setting File>Export Collections MenuItem action event
        exportCollectionMenuItem.setOnAction(event -> {
            if (CollectionUtils.isEmpty(collectionTable.getItems())) {
                triggerErrorAlert("Collections Empty!", "Add Collection before export");
            } else {
                bringExistingActiveWindowToFrontOrElse(() -> {
                    StageLoader<ExportCollectionController> exportCollectionStageLoader = new ExportCollectionStageLoader();
                    exportCollectionStageLoader.loadStage();
                }, APP_EXPORT_COLLECTION_TITLE);
            }
        });
        // setting File>Settings MenuItem action event
        settingsMenuItem.setOnAction(event ->
                bringExistingActiveWindowToFrontOrElse(() -> {
                    StageLoader<SettingsController> settingsStageLoader = new SettingsStageLoader();
                    settingsStageLoader.loadStage();
                }, APP_SETTING_TITLE));
    }

    private void initializeActiveServerView() {
        // setting active servers count text property binding
        activeServerCountText.textProperty().bind(size(ServerManager.INSTANCE.getActiveServerIds()).asString());
        // setting active servers background color property binding
        activeServerHBox.backgroundProperty().bind(when(isEmpty(ServerManager.INSTANCE.getActiveServerIds()))
                .then(Background.fill(Paint.valueOf("Grey"))).otherwise(Background.fill(Paint.valueOf("Green"))));
        // setting active servers cursor property binding
        activeServerHBox.cursorProperty().bind(when(isEmpty(ServerManager.INSTANCE.getActiveServerIds()))
                .then(Cursor.DEFAULT).otherwise(Cursor.HAND));

        // setting active servers on click event
        activeServerHBox.setOnMouseClicked(event -> {
            if (ServerManager.INSTANCE.hasAnyActiveServer()) {
                initializeActiveServerManager(event);
            }
        });
    }

    private void initializeCollectionTable() {
        // Allowing only single row selection in collectionTable
        collectionTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        // // Adding key press event handler for collectionTable row
        collectionTable.setOnKeyPressed(keyEvent -> {
            // Adding 'Copy' event handler for collectionTable row
            if (CustomKeyCode.INSTANCE.getCopyKeycodeCombination().match(keyEvent)) {
                EventHandler<KeyEvent> keyEventHandler = new TableRowCopyKeyEventHandler();
                keyEventHandler.handle(keyEvent);
            }
            // remove collection table row selection for Escape key press event
            if (CustomKeyCode.INSTANCE.getEscapeKeycode().equals(keyEvent.getCode())) {
                // clear server table items and also clear collection table row selection
                clearCollectionTableSelection();
            }
            // Edit collection for 'Enter' key press event
            if (CustomKeyCode.INSTANCE.getEnterKeycode().equals(keyEvent.getCode())) {
                modifyCollection();
            }
            // Delete collection for 'Delete' key press event
            if (CustomKeyCode.INSTANCE.getDeleteKeycode().equals(keyEvent.getCode())) {
                Optional.ofNullable(collectionTable.getSelectionModel())
                        .filter(collectionTableSelectionModel ->
                                !collectionTableSelectionModel.isEmpty())
                        .filter(collectionTableSelectionModel -> {
                            ButtonType promptResult = triggerConfirmationPrompt("Delete Collection?",
                                    """
                                            All the server associated with this collection will get deleted.
                                            Continue to delete the collection""");
                            return ButtonType.OK == promptResult;
                        })
                        .map(TableView.TableViewSelectionModel::getSelectedItem)
                        .map(CollectionTableData::getCollectionObjectProperty)
                        .map(collection -> collectionService.getCollectionById(collection.getCollectionId()))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .ifPresent(new DeleteCollectionConsumer());
            }
        });
        // adding existing data
        collectionTable.setItems(FXCollections.observableList(collectionService.getCollectionTableData()));
        // setting row factory for row related interactions
        collectionTable.setRowFactory(tableView -> {
            TableRow<CollectionTableData> tableRow = new TableRow<>();
            tableRow.setOnMouseClicked(event -> {
                if (!tableRow.isEmpty() && event.getClickCount() == 2) {
                    // handle collection edit on row double click
                    modifyCollection();
                }
            });
            return tableRow;
        });
        // setting row selection listener in collectionTable
        collectionTable.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (null != newValue) {
                        handleCollectionTableOnRowSelection();
                    }
                });

        // setting collection table cell value factory for collectionName column
        collectionNameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCollectionObjectProperty().getCollectionName()));
        // setting cell factory for cell(column/row) related interactions for 'collectionName' column
        collectionNameCol.setCellFactory(tableColumn -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
            }
        });

        // setting cell factory for cell(column/row) related interactions for 'collectionDelete' column
        collectionDeleteCol.setCellFactory(tableColumn -> {
            ButtonImageViewTableCell<CollectionTableData, StackPane> deleteCollectionTableCell =
                    new ButtonImageViewTableCell<>(DELETE_BUTTON_IMAGE_PATH);
            deleteCollectionTableCell.setCustomMouseEvent(event -> {
                ButtonType promptResult = triggerConfirmationPrompt("Delete Collection?",
                        """
                                All the server associated with this collection will get deleted.
                                Continue to delete the collection""");
                if (ButtonType.OK == promptResult && !deleteCollectionTableCell.isEmpty()) {
                    // Handle click event to delete the selected Collection
                    Optional.ofNullable(deleteCollectionTableCell.getTableRow())
                            .map(TableRow::getItem)
                            .map(CollectionTableData::getCollectionObjectProperty)
                            .stream()
                            .map(collection -> collectionService.getCollectionById(collection.getCollectionId()))
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .findFirst()
                            .ifPresent(new DeleteCollectionConsumer());
                }
            });
            return deleteCollectionTableCell;
        });
    }

    private void initializeServerTable() {
        // setting the server table columns responsive
        serverTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        // Allowing only single row selection in serverTable
        serverTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        // Adding key press event handler for serverTable row
        serverTable.setOnKeyPressed(keyEvent -> {
            // Adding 'Copy' event handler for serverTable row
            if (CustomKeyCode.INSTANCE.getCopyKeycodeCombination().match(keyEvent)) {
                EventHandler<KeyEvent> keyEventHandler = new TableRowCopyKeyEventHandler();
                keyEventHandler.handle(keyEvent);
            }
            // Remove server table row selection for 'Escape' key press event
            if (CustomKeyCode.INSTANCE.getEscapeKeycode().equals(keyEvent.getCode())) {
                Optional.ofNullable(serverTable.getSelectionModel())
                        .ifPresent(serverTableSelectionModel -> {
                            if (Objects.nonNull(serverTableSelectionModel.getSelectedItem())) {
                                serverTableSelectionModel.clearSelection();
                            } else {
                                // clear server table items and also clear collection table row selection
                                clearCollectionTableSelection();
                            }
                        });
            }
            // Edit server for 'Enter' key press event
            if (CustomKeyCode.INSTANCE.getEnterKeycode().equals(keyEvent.getCode())) {
                editServer();
            }
            // Delete server for 'Delete' key press event
            if (CustomKeyCode.INSTANCE.getDeleteKeycode().equals(keyEvent.getCode())) {
                Optional.ofNullable(serverTable.getSelectionModel())
                        .filter(serverTableSelectionModel ->
                                !serverTableSelectionModel.isEmpty())
                        .filter(serverTableSelectionModel -> {
                            ButtonType promptResult = triggerConfirmationPrompt("Delete Server?", "Continue to delete the server");
                            return ButtonType.OK == promptResult;
                        })
                        .map(TableView.TableViewSelectionModel::getSelectedItem)
                        .map(ServerTableData::getServerObjectProperty)
                        .map(server -> serverService.getServerById(server.getServerId()))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .stream()
                        .map(new DeleteServerFunction())
                        .findFirst()
                        .ifPresent(isServerDeleted -> {
                            if (!isServerDeleted) {
                                triggerErrorAlert("Server deletion failed!", """
                                        Something went wrong. Try again later.""");
                            }
                        });
            }
        });
        // setting row factory for row related interactions
        serverTable.setRowFactory(tableView -> {
            TableRow<ServerTableData> tableRow = new TableRow<>();
            tableRow.setOnMouseClicked(event -> {
                if (!tableRow.isEmpty() && event.getClickCount() == 2) {
                    // Edit server
                    editServer();
                }
            });
            return tableRow;
        });

        // setting cell factory for cell(column/row) related interactions for 'serverDelete' column
        serverDeleteCol.setCellFactory(tableColumn -> {
            ButtonImageViewTableCell<ServerTableData, StackPane> deleteServerTableCell =
                    new ButtonImageViewTableCell<>(DELETE_BUTTON_IMAGE_PATH);
            deleteServerTableCell.setCustomMouseEvent(event -> {
                ButtonType promptResult = triggerConfirmationPrompt("Delete Server?", "Continue to delete the server");
                if (ButtonType.OK == promptResult && !deleteServerTableCell.isEmpty()) {
                    // Handle click event to delete the selected Server
                    Optional.ofNullable(deleteServerTableCell.getTableRow())
                            .map(TableRow::getItem)
                            .map(ServerTableData::getServerObjectProperty)
                            .stream()
                            .map(server -> serverService.getServerById(server.getServerId()))
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .map(new DeleteServerFunction())
                            .findFirst()
                            .ifPresent(isServerDeleted -> {
                                if (!isServerDeleted) {
                                    triggerErrorAlert("Server deletion failed!", """
                                            Something went wrong. Try again later.""");
                                }
                            });
                }
            });
            return deleteServerTableCell;
        });
        // setting cell factory for cell(column/row) related interactions for 'serverStatus' column
        serverStatusCol.setCellFactory(tableColumn -> new ServerTableStatusFontColorTableCell());

        // setting server table cell value factory for all columns
        serverStatusCol.setCellValueFactory(cellData -> {
            String serverId = cellData.getValue().getServerObjectProperty().getServerId();
            String serverStatus = ServerManager.INSTANCE.isServerActive(serverId) ? ACTIVE : INACTIVE;
            return new SimpleStringProperty(serverStatus);
        });
        serverNameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getServerObjectProperty().getServerName()));
        serverUrlCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getServerObjectProperty().getUrlEndpoint()));
        serverMethodCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getServerObjectProperty().getMethod().name()));
        serverStatusCodeCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getServerObjectProperty().getResponseCode())));
        serverDelayCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getServerObjectProperty().getDelay())));
        serverPortCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getServerObjectProperty().getPort())));
    }

    private void handleCollectionTableOnRowSelection() {
        if (!collectionTable.getSelectionModel().isEmpty()) {
            CollectionTableData selectedCollectionTableData = collectionTable.getSelectionModel().getSelectedItem();
            if (null != selectedCollectionTableData && null != selectedCollectionTableData.getCollectionObjectProperty()) {
                Collection selectedCollection = selectedCollectionTableData.getCollectionObjectProperty();
                List<ServerTableData> serverTableDataList = serverService.getServerTableDataList(selectedCollection.getCollectionId());
                if (CollectionUtils.isNotEmpty(serverTableDataList))
                    serverTable.setItems(FXCollections.observableList(serverTableDataList));
                else serverTable.setItems(FXCollections.emptyObservableList());
            }
        }
    }

    private void selectCollection(Collection collection) {
        selectCollection(collection.getCollectionId());
    }

    private void selectCollection(String collectionId) {
        collectionTable.getItems()
                .stream()
                .filter(collectionTableData ->
                        collectionTableData.getCollectionObjectProperty().getCollectionId()
                                .equals(collectionId))
                .findFirst()
                .ifPresent(collectionTableData -> {
                    /* removing selection - In case of selecting same row of the table, the ChangeListener
                     * doesn't get executed, hence the server table also don't get refreshed with the new or
                     * updated data. */
                    collectionTable.getSelectionModel().clearSelection();
                    collectionTable.getSelectionModel().select(collectionTableData);
                });
    }

    private void selectServer(Server server) {
        serverTable.getItems()
                .stream()
                .filter(serverTableData ->
                        null != serverTableData.getServerObjectProperty())
                .filter(serverTableData ->
                        serverTableData.getServerObjectProperty().getServerId().equals(server.getServerId()))
                .findFirst()
                .ifPresent(serverTableData -> {
                    serverTable.getSelectionModel().clearSelection();
                    serverTable.getSelectionModel().select(serverTableData);
                });
    }

    private void createCollection(ActionEvent event) {
        openCreateModifyCollectionWindow(event, false);
    }

    private void modifyCollection() {
        Optional.ofNullable(collectionTable.getSelectionModel())
                .filter(collectionTableSelectionModel ->
                        !collectionTableSelectionModel.isEmpty())
                .ifPresent(collectionTableSelectionModel -> {
                    openCreateModifyCollectionWindow(null, true);
                });
    }

    private void openCreateModifyCollectionWindow(ActionEvent event, boolean doEdit) {
        bringExistingActiveWindowToFrontOrElse(() -> {
            StageLoader<CollectionFormController> stageLoader;
            Collection selectedCollection;
            if (doEdit && !collectionTable.getSelectionModel().isEmpty()) {
                selectedCollection = collectionTable.getSelectionModel().getSelectedItem().getCollectionObjectProperty();
                stageLoader = new CollectionFormStageLoader(true, selectedCollection.getCollectionName());
            } else {
                selectedCollection = null;
                stageLoader = new CollectionFormStageLoader(false, null);
            }
            stageLoader.loadStage();
            CollectionFormController controller = stageLoader.getController();
            controller.getCollectionNameInput()
                    .ifPresent(collectionNameInput ->
                            collectionTable.getItems()
                                    .stream()
                                    .map(CollectionTableData::getCollectionObjectProperty)
                                    .filter(collection ->
                                            collection.getCollectionName().equalsIgnoreCase(collectionNameInput))
                                    .findFirst()
                                    .ifPresentOrElse(collection ->
                                                    triggerErrorAlert("Duplicate Collection!",
                                                            "Collection name must be unique."),
                                            () -> {
                                                // persist collection
                                                Optional<Collection> collectionOptional;
                                                if (doEdit && null != selectedCollection) {
                                                    collectionOptional =
                                                            collectionService.editCollection(selectedCollection.getCollectionId(),
                                                                    collectionNameInput);
                                                } else {
                                                    collectionOptional = collectionService.createCollection(collectionNameInput);
                                                }
                                                // update collection table view
                                                collectionOptional.ifPresent(collection -> {
                                                    List<CollectionTableData> newCollectionTableDataList =
                                                            collectionService.getCollectionTableData();
                                                    collectionTable.setItems(FXCollections.observableList(newCollectionTableDataList));
                                                    // select the newly created collection in the collection table
                                                    selectCollection(collection);
                                                });
                                            }));
        }, APP_COLLECTION_FORM_TITLE, EDIT_COLLECTION_FORM_TITLE);
    }

    private void initializeActiveServerManager(Event event) {
        // get currently selected collection
        Collection selectedCollection = null;
        CollectionTableData selectedCollectionTableData = collectionTable.getSelectionModel().getSelectedItem();
        // get currently selected collection
        Server selectedServer = null;
        ServerTableData serverTableData = serverTable.getSelectionModel().getSelectedItem();
        if (Objects.nonNull(selectedCollectionTableData) &&
                Objects.nonNull(selectedCollectionTableData.getCollectionObjectProperty())) {
            selectedCollection = selectedCollectionTableData.getCollectionObjectProperty();
        }
        if (Objects.nonNull(serverTableData) &&
                Objects.nonNull(serverTableData.getServerObjectProperty())) {
            selectedServer = serverTableData.getServerObjectProperty();
        }
        StageLoader<ActiveServersController> stageLoader = new ActiveServersStageLoader();
        stageLoader.loadStage();
        // refresh collection table
        collectionTable.setItems(FXCollections.observableList(collectionService.getCollectionTableData()));
        if (Objects.nonNull(selectedCollection)) {
            selectCollection(selectedCollection);
            if (Objects.nonNull(selectedServer)) {
                selectServer(selectedServer);
            }
        }
    }

    private void clearCollectionTableSelection() {
        Optional.ofNullable(collectionTable.getSelectionModel())
                .ifPresent(collectionTableSelectionModel -> {
                    // clear server table items
                    serverTable.getItems().clear();
                    // clear collection table row selection
                    collectionTableSelectionModel.clearSelection();
                });
    }

    private void editServer() {
        Optional.ofNullable(serverTable.getSelectionModel())
                .filter(serverTableSelectionModel ->
                        !serverTableSelectionModel.isEmpty())
                .ifPresent(serverTableSelectionModel -> {
                    openServerEditWindow();
                });
    }

    private void openServerEditWindow() {
        bringExistingActiveWindowToFrontOrElse(() -> {
            if (!serverTable.getSelectionModel().isEmpty() && !collectionTable.getSelectionModel().isEmpty()) {
                Collection selectedCollection = collectionTable.getSelectionModel().getSelectedItem()
                        .getCollectionObjectProperty();
                Server selectedServer = serverTable.getSelectionModel().getSelectedItem()
                        .getServerObjectProperty();
                StageLoader<ServerFormController> stageLoader =
                        new ServerFormStageLoader(selectedServer.getServerId(), selectedCollection);
                stageLoader.loadStage();
                ServerFormController controller = stageLoader.getController();
                controller.getServerInput()
                        .ifPresent(server -> {
                            String serverId = selectedServer.getServerId();
                            // stop the server if it's already running before performing update
                            if (ServerManager.INSTANCE.isServerActive(serverId)) {
                                ServerManager.INSTANCE.stopServer(selectedServer, true);
                            }
                            server.setServerId(serverId);
                            Optional<Server> updatedServerOptional = serverService.updateServer(server);
                            updatedServerOptional.ifPresent(srvr -> {
                                selectCollection(srvr.getCollectionId());
                                selectServer(srvr);
                            });
                        });
            }
        }, APP_SERVER_FORM_TITLE, APP_SERVER_FORM_EDIT_TITLE);
    }

    private class DeleteCollectionConsumer implements Consumer<Collection> {
        @Override
        public void accept(Collection collection) {
            serverService.getServersByCollection(collection.getCollectionId())
                    .filter(Objects::nonNull)
                    .forEach(server -> {
                        if (ServerManager.INSTANCE.isServerActive(server.getServerId())) {
                            ServerManager.INSTANCE.stopServer(server, true);
                        }
                        serverService.deleteServerById(server.getServerId());
                    });
            if (collectionService.deleteCollectionById(collection.getCollectionId())) {
                collectionTable.setItems(FXCollections.observableList(collectionService.getCollectionTableData()));
                serverTable.getItems().clear();
            }
        }
    }

    private class DeleteServerFunction implements Function<Server, Boolean> {
        @Override
        public Boolean apply(Server server) {
            if (ServerManager.INSTANCE.isServerActive(server.getServerId())) {
                ServerManager.INSTANCE.stopServer(server, true);
            }
            if (serverService.deleteServerById(server.getServerId())) {
                serverTable.setItems(FXCollections.observableList(serverService
                        .getServerTableDataList(server.getCollectionId())));
                return true;
            }
            return false;
        }
    }
}
