package com.server.app.controller;

import com.server.app.util.AppUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.net.URL;
import java.util.ResourceBundle;

public class PromptMessageController implements Initializable {
    @FXML
    public Button messageAcceptButton;
    @FXML
    public TextFlow messageTextFlow;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        messageAcceptButton.setOnAction(AppUtil::closeWindowButtonEvent);
    }

    public void setMessageText(String messageText) {
        Text text = new Text(messageText);
        setMessageText(text);
    }

    public void setMessageText(Text... texts) {
        for (Text text : texts) {
            messageTextFlow.getChildren().add(text);
        }
    }
}
