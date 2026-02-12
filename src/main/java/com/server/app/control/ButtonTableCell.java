package com.server.app.control;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;

public class ButtonTableCell<S, T> extends TableCell<S, T> {
    private final StackPane stackPaneContainer;
    private final Button statusButton;

    public ButtonTableCell() {
        statusButton = new Button();
        statusButton.setTextAlignment(TextAlignment.CENTER);
        statusButton.setPrefSize(70.0, 25.0);
        statusButton.setCursor(Cursor.HAND);
        stackPaneContainer = new StackPane(statusButton);
        stackPaneContainer.setAlignment(Pos.CENTER);
    }

    public void setCustomMouseEvent(EventHandler<? super MouseEvent> event) {
        this.statusButton.setOnMouseClicked(event);
    }

    public void customiseButton(String buttonText) {
        statusButton.setText(buttonText);
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
            setText(null);
        } else {
            setGraphic(stackPaneContainer);
        }
    }
}
