package com.server.app.controller;

import com.server.app.config.AppConfig;
import com.server.app.util.ImportExportUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import static com.server.app.constants.ApplicationConstants.APP_IMPORT_COLLECTION_TITLE;
import static com.server.app.constants.ApplicationConstants.FILE_SELECTOR_TITLE;
import static com.server.app.constants.ApplicationConstants.IMPORT_FILE_PATH_DEFAULT_TEXT;
import static com.server.app.constants.ApplicationConstants.JAVA_CROSS_PLATFORM_USER_DIRECTORY_PATH;
import static com.server.app.constants.ApplicationConstants.JSON_FILE_EXTENSION;
import static com.server.app.constants.ApplicationConstants.LIGHT_GREEN_COLOR_HEX_CODE;
import static com.server.app.constants.ApplicationConstants.LIGHT_GREY_COLOR_HEX_CODE;
import static com.server.app.constants.ApplicationConstants.LIGHT_RED_COLOR_HEX_CODE;
import static com.server.app.util.AppUtil.closeWindowButtonEvent;
import static com.server.app.util.AppUtil.getApplicationWindowByTitle;
import static com.server.app.util.AppUtil.triggerErrorAlert;

/**
 * author: Kazi Tanvir Azad
 */
public class ImportCollectionController implements Initializable {
    private static final Logger log = LoggerFactory.getLogger(ImportCollectionController.class);
    private final ImportExportUtil ioUtil;
    @FXML
    private Text importFileText;
    @FXML
    private TextFlow dragNDropTextFlow;
    private File selectedFile;

    public ImportCollectionController() {
        this.ioUtil = AppConfig.INSTANCE.getIoUtil();
    }

    @FXML
    private void handleSelectFileButton(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(FILE_SELECTOR_TITLE);
        if (FileUtils.isRegularFile(selectedFile) && FileUtils.isDirectory(selectedFile.getParentFile())) {
            fileChooser.setInitialDirectory(selectedFile.getParentFile());
        } else {
            fileChooser.setInitialDirectory(new File(JAVA_CROSS_PLATFORM_USER_DIRECTORY_PATH));
        }
        File newSelectedFile = fileChooser.showOpenDialog(getApplicationWindowByTitle(APP_IMPORT_COLLECTION_TITLE));
        if (FileUtils.isRegularFile(newSelectedFile)) {
            selectedFile = newSelectedFile;
            viewSelectedFile();
        }
    }

    @FXML
    private void handleImportCollectionButton(ActionEvent event) {
        if (!FileUtils.isRegularFile(selectedFile)) {
            triggerErrorAlert("Invalid file selection", "Select a valid file to continue");
            return;
        }
        if (ioUtil.importCollection(selectedFile)) {
            closeWindowButtonEvent(event);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // creating drag n drop label text
        Label defaultLabel = initializeDragNDropLabel("""
                Drag the file
                        &
                   drop here""", LIGHT_GREY_COLOR_HEX_CODE);
        Label notAFileLabel = initializeDragNDropLabel("""
                Directory
                     not
                  allowed""", LIGHT_RED_COLOR_HEX_CODE);
        Label multiDragFileLabel = initializeDragNDropLabel("""
                Multiple files
                      not
                  allowed""", LIGHT_RED_COLOR_HEX_CODE);
        Label unsupportedFileTypeLabel = initializeDragNDropLabel("""
                Unsupported
                      file
                    type""", LIGHT_RED_COLOR_HEX_CODE);
        Label dropFileLabel = initializeDragNDropLabel("""
                Drop here
                       to
                     add""", LIGHT_GREEN_COLOR_HEX_CODE);

        // setting default label
        dragNDropTextFlow.getChildren().add(defaultLabel);

        // setting drag & drop functionality - drag over
        dragNDropTextFlow.setOnDragOver(event -> {
            if (event.getGestureSource() != dragNDropTextFlow && event.getDragboard().hasFiles()) {
                Dragboard dragboard = event.getDragboard();
                event.acceptTransferModes(TransferMode.COPY);
                // setting drag & drop label text to make user validate the file to be imported
                if (dragboard.getFiles().size() > 1) {
                    dragNDropTextFlow.getChildren().clear();
                    dragNDropTextFlow.getChildren().add(multiDragFileLabel);
                } else if (FileUtils.isDirectory(dragboard.getFiles().getFirst())) {
                    dragNDropTextFlow.getChildren().clear();
                    dragNDropTextFlow.getChildren().add(notAFileLabel);
                } else if (FileUtils.isRegularFile(dragboard.getFiles().getFirst())
                        && !JSON_FILE_EXTENSION.equalsIgnoreCase(FilenameUtils.getExtension(dragboard.getFiles().getFirst().getName()))) {
                    dragNDropTextFlow.getChildren().clear();
                    dragNDropTextFlow.getChildren().add(unsupportedFileTypeLabel);
                } else {
                    dragNDropTextFlow.getChildren().clear();
                    dragNDropTextFlow.getChildren().add(dropFileLabel);
                }
            }
            event.consume();
        });
        // setting drag & drop functionality - drag exit
        dragNDropTextFlow.setOnDragExited(event -> {
            dragNDropTextFlow.getChildren().clear();
            dragNDropTextFlow.getChildren().add(defaultLabel);
            event.consume();
        });
        // setting drag & drop functionality - drag dropped
        dragNDropTextFlow.setOnDragDropped(event -> {
            if (event.getGestureSource() != dragNDropTextFlow && event.getDragboard().hasFiles()) {
                Dragboard dragboard = event.getDragboard();
                if (dragboard.getFiles().size() == 1 && FileUtils.isRegularFile(dragboard.getFiles().getFirst())
                        && JSON_FILE_EXTENSION.equalsIgnoreCase(FilenameUtils.getExtension(dragboard.getFiles().getFirst().getName()))) {
                    selectedFile = dragboard.getFiles().getFirst();
                    viewSelectedFile();
                }
            }
            event.consume();
        });

        // setting file path view default text
        importFileText.setText(IMPORT_FILE_PATH_DEFAULT_TEXT);
    }

    private void viewSelectedFile() {
        if (FileUtils.isRegularFile(selectedFile)) {
            String absolutePath = selectedFile.getAbsolutePath();
            String pathToView = StringUtils.isNotBlank(absolutePath) && absolutePath.length() >= 80
                    ? absolutePath.substring(absolutePath.length() - 79) : absolutePath;
            importFileText.setText(pathToView);
        }
    }

    private Label initializeDragNDropLabel(String text, String colorHexCode) {
        Label label = new Label();
        label.setText(text);
        label.setStyle(String.format("-fx-font-size: 50px;-fx-font-weight: bold;-fx-text-fill: %s", colorHexCode));
        return label;
    }
}
