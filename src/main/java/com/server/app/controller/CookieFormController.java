package com.server.app.controller;

import com.server.app.model.data.Cookie;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.server.app.util.AppUtil.closeWindowButtonEvent;
import static com.server.app.util.AppUtil.triggerErrorAlert;

/**
 * author: Kazi Tanvir Azad
 */
public class CookieFormController implements Initializable {
    @FXML
    private TextField cookieNameInput;
    @FXML
    private TextField cookieValueInput;
    @FXML
    private TextField domainInput;
    @FXML
    private TextField expiresInput;
    @FXML
    private CheckBox httpOnlyCheckbox;
    @FXML
    private CheckBox portionedCheckbox;
    @FXML
    private CheckBox secureCheckbox;
    @FXML
    private TextField maxAgeInput;
    @FXML
    private TextField pathInput;
    @FXML
    private ChoiceBox<Cookie.SameSite> sameSiteChoice;
    private Cookie cookieInput;

    @FXML
    private void handleSaveCookieButtonEvent(ActionEvent event) {
        var cookieName = cookieNameInput.getText();
        var cookieValue = cookieValueInput.getText();
        var domain = domainInput.getText();
        var expires = expiresInput.getText();
        var maxAge = maxAgeInput.getText();
        var path = pathInput.getText();
        var sameSite = sameSiteChoice.getValue();
        var httpOnly = httpOnlyCheckbox.isSelected();
        var portioned = portionedCheckbox.isSelected();
        var secure = secureCheckbox.isSelected();
        if (StringUtils.isBlank(cookieName) || StringUtils.isBlank(cookieValue)) {
            triggerErrorAlert("Invalid Cookie!", "Cookie Name and Value is mandatory!");
        } else {
            try {
                cookieInput = new Cookie();
            } catch (RuntimeException exception) {
                cookieInput = null;
                closeWindowButtonEvent(event);
                return;
            }
            cookieInput.setName(cookieName);
            cookieInput.setValue(cookieValue);
            if (StringUtils.isNotBlank(domain)) {
                cookieInput.setDomain(domain.trim());
            }
            if (StringUtils.isNotBlank(expires)) {
                cookieInput.setExpires(expires.trim());
            }
            if (StringUtils.isNotBlank(maxAge) && NumberUtils.isDigits(maxAge.trim())) {
                cookieInput.setMaxAge(Long.valueOf(maxAge.trim()));
            }
            if (StringUtils.isNotBlank(path)) {
                cookieInput.setPath(path.trim());
            }
            if (null != sameSite) {
                cookieInput.setSameSite(sameSite);
            }
            if (httpOnly) {
                cookieInput.setHttpOnly(true);
            }
            if (portioned) {
                cookieInput.setPartitioned(true);
            }
            if (secure) {
                cookieInput.setSecure(true);
            }
            closeWindowButtonEvent(event);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Setting SameSite choice data
        sameSiteChoice.setItems(FXCollections.observableArrayList(Cookie.SameSite.values()));
        // setting sameSiteChoice interaction on selection
        sameSiteChoice.getSelectionModel()
                .selectedIndexProperty()
                .addListener((observable, oldValue, newValue) ->
                        sameSiteChoice.setValue(Cookie.SameSite.values()[newValue.intValue()]));
    }

    // Get Cookie form input
    public Optional<Cookie> getCookieInput() {
        return Optional.ofNullable(cookieInput);
    }
}
