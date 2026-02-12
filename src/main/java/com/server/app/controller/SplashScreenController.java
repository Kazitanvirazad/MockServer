package com.server.app.controller;

import com.server.app.config.AppConfig;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class SplashScreenController implements Initializable {
    @FXML
    private ImageView splashScreenIconImageView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        splashScreenIconImageView.setPreserveRatio(true);
        splashScreenIconImageView.setImage(AppConfig.INSTANCE.getSplashScreenLogo());
    }
}
