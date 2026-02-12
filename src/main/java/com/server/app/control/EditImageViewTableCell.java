package com.server.app.control;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EditImageViewTableCell<S, T> extends TableCell<S, T> {
    private static final Logger log = LogManager.getLogger(EditImageViewTableCell.class);
    private final StackPane stackPaneContainer;
    private final ImageView editImageView;

    public EditImageViewTableCell() {
        editImageView = new ImageView();
        editImageView.setFitHeight(24.0);
        editImageView.setFitWidth(24.0);
        editImageView.setPreserveRatio(true);
        Image placeholderImage;
        try {
            placeholderImage = new Image("/static/icons/grey-edit-24.png");
        } catch (Exception exception) {
            log.error("Error loading placeholder 'edit' image: {}", exception.getMessage());
            placeholderImage = new Image("/static/icons/edit-24.png");
        }
        editImageView.setCursor(Cursor.HAND);
        editImageView.setImage(placeholderImage);
        stackPaneContainer = new StackPane(editImageView);
        stackPaneContainer.setAlignment(Pos.CENTER);
    }

    public void setCustomMouseEvent(EventHandler<? super MouseEvent> event) {
        this.editImageView.setOnMouseClicked(event);
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
        } else {
            setGraphic(stackPaneContainer);
        }
    }
}
