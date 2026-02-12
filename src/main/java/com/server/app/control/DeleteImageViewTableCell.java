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

public class DeleteImageViewTableCell<S, T> extends TableCell<S, T> {
    private static final Logger log = LogManager.getLogger(DeleteImageViewTableCell.class);
    private final StackPane stackPaneContainer;
    private final ImageView deleteImageView;

    public DeleteImageViewTableCell() {
        deleteImageView = new ImageView();
        deleteImageView.setFitHeight(24.0);
        deleteImageView.setFitWidth(24.0);
        deleteImageView.setPreserveRatio(true);
        Image placeholderImage;
        try {
            placeholderImage = new Image("/static/icons/delete-24.png");
        } catch (Exception exception) {
            log.error("Error loading placeholder 'delete' image: {}", exception.getMessage());
            placeholderImage = new Image("/static/icons/grey-delete-24.png");
        }
        deleteImageView.setCursor(Cursor.HAND);
        deleteImageView.setImage(placeholderImage);
        stackPaneContainer = new StackPane(deleteImageView);
        stackPaneContainer.setAlignment(Pos.CENTER);
    }

    public void setCustomMouseEvent(EventHandler<? super MouseEvent> event) {
        this.deleteImageView.setOnMouseClicked(event);
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
