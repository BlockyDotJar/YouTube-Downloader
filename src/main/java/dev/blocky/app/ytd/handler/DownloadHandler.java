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
package dev.blocky.app.ytd.handler;

import dev.blocky.app.ytd.api.ServiceProvider;
import dev.blocky.app.ytd.api.entities.NoEmbed;
import dev.blocky.app.ytd.entities.NodeCreator;
import dev.blocky.app.ytd.entities.YTDownloader;
import dev.blocky.app.ytd.windows.api.WindowsRegistry;
import dev.blocky.app.ytd.windows.api.dwm.DWMHandler;
import javafx.application.HostServices;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.controlsfx.control.InfoOverlay;
import org.controlsfx.control.ToggleSwitch;

import static dev.blocky.app.ytd.handler.ActionHandler.invalidAction;
import static dev.blocky.app.ytd.handler.TrayIconHandler.sendErrorPushNotification;

public class DownloadHandler
{
    private static final NodeCreator creator = new NodeCreator();

    public static String audioFormat = "mp3";
    public static int audioQuality = 0;

    public static void initDownloader(HostServices hostServices, AnchorPane anchorPane, TextArea detailArea, boolean immersiveDarkMode)
    {
        TextField url = creator.createTextField("Enter a valid YouTube url.", 130, 10);

        ToggleSwitch appearance = creator.createToggleSwitch(30, 407, immersiveDarkMode);
        Label appearanceLabel = creator.createLabel("Dark mode", 30, 385, true);

        Button downloads = creator.createButton("\u2B07", 10, 50, false);
        Button download = creator.createButton("Download", 10, 10, true);
        Button clear = creator.createButton("Clear", 575, 10, false);

        url.textProperty().addListener((_, _, newVal) ->
        {
            if (newVal == null || newVal.isBlank())
            {
                download.setDisable(true);
                return;
            }

            download.setDisable(false);
        });

        ObservableList<String> audioFormats = FXCollections.observableArrayList
                (
                        "mp3", "wav", "ogg", "m4a", "flac"
                );

        ComboBox<String> audioFormatBox = creator.createComboBox("mp3", 130, 50, audioFormats);

        ObservableList<String> audioQualities = FXCollections.observableArrayList
                (
                        "highest possible", "normal", "lowest possible"
                );

        ComboBox<String> audioQualityBox = creator.createComboBox("highest possible", 391, 50, audioQualities);

        Region thumbnailRegion = creator.createRegion(130, 175);
        InfoOverlay thumbnail = creator.createInfoOverlay(140, 185);

        ObservableList<Node> children = anchorPane.getChildren();
        children.addAll(url, appearance, appearanceLabel, downloads, download, clear, audioFormatBox, audioQualityBox, detailArea, thumbnailRegion, thumbnail);

        initDownload(detailArea, download, thumbnail, url);
        initClear(detailArea, clear, url, appearance, audioFormatBox, audioQualityBox, thumbnail);
        initAudioFormatBox(audioFormatBox);
        initAudioQualityBox(audioQualityBox);
        initAppearance(appearance, detailArea);
        initDownloads(downloads, hostServices);
    }

    public static void initDownload(TextArea detailArea, Button download, InfoOverlay thumbnail, TextField url)
    {
        download.setOnAction(_ ->
        {
            try
            {
                String link = url.getText();

                String videoID = YTDownloader.getVideoID(link);

                if (videoID == null)
                {
                    invalidAction(detailArea, "Invalid YouTube link specified.");
                    return;
                }

                link = STR."https://www.youtube.com/watch?v=\{videoID}";

                NoEmbed noEmbed = ServiceProvider.getNoEmbed(link);

                String titleRaw = noEmbed.getTitle();
                String title = RegExUtils.removeAll(titleRaw, "[\\\\/|:*?\"<>]");

                String authorName = noEmbed.getAuthorName();

                YTDownloader.downloadTrack(detailArea, link, videoID, thumbnail, title, authorName);
            }
            catch (Exception e)
            {
                sendErrorPushNotification(detailArea, e);
                invalidAction(detailArea, ExceptionUtils.getStackTrace(e));
            }
        });
    }

    public static void initClear(TextArea detailArea, Button clear, TextField url, ToggleSwitch appearance, ComboBox<String> audioFormatBox, ComboBox<String> audioQualityBox, InfoOverlay thumbnail)
    {
        clear.setOnAction(_ ->
        {
            boolean immersiveDarkMode = SettingsHandler.usesImmersiveDarkMode(detailArea);

            url.clear();

            appearance.setSelected(immersiveDarkMode);

            detailArea.clear();

            SingleSelectionModel<String> audioFormatModel = audioFormatBox.getSelectionModel();
            audioFormatModel.clearAndSelect(0);

            SingleSelectionModel<String> audioQualityModel = audioQualityBox.getSelectionModel();
            audioQualityModel.clearAndSelect(0);

            audioFormat = "mp3";
            audioQuality = 0;

            ImageView imageView = new ImageView();
            imageView.setFitWidth(405);
            imageView.setFitHeight(227.81);

            thumbnail.setContent(imageView);
            thumbnail.setText(null);
        });
    }

    public static void initAudioFormatBox(ComboBox<String> audioFormatBox)
    {
        audioFormatBox.setOnAction(_ ->
        {
            String audioFormatRaw = audioFormatBox.getValue();

            if (!audioFormatRaw.equals("ogg"))
            {
                audioFormat = audioFormatRaw;
                return;
            }

            audioFormat = "vorbis";
        });
    }

    public static void initAudioQualityBox(ComboBox<String> audioQualityBox)
    {
        audioQualityBox.setOnAction(_ ->
        {
            String audioQualityRaw = audioQualityBox.getValue();

            audioQuality = switch (audioQualityRaw)
            {
                case "normal" -> 5;
                case "lowest possible" -> 10;
                default -> 0;
            };
        });
    }

    public static void initAppearance(ToggleSwitch appearance, TextArea detailArea)
    {
        appearance.selectedProperty().addListener((_, _, newVal) ->
        {
            SettingsHandler.writeSettings(detailArea, "immersive-dark-mode", newVal);
            DWMHandler.setAcrylicStyle(newVal);
        });
    }

    public static void initDownloads(Button downloads, HostServices hosts)
    {
        String appHome = WindowsRegistry.getApplicationHome();
        downloads.setOnAction(_ -> hosts.showDocument(STR."\{appHome}\\Downloads"));
    }
}
