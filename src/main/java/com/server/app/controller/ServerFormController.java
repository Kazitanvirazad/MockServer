package com.server.app.controller;

import com.server.app.constants.Method;
import com.server.app.control.ButtonImageViewTableCell;
import com.server.app.event.handler.TableRowCopyKeyEventHandler;
import com.server.app.fxml.loader.CookieFormStageLoader;
import com.server.app.fxml.loader.HeaderFormStageLoader;
import com.server.app.fxml.loader.StageLoader;
import com.server.app.model.data.Collection;
import com.server.app.model.data.Cookie;
import com.server.app.model.data.Header;
import com.server.app.model.data.Server;
import com.server.app.model.view.CookieTableData;
import com.server.app.model.view.HeaderTableData;
import com.server.app.service.CollectionService;
import com.server.app.service.ServerService;
import com.server.app.service.Service;
import com.server.app.util.CustomKeyCode;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static com.server.app.constants.ApplicationConstants.APP_COOKIE_FORM_TITLE;
import static com.server.app.constants.ApplicationConstants.APP_HEADER_FORM_TITLE;
import static com.server.app.constants.ApplicationConstants.DEFAULT_PATH;
import static com.server.app.constants.ApplicationConstants.DEFAULT_RESPONSE_CODE;
import static com.server.app.constants.ApplicationConstants.DELETE_BUTTON_IMAGE_PATH;
import static com.server.app.constants.ApplicationConstants.JAVA_CROSS_PLATFORM_USER_DIRECTORY_PATH;
import static com.server.app.constants.ApplicationConstants.RESPONSE_BINARY_FILE_SELECTOR_TITLE;
import static com.server.app.util.AppUtil.RESPONSE_CODE_RANGE;
import static com.server.app.util.AppUtil.SERVER_PORT_RANGE;
import static com.server.app.util.AppUtil.bringExistingActiveWindowToFrontOrElse;
import static com.server.app.util.AppUtil.closeWindowButtonEvent;
import static com.server.app.util.AppUtil.triggerErrorAlert;
import static com.server.app.util.AppUtil.triggerInfoAlert;

/**
 * @author Kazi Tanvir Azad
 */
public class ServerFormController {
    private static final Logger log = LogManager.getLogger(ServerFormController.class);
    private final CollectionService collectionService;
    private final ServerService serverService;

    public ServerFormController() {
        this.collectionService = Service.INSTANCE.getCollectionService();
        this.serverService = Service.INSTANCE.getServerService();
    }

    @FXML
    public ImageView endpointInfoImageView;
    @FXML
    private TableView<HeaderTableData> headerTable;
    @FXML
    private TableColumn<HeaderTableData, String> headerKeyColumn;
    @FXML
    private TableColumn<HeaderTableData, String> headerValueColumn;
    @FXML
    private TableColumn<HeaderTableData, StackPane> headerDeleteColumn;
    @FXML
    private TableView<CookieTableData> cookieTable;
    @FXML
    private TableColumn<CookieTableData, String> cookieColumn;
    @FXML
    private TableColumn<CookieTableData, StackPane> cookieDeleteColumn;
    @FXML
    private Button saveServerButton;
    @FXML
    private ChoiceBox<Collection> collectionChoice;
    @FXML
    private Text mockServerFormTitle;
    @FXML
    private TextField serverNameInput;
    @FXML
    private TextField endpointInput;
    @FXML
    private TextField responseCodeInput;
    @FXML
    private TextField portInput;
    @FXML
    private ChoiceBox<Method> methodChoice;
    @FXML
    private TextField delayInput;
    @FXML
    private TextArea responseDataInput;
    @FXML
    private Text binaryResponseFilePath;
    @FXML
    private CheckBox defaultResponseBinaryCheck;

    private Collection defaultSelectedCollection;
    private Server serverInput;
    private String serverId;
    private String responseBinaryPath;

    @FXML
    private void handleCreateServerButtonEvent(ActionEvent event) {
        serverInput = new Server();
        if (StringUtils.isBlank(portInput.getText())) {
            serverInput = null;
            triggerErrorAlert("Enter server port", "Server port number is mandatory");
            return;
        }
        if (!NumberUtils.isDigits(portInput.getText())) {
            serverInput = null;
            triggerErrorAlert("Server port is not a number",
                    "Server port should be in number format");
            return;
        } else {
            int port = Integer.parseInt(portInput.getText());
            if (!SERVER_PORT_RANGE.contains(port)) {
                serverInput = null;
                triggerErrorAlert("Server port out of range",
                        "Server port range should be between 1 to 65535");
                return;
            }
        }
        if (StringUtils.isBlank(serverNameInput.getText())) {
            serverInput = null;
            triggerErrorAlert("Enter server name", "Server port name is mandatory");
            return;
        }
        serverInput.setServerName(serverNameInput.getText());
        serverInput.setPort(Integer.parseInt(portInput.getText()));
        if (StringUtils.isNotBlank(endpointInput.getText())) {
            String endpointInputText = endpointInput.getText();
            if (!endpointInputText.startsWith(DEFAULT_PATH)) {
                endpointInputText = DEFAULT_PATH + endpointInputText;
            }
            serverInput.setUrlEndpoint(endpointInputText);
        }
        if (!methodChoice.getSelectionModel().isEmpty()) {
            serverInput.setMethod(methodChoice.getValue());
        }
        if (StringUtils.isNotBlank(delayInput.getText())) {
            if (!NumberUtils.isDigits(delayInput.getText())) {
                serverInput = null;
                triggerErrorAlert("Invalid Delay input!",
                        "Delay must be valid and should be in number format");
                return;
            }
            long delayInputLong = Long.parseLong(delayInput.getText());
            if (delayInputLong < 0) {
                triggerErrorAlert("Invalid Delay input!",
                        "Delay must be equal to or greater than 0");
                return;
            }
            serverInput.setDelay(delayInputLong);
        }
        if (StringUtils.isNotBlank(responseCodeInput.getText())) {
            if (!NumberUtils.isDigits(responseCodeInput.getText())) {
                serverInput = null;
                triggerErrorAlert("Invalid Response code input!",
                        "Response code must be valid and should be in number format");
                return;
            }
            int responseCode = Integer.parseInt(responseCodeInput.getText());
            if (!RESPONSE_CODE_RANGE.contains(responseCode)) {
                serverInput = null;
                triggerErrorAlert("Invalid Http Response code!",
                        "Response code must be between 100 to 599");
                return;
            }
            serverInput.setResponseCode(responseCode);
        } else {
            serverInput.setResponseCode(DEFAULT_RESPONSE_CODE);
        }
        if (StringUtils.isNotBlank(responseDataInput.getText())) {
            serverInput.setResponseData(responseDataInput.getText());
        }
        if (defaultResponseBinaryCheck.isSelected() && StringUtils.isBlank(responseBinaryPath)) {
            serverInput = null;
            triggerErrorAlert("Invalid Binary Response path!",
                    """
                            Binary Response file must be added if it's set to default.
                            Attach file to continue.""");
            return;
        }
        if (StringUtils.isNotBlank(responseBinaryPath)) {
            serverInput.setResponseBinaryPath(responseBinaryPath);
        }
        serverInput.setDefaultResponseBinary(defaultResponseBinaryCheck.isSelected());
        serverInput.setCollectionId(collectionChoice.getValue().getCollectionId());
        final List<Header> headers = new ArrayList<>();
        headerTable.getItems().forEach(headerTableData ->
                headers.add(headerTableData.getHeaderSimpleObjectProperty()));
        if (CollectionUtils.isNotEmpty(headers)) {
            serverInput.setHeaders(headers);
        }
        final List<Cookie> cookies = new ArrayList<>();
        cookieTable.getItems().forEach(cookieTableData ->
                cookies.add(cookieTableData.getCookieSimpleObjectProperty()));
        if (CollectionUtils.isNotEmpty(cookies)) {
            serverInput.setCookies(cookies);
        }
        closeWindowButtonEvent(event);
    }

    @FXML
    private void handleAttachBinaryButtonEvent(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(RESPONSE_BINARY_FILE_SELECTOR_TITLE);
        fileChooser.setInitialDirectory(new File(JAVA_CROSS_PLATFORM_USER_DIRECTORY_PATH));
        File newSelectedFile = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());
        if (FileUtils.isRegularFile(newSelectedFile)) {
            responseBinaryPath = newSelectedFile.getAbsolutePath();
            viewSelectedFile();
        }
    }

    @FXML
    private void handleAddHeaderButtonEvent(ActionEvent event) {
        bringExistingActiveWindowToFrontOrElse(() -> {
            StageLoader<HeaderFormController> stageLoader = new HeaderFormStageLoader();
            stageLoader.loadStage();
            HeaderFormController controller = stageLoader.getController();
            controller.getHeaderInput()
                    .ifPresent(header -> {
                        if (CollectionUtils.isEmpty(headerTable.getItems())) {
                            List<HeaderTableData> headerTableDataList = new ArrayList<>();
                            headerTableDataList.add(new HeaderTableData(new SimpleObjectProperty<>(header)));
                            headerTable.setItems(FXCollections.observableList(headerTableDataList));
                        } else {
                            headerTable.getItems()
                                    .add(new HeaderTableData(new SimpleObjectProperty<>(header)));
                        }
                    });
        }, APP_HEADER_FORM_TITLE);
    }

    @FXML
    private void handleAddCookieButtonEvent(ActionEvent event) {
        bringExistingActiveWindowToFrontOrElse(() -> {
            StageLoader<CookieFormController> stageLoader = new CookieFormStageLoader();
            stageLoader.loadStage();
            CookieFormController controller = stageLoader.getController();
            controller.getCookieInput()
                    .ifPresent(cookie -> {
                        if (CollectionUtils.isEmpty(cookieTable.getItems())) {
                            List<CookieTableData> cookieTableDataList = new ArrayList<>();
                            cookieTableDataList.add(new CookieTableData(new SimpleObjectProperty<>(cookie)));
                            cookieTable.setItems(FXCollections.observableList(cookieTableDataList));
                        } else {
                            cookieTable.getItems()
                                    .add(new CookieTableData(new SimpleObjectProperty<>(cookie)));
                        }
                    });
        }, APP_COOKIE_FORM_TITLE);
    }

    public void initialize(URL location, ResourceBundle resources) {
        // Setting icons of the stage
        setIconsAndIconEvents();
        // Setting Method choice data
        initializeMethodChoice();
        // Setting Collection choice data
        initializeCollectionChoice();
        // initializing Header View Table
        initializeHeaderTable();
        // initializing Cookie View Table
        initializeCookieTable();
        // initializing server data in case of edit
        initializeEditServerFormData();
    }

    private void setIconsAndIconEvents() {
        // Setting icon for endpoint info popup imageview
        Image endpointInfoImage = new Image("/static/icons/info-20.png");
        endpointInfoImageView.setImage(endpointInfoImage);
        endpointInfoImageView.setCursor(Cursor.HAND);
        endpointInfoImageView.setOnMouseClicked(event -> {
            triggerInfoAlert("""
                            Follow the below rules for adding endpoint
                            to prevent unexpected response/behaviour""",
                    """
                            Rules for adding endpoint:
                            a. Default endpoint will be set to '/'
                            b. Do not add any hostname
                            c. Do not add any query params
                            d. All server will run on localhost
                            *The server implementation is based on Java
                             com.sun.net.httpserver.HttpServer
                            *This is a mock server and do not performs any logic
                             and validation. Hence, adding query params might
                             cause unexpected behaviour. While calling the endpoint
                             query params can be added because requests are mapped
                             based on the path. For more information read
                             Java HttpServer documentations""");
        });
    }

    private void initializeEditServerFormData() {
        // initializing Server form input data for edit
        if (null != serverId) {
            Optional<Server> serverOptional = serverService.getServerById(serverId);
            serverOptional.ifPresent(server -> {
                serverNameInput.setText(server.getServerName());
                endpointInput.setText(server.getUrlEndpoint());
                responseCodeInput.setText(String.valueOf(server.getResponseCode()));
                portInput.setText(String.valueOf(server.getPort()));
                methodChoice.setValue(server.getMethod());
                delayInput.setText(String.valueOf(server.getDelay()));
                responseDataInput.setText(server.getResponseData());
                String binaryPath = server.getResponseBinaryPath();
                if (StringUtils.isNotBlank(binaryPath)) {
                    binaryResponseFilePath.setText(binaryPath.length() >= 30 ?
                            binaryPath.substring(binaryPath.length() - 29) : binaryPath);
                    responseBinaryPath = binaryPath;
                }
                defaultResponseBinaryCheck.setSelected(server.isDefaultResponseBinary());
                if (CollectionUtils.isNotEmpty(server.getHeaders()))
                    headerTable.setItems(FXCollections.observableList(server.getHeaders()
                            .stream().map(header -> new HeaderTableData(new SimpleObjectProperty<>(header)))
                            .collect(Collectors.toList())));
                if (CollectionUtils.isNotEmpty(server.getCookies()))
                    cookieTable.setItems(FXCollections.observableList(server.getCookies()
                            .stream().map(cookie -> new CookieTableData(new SimpleObjectProperty<>(cookie)))
                            .collect(Collectors.toList())));
            });
        }
    }

    private void initializeMethodChoice() {
        // Setting Http method choice data
        methodChoice.setItems(FXCollections.observableArrayList(Method.values()));
        // setting default method selection
        methodChoice.setValue(Method.GET);
        // setting methodChoice interaction on selection
        methodChoice.getSelectionModel()
                .selectedIndexProperty()
                .addListener((observable, oldValue, newValue) ->
                        methodChoice.setValue(Method.values()[newValue.intValue()]));
    }

    private void initializeCollectionChoice() {
        List<Collection> collectionChoiceData = collectionService.getCollectionStream().collect(Collectors.toList());
        // Setting Collection choice data
        collectionChoice.setItems(FXCollections.observableList(collectionChoiceData));
        // setting collectionChoice interaction on selection
        collectionChoice.getSelectionModel()
                .selectedIndexProperty()
                .addListener((observable, oldValue, newValue) ->
                        collectionChoice.getItems().get(newValue.intValue()));
        // setting default Collection selection
        if (CollectionUtils.isNotEmpty(collectionChoiceData)) {
            if (null != defaultSelectedCollection) {
                collectionChoice.setValue(collectionChoiceData.stream()
                        .filter(cc -> cc.equals(defaultSelectedCollection))
                        .findFirst()
                        .orElseGet(collectionChoiceData::getFirst));
            } else {
                collectionChoice.setValue(collectionChoiceData.getFirst());
            }
        }
    }

    private void initializeHeaderTable() {
        // Allowing only single row selection in headerTable
        headerTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        // Adding key press event handler for headerTable row
        headerTable.setOnKeyPressed(keyEvent -> {
            // Adding 'Copy' event handler for headerTable row
            if (CustomKeyCode.INSTANCE.getCopyKeycodeCombination().match(keyEvent)) {
                EventHandler<KeyEvent> keyEventHandler = new TableRowCopyKeyEventHandler();
                keyEventHandler.handle(keyEvent);
            }
            // Delete header for 'Delete' key press event
            if (CustomKeyCode.INSTANCE.getDeleteKeycode().equals(keyEvent.getCode())) {
                Optional.ofNullable(headerTable.getSelectionModel())
                        .filter(headerTableSelectionModel ->
                                !headerTableSelectionModel.isEmpty())
                        .map(TableView.TableViewSelectionModel::getSelectedItem)
                        .map(HeaderTableData::getHeaderSimpleObjectProperty)
                        .ifPresent(headerToDelete -> {
                            List<HeaderTableData> headersData = headerTable.getItems()
                                    .stream()
                                    .map(HeaderTableData::getHeaderSimpleObjectProperty)
                                    .filter(header -> !header.equals(headerToDelete))
                                    .map(header -> new HeaderTableData(new SimpleObjectProperty<>(header)))
                                    .collect(Collectors.toList());
                            headerTable.setItems(FXCollections.observableList(headersData));
                        });
            }
        });

        // setting cell factory for cell(column/row) related interactions for 'headerDeleteColumn' column
        headerDeleteColumn.setCellFactory(tableColumn -> {
            ButtonImageViewTableCell<HeaderTableData, StackPane> deleteHeaderTableCell =
                    new ButtonImageViewTableCell<>(DELETE_BUTTON_IMAGE_PATH);
            deleteHeaderTableCell.setCustomMouseEvent(event -> {
                if (!deleteHeaderTableCell.isEmpty()) {
                    // Handle click event to delete the selected header
                    if (!deleteHeaderTableCell.getTableRow().isEmpty()) {
                        Header headerToDelete = deleteHeaderTableCell.getTableRow().getItem().getHeaderSimpleObjectProperty();
                        List<HeaderTableData> headersData = headerTable.getItems()
                                .stream()
                                .map(HeaderTableData::getHeaderSimpleObjectProperty)
                                .filter(header -> !header.equals(headerToDelete))
                                .map(header -> new HeaderTableData(new SimpleObjectProperty<>(header)))
                                .collect(Collectors.toList());
                        headerTable.setItems(FXCollections.observableList(headersData));
                    }
                }
            });
            return deleteHeaderTableCell;
        });

        // setting header table cell value factory for all columns
        headerKeyColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getHeaderSimpleObjectProperty().getKey()));
        headerValueColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getHeaderSimpleObjectProperty().getValue()));
    }

    private void initializeCookieTable() {
        // Allowing only single row selection in cookieTable
        cookieTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        // Adding key press event handler for cookieTable row
        cookieTable.setOnKeyPressed(keyEvent -> {
            // Adding 'Copy' event handler for cookieTable row
            if (CustomKeyCode.INSTANCE.getCopyKeycodeCombination().match(keyEvent)) {
                EventHandler<KeyEvent> keyEventHandler = new TableRowCopyKeyEventHandler();
                keyEventHandler.handle(keyEvent);
            }
            // Delete cookie for 'Delete' key press event
            if (CustomKeyCode.INSTANCE.getDeleteKeycode().equals(keyEvent.getCode())) {
                Optional.ofNullable(cookieTable.getSelectionModel())
                        .filter(cookieTableSelectionModel ->
                                !cookieTableSelectionModel.isEmpty())
                        .map(TableView.TableViewSelectionModel::getSelectedItem)
                        .map(CookieTableData::getCookieSimpleObjectProperty)
                        .ifPresent(cookieToDelete -> {
                            List<CookieTableData> cookieData = cookieTable.getItems()
                                    .stream()
                                    .map(CookieTableData::getCookieSimpleObjectProperty)
                                    .filter(cookie -> !cookieToDelete.equals(cookie))
                                    .map(cookie -> new CookieTableData(new SimpleObjectProperty<>(cookie)))
                                    .collect(Collectors.toList());
                            cookieTable.setItems(FXCollections.observableList(cookieData));
                        });
            }
        });

        // setting cell factory for cell(column/row) related interactions for 'cookieDeleteColumn' column
        cookieDeleteColumn.setCellFactory(tableColumn -> {
            ButtonImageViewTableCell<CookieTableData, StackPane> deleteCookieTableCell =
                    new ButtonImageViewTableCell<>(DELETE_BUTTON_IMAGE_PATH);
            deleteCookieTableCell.setCustomMouseEvent(event -> {
                if (!deleteCookieTableCell.isEmpty()) {
                    // Handle click event to delete the selected cookie
                    if (!deleteCookieTableCell.getTableRow().isEmpty()) {
                        Cookie cookieToDelete = deleteCookieTableCell.getTableRow().getItem().getCookieSimpleObjectProperty();
                        List<CookieTableData> cookieData = cookieTable.getItems()
                                .stream()
                                .map(CookieTableData::getCookieSimpleObjectProperty)
                                .filter(cookie -> !cookieToDelete.equals(cookie))
                                .map(cookie -> new CookieTableData(new SimpleObjectProperty<>(cookie)))
                                .collect(Collectors.toList());
                        cookieTable.setItems(FXCollections.observableList(cookieData));
                    }
                }
            });
            return deleteCookieTableCell;
        });

        // setting cookie table cell value factory for all columns
        cookieColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getCookieSimpleObjectProperty().value()));
    }

    private void initializeBinaryResponse() {

    }

    public void setMockServerFormTitle(String mockServerFormTitle) {
        this.mockServerFormTitle.setText(mockServerFormTitle);
    }

    public void setSaveServerButton(String saveServerButtonText) {
        this.saveServerButton.setText(saveServerButtonText);
    }

    public void setDefaultSelectedCollection(Collection defaultSelectedCollection) {
        this.defaultSelectedCollection = defaultSelectedCollection;
    }

    // Get Server form input
    public Optional<Server> getServerInput() {
        return Optional.ofNullable(serverInput);
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    private void viewSelectedFile() {
        // setting last 30 characters of the file absolute path to the file path view
        if (StringUtils.isNotBlank(responseBinaryPath)) {
            binaryResponseFilePath.setText(responseBinaryPath.length() >= 30 ?
                    responseBinaryPath.substring(responseBinaryPath.length() - 29) : responseBinaryPath);
        }
    }
}
