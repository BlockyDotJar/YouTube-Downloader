/**
 * YouTube-Downloader - Downloader for YouTube videos, that extracts audio from the video.
 * Copyright (C) 2024 BlockyDotJar (aka. Dominic R.)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package dev.blocky.app.ytd;

import dev.blocky.app.ytd.entities.NodeCreator;
import dev.blocky.app.ytd.handler.SettingsHandler;
import dev.blocky.app.ytd.windows.api.WindowsRegistry;
import dev.blocky.app.ytd.windows.api.dwm.DWMHandler;
import fr.brouillard.oss.cssfx.CSSFX;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static dev.blocky.app.ytd.handler.DownloadHandler.initDownloader;
import static dev.blocky.app.ytd.updater.ApplicationUpdater.initApplicationUpdater;

public class YTDApplication extends Application
{
    @Override
    public void start(Stage stage) throws IOException
    {
        HostServices hostServices = getHostServices();
        NodeCreator creator = new NodeCreator();

        AnchorPane anchorPane = new AnchorPane();

        TextArea detailArea = creator.createTextArea(null, 130, 95);

        boolean immersiveDarkMode = SettingsHandler.usesImmersiveDarkMode(detailArea);

        initDownloader(hostServices, anchorPane, detailArea, immersiveDarkMode);

        URL iconResource = getClass().getResource("/assets/icons/icon.png");
        InputStream iconResourceStream = iconResource.openStream();
        Image icon = new Image(iconResourceStream);

        URL cssResource = getClass().getResource("/assets/ui/css/styles.css");
        String css = cssResource.toExternalForm();

        Scene scene = new Scene(anchorPane, 640, 440);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(css);

        stage.setOnCloseRequest(_ -> System.exit(0));
        stage.setTitle("YouTube-Downloader");
        stage.initStyle(StageStyle.UNIFIED);
        stage.getIcons().add(icon);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

        Platform.runLater(() ->
                {
                    String displayVersionRaw = WindowsRegistry.getWindowsDisplayVersion();
                    String displayVersion = StringUtils.remove(displayVersionRaw, "H");

                    int dvNumber = Integer.parseInt(displayVersion);

                    if (SystemUtils.IS_OS_WINDOWS_11 && dvNumber >= 222)
                    {
                        DWMHandler.setAcrylicStyle(immersiveDarkMode);
                    }
                }
        );

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> initApplicationUpdater(hostServices, detailArea));

        CSSFX.start();
    }

    public static void main(String[] args)
    {
        System.setProperty("prism.forceUploadingPainter", "true");

        launch();
    }
}
