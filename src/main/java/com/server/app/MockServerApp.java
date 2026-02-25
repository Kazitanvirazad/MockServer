package com.server.app;

import com.server.app.config.AppConfig;
import com.server.app.controller.MainAppController;
import com.server.app.controller.SplashScreenController;
import com.server.app.fxml.loader.MainStageLoader;
import com.server.app.fxml.loader.SplashScreenStageLoader;
import com.server.app.fxml.loader.StageLoader;
import com.server.app.server.ServerManager;
import com.server.app.service.ServerRestartService;
import com.server.app.service.ServerService;
import com.server.app.service.Service;
import com.server.app.service.SettingsService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.MDC;

import java.security.Security;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

import static com.server.app.constants.ApplicationConstants.TRACER;
import static com.server.app.util.AppUtil.exitApplication;
import static com.server.app.util.AppUtil.generateUUID7BasedId;
import static com.server.app.util.AppUtil.triggerErrorAlert;
import static com.server.app.util.DatabaseUtil.executeCreateQuery;
import static com.server.app.util.DatabaseUtil.readStartupSQLScript;

/**
 * @author Kazi Tanvir Azad
 */
public class MockServerApp extends Application {
    private static final Logger log = LogManager.getLogger(MockServerApp.class);
    private final ServerService serverService = Service.INSTANCE.getServerService();
    private final ServerRestartService serverRestartService = Service.INSTANCE.getServerRestartService();
    private final SettingsService settingsService = Service.INSTANCE.getSettingsService();

    @Override
    public void start(Stage primaryStage) {
        try {
            // Setting tracer for logging
            generateUUID7BasedId().ifPresent(tracer -> MDC.put(TRACER, tracer));
            // Setting HostServices to Server
            Service.INSTANCE.setHostServices(getHostServices());
            // Setting BouncyCastle Provider to java.security
            final BouncyCastleProvider provider = new BouncyCastleProvider();
            if (null == Security.getProvider(BouncyCastleProvider.PROVIDER_NAME)) {
                Security.addProvider(provider);
            }
            StageLoader<SplashScreenController> splashScreenStageLoader = new SplashScreenStageLoader();
            StageLoader<MainAppController> mainStageLoader = new MainStageLoader(primaryStage);
            // Loading splash screen during application startup
            splashScreenStageLoader.loadStage();
            Stage splashScreenStage = splashScreenStageLoader.getStage();

            // handling the Splash Screen Stage closure and Main App Stage loading
            Platform.runLater(() -> {
                try {
                    // restart servers during app startup if configured in settings
                    if (AppConfig.INSTANCE.getConfiguration().isStartServerOnStartup()) {
                        serverRestartService.getAllServerRestartDataStream()
                                .map(serverService::getServerById)
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                                .forEach(server -> ServerManager.INSTANCE.startServer(server, true));
                    }
                    // Keeping the Splash screen for 1.4 seconds
                    Thread.sleep(1400);
                } catch (Exception exception) {
                    log.error(exception.getMessage());
                }
                // closing splash screen
                splashScreenStage.close();
                // Loading application main stage
                mainStageLoader.loadStage();
            });
        } catch (RuntimeException exception) {
            triggerErrorAlert("Failed to load application!", """
                    Try opening the application again.
                    If the problem persists,
                    try reinstalling the application.""");
            log.error("Failed to load application!");
            log.error(exception.getMessage());
            exitApplication();
        }
    }

    @Override
    public void init() throws Exception {
        super.init();
        // Execute sql DDL queries during application initialization
        List<String> queries = readStartupSQLScript();
        for (String query : queries) {
            executeCreateQuery(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                return preparedStatement.executeUpdate();
            });
        }
        // Initialize setting table row for first time app startup and sync configuration
        settingsService.initAndSyncSettings();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        // Stop all servers
        List<String> stoppedServerIds = ServerManager.INSTANCE.stopAllServers(true);
        // persist stopped ServerIds for restart on application startup
        serverRestartService.resetServerRestartData();
        if (CollectionUtils.isNotEmpty(stoppedServerIds)) {
            serverRestartService.putServerRestartData(stoppedServerIds);
        }
        // Save the settings in case not updated
        settingsService.updateConfig();
        // Remove the log tracer
        if (null != MDC.get(TRACER)) {
            MDC.remove(TRACER);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
