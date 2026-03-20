package com.server.app.controller;

import com.server.core.config.CommonConfig;
import com.server.core.model.data.Collection;
import com.server.core.service.CollectionService;
import com.server.core.service.Service;
import com.server.core.util.ImportExportUtil;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static com.server.app.constants.AppConstants.APP_EXPORT_COLLECTION_TITLE;
import static com.server.app.constants.AppConstants.JAVA_CROSS_PLATFORM_USER_DIRECTORY_PATH;
import static com.server.app.util.AppUtil.closeWindowButtonEvent;
import static com.server.app.util.AppUtil.getApplicationWindowByTitle;
import static com.server.core.constants.CommonConstants.EXPORT_DIRECTORY_PATH_DEFAULT_TEXT;
import static com.server.core.constants.CommonConstants.EXPORT_DIRECTORY_SELECTOR_TITLE;
import static javafx.beans.binding.Bindings.not;

/**
 * @author Kazi Tanvir Azad
 */
public class ExportCollectionController implements Initializable {
    private static final Logger log = LogManager.getLogger(ExportCollectionController.class);
    private final CollectionService collectionService;
    private final ImportExportUtil ioUtil;
    @FXML
    private Text exportDirectoryText;
    @FXML
    private ChoiceBox<Collection> collectionChoice;
    @FXML
    private CheckBox exportAllCheck;
    private File selectedDirectory;

    public ExportCollectionController() {
        this.ioUtil = CommonConfig.INSTANCE.getIoUtil();
        this.collectionService = Service.INSTANCE.getCollectionService();
    }

    @FXML
    private void handleSelectDirectory(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(EXPORT_DIRECTORY_SELECTOR_TITLE);
        if (FileUtils.isDirectory(selectedDirectory)) {
            directoryChooser.setInitialDirectory(selectedDirectory);
        } else {
            directoryChooser.setInitialDirectory(new File(JAVA_CROSS_PLATFORM_USER_DIRECTORY_PATH));
        }
        File newSelectedDirectory = directoryChooser.showDialog(getApplicationWindowByTitle(APP_EXPORT_COLLECTION_TITLE));
        if (FileUtils.isDirectory(newSelectedDirectory)) {
            String absolutePath = newSelectedDirectory.getAbsolutePath();
            String pathToView = StringUtils.isNotBlank(absolutePath) && absolutePath.length() >= 80
                    ? absolutePath.substring(absolutePath.length() - 79) : absolutePath;
            exportDirectoryText.setText(EXPORT_DIRECTORY_PATH_DEFAULT_TEXT + pathToView);
            selectedDirectory = newSelectedDirectory;
        }
    }

    @FXML
    private void handleExportCollectionButton(ActionEvent event) {
        if (!FileUtils.isDirectory(selectedDirectory)) {
            CommonConfig.INSTANCE.notification()
                    .triggerErrorNotification("Invalid directory path selection", "Select a valid directory path to continue");
            return;
        }
        List<Collection> collections = null;
        if (exportAllCheck.isSelected()) {
            collections = collectionService.getCollectionStream().collect(Collectors.toList());
        } else {
            Optional<Collection> optionalSelectedCollection = collectionService.getCollectionById(collectionChoice
                    .getValue().getCollectionId());
            if (optionalSelectedCollection.isPresent()) {
                collections = List.of(optionalSelectedCollection.get());
            }
        }
        if (CollectionUtils.isEmpty(collections)) {
            CommonConfig.INSTANCE.notification()
                    .triggerErrorNotification("Collection Not Found!", "Add Collection before export");
        } else {
            ioUtil.exportCollection(selectedDirectory, collections);
        }
        closeWindowButtonEvent(event);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // setting collectionChoice disable property binding
        collectionChoice.disableProperty().bind(not(exportAllCheck.selectedProperty().not()));
        // setting Collection choice data
        List<Collection> collectionChoiceData = collectionService.getCollectionStream().collect(Collectors.toList());
        collectionChoice.setItems(FXCollections.observableList(collectionChoiceData));
        // setting collectionChoice interaction on selection
        collectionChoice.getSelectionModel()
                .selectedIndexProperty()
                .addListener((observable, oldValue, newValue) ->
                        collectionChoice.getItems().get(newValue.intValue()));
        // setting default Collection selection
        if (CollectionUtils.isNotEmpty(collectionChoiceData)) {
            collectionChoiceData.stream()
                    .findFirst()
                    .ifPresent(collectionChoice::setValue);
        }
        //setting directory path view default text
        exportDirectoryText.setText(EXPORT_DIRECTORY_PATH_DEFAULT_TEXT);
    }
}
