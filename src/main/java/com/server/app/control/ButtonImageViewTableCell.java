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

/**
 * author: Kazi Tanvir Azad
 */
public class ButtonImageViewTableCell<S, T> extends TableCell<S, T> {
    private static final Logger log = LogManager.getLogger(ButtonImageViewTableCell.class);
    private String imagePath;
    private final StackPane stackPaneContainer;
    private final ImageView buttonImageView;

    public ButtonImageViewTableCell(String imagePath) {
        this.imagePath = imagePath;
        this();
    }

    private ButtonImageViewTableCell() {
        this.buttonImageView = new ImageView();
        this.buttonImageView.setFitHeight(24.0);
        this.buttonImageView.setFitWidth(24.0);
        this.buttonImageView.setPreserveRatio(true);
        Image placeholderImage = null;
        try {
            placeholderImage = new Image(this.imagePath);
        } catch (Exception exception) {
            log.error("Error loading placeholder image: {}", exception.getMessage());
        }
        this.buttonImageView.setCursor(Cursor.HAND);
        if (null != placeholderImage) {
            this.buttonImageView.setImage(placeholderImage);
        }
        this.stackPaneContainer = new StackPane(buttonImageView);
        this.stackPaneContainer.setAlignment(Pos.CENTER);
    }

    public void setCustomMouseEvent(EventHandler<? super MouseEvent> event) {
        this.buttonImageView.setOnMouseClicked(event);
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
