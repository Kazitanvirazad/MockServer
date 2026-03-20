package com.server.app.controller;

import com.server.core.config.CommonConfig;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.server.app.util.AppUtil.closeWindowButtonEvent;

/**
 * @author Kazi Tanvir Azad
 */
public class CollectionFormController implements Initializable {
    @FXML
    private TextField collectionNameTextField;
    @FXML
    private Button saveCollectionButton;
    private String collectionNameInput;
    private boolean doEdit;
    private String collectionName;

    @FXML
    private void handleSaveButtonEvent(ActionEvent event) {
        if (StringUtils.isBlank(collectionNameTextField.getText())) {
            CommonConfig.INSTANCE.notification()
                    .triggerErrorNotification("Invalid Collection Name!", "Continue to add Name to the Collection");
            return;
        }
        collectionNameInput = collectionNameTextField.getText().trim();
        closeWindowButtonEvent(event);
    }

    @FXML
    private void handleCancelButtonEvent(ActionEvent event) {
        closeWindowButtonEvent(event);
    }

    // Get Collection form input
    public Optional<String> getCollectionNameInput() {
        return Optional.ofNullable(collectionNameInput);
    }

    public void setDoEdit(boolean doEdit) {
        this.doEdit = doEdit;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        saveCollectionButton.setText(doEdit ? "Edit" : "Save");
        if (null != collectionName) {
            collectionNameTextField.setText(collectionName);
        }
    }
}
