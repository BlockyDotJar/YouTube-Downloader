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
package dev.blocky.app.ytd.updater;

import dev.blocky.app.ytd.entities.NodeCreator;
import dev.blocky.app.ytd.windows.api.WindowsRegistry;
import io.github.g00fy2.versioncompare.Version;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import one.jpro.platform.mdfx.MarkdownView;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.kohsuke.github.*;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.List;

import static dev.blocky.app.ytd.handler.ActionHandler.invalidAction;
import static dev.blocky.app.ytd.handler.TrayIconHandler.*;

public class ApplicationUpdater
{
    private static List<String> getLatestVersion(TextArea detailArea)
    {
        try
        {
            GitHub gitHub = new GitHubBuilder().build();
            GHRepository repository = gitHub.getRepository("BlockyDotJar/YouTube-Downloader");
            GHRelease release = repository.getLatestRelease();

            if (release == null)
            {
                return null;
            }

            String currentVersionTag = WindowsRegistry.getApplicationVersion();
            String ghVersionTagName = release.getTagName();

            if (ghVersionTagName == null)
            {
                return null;
            }

            String ghVersionTag = ghVersionTagName.substring(1);

            Version currentVersion = new Version(currentVersionTag);
            Version ghVersion = new Version(ghVersionTag);

            if (!ghVersionTag.matches("[0-9.]+") || ghVersion.isLowerThan(currentVersion))
            {
                return null;
            }

            String version = null;
            String releaseLink = null;
            String downloadLink = null;
            String markdown = null;

            if (release.isPrerelease())
            {
                PagedIterable<GHRelease> releasesRaw = repository.listReleases();
                List<GHRelease> releases = releasesRaw.toList();

                for (GHRelease ghRelease : releases)
                {
                    if (ghRelease.isPrerelease())
                    {
                        continue;
                    }

                    String ghrVersionTagRaw = ghRelease.getTagName();
                    String ghrVersionTag = ghrVersionTagRaw.substring(1);

                    Version ghrVersion = new Version(ghrVersionTag);

                    if (ghrVersion.isLowerThan(currentVersion))
                    {
                        continue;
                    }

                    PagedIterable<GHAsset> assetsRaw = ghRelease.listAssets();
                    List<GHAsset> assets = assetsRaw.toList();

                    if (assets.isEmpty())
                    {
                        return List.of();
                    }

                    GHAsset ghAsset = assets.getFirst();

                    URL htmlURL = ghRelease.getHtmlUrl();

                    version = ghrVersionTag;
                    releaseLink = htmlURL.toString();
                    downloadLink = ghAsset.getBrowserDownloadUrl();
                    markdown = ghRelease.getBody();
                }

                return List.of(releaseLink, downloadLink, version, markdown);
            }

            List<GHAsset> assets = release.listAssets().toList();

            if (assets.isEmpty())
            {
                return null;
            }

            version = ghVersionTag;
            releaseLink = release.getHtmlUrl().toString();
            downloadLink = assets.getFirst().getBrowserDownloadUrl();
            markdown = release.getBody();

            return List.of(releaseLink, downloadLink, version, markdown);
        }
        catch (Exception e)
        {
            sendErrorPushNotification(detailArea, e);
            invalidAction(detailArea, ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    private static File downloadAndInstallFile(TextArea detailArea, List<String> versionData)
    {
        try
        {
            String downloadUrl = versionData.get(1);

            String appHome = WindowsRegistry.getApplicationHome();
            File home = new File(appHome);

            if (!home.exists() || !home.isDirectory())
            {
                return null;
            }

            File update = new File(home, "update");

            if (!update.exists() || !update.isDirectory())
            {
                update.mkdir();
            }

            int lastSlash = downloadUrl.lastIndexOf('/');
            String exeName = downloadUrl.substring(lastSlash + 1);

            File file = new File(update, exeName);
            URI uri = new URI(downloadUrl);
            URL url = uri.toURL();

            FileUtils.copyURLToFile(url, file);

            return file;
        }
        catch (Exception e)
        {
            sendErrorPushNotification(detailArea, e);
            invalidAction(detailArea, ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    private static void startDownloadTask(HostServices hostServices, NodeCreator creator, TextArea detailArea, List<String> versionDetails)
    {
        Task<File> backgroundTask = new Task<>()
        {
            @Override
            protected File call()
            {
                return downloadAndInstallFile(detailArea, versionDetails);
            }
        };

        backgroundTask.setOnCancelled(_ ->
                Platform.runLater(() ->
                {
                    String title = "Download of the latest version of the YouTube-Downloader was canceled";
                    String headerText = "The download process was canceled due to unknown reasons.";

                    AnchorPane alertPane = new AnchorPane();

                    Label retryLabel = creator.createLabel("Retry downloading the application and check if you have access to your internet and also check that your antivirus doesn't block anything.", 4, 10, false);
                    Hyperlink hyperlink = creator.createHyperlink(hostServices, "Click here to open a new issue on the YouTube-Downloader GitHub page.", "https://github.com/BlockyDotJar/YouTube-Downloader/issues/new?template=BUG_REPORT.yml", 0, 25);

                    ObservableList<Node> children = alertPane.getChildren();
                    children.addAll(retryLabel, hyperlink);

                    sendWarningPushNotification(detailArea, title, headerText);
                    invalidAction(detailArea, headerText);

                    Alert closeAlert = creator.createAlert(Alert.AlertType.WARNING, title, headerText, alertPane);
                    closeAlert.show();
                })
        );

        backgroundTask.setOnFailed(_ ->
                Platform.runLater(() ->
                {
                    String title = "Download of the latest version of YouTube-Downloader failed";
                    String headerText = "The download process failed due to unknown reasons.";

                    AnchorPane alertPane = new AnchorPane();

                    Label retryLabel = creator.createLabel("Retry downloading the application and check if you have access to your internet and also check that your antivirus doesn't block anything.", 4, 10, false);
                    Hyperlink hyperlink = creator.createHyperlink(hostServices, "Click here to open a new issue on the YouTube-Downloader GitHub page.", "https://github.com/BlockyDotJar/YouTube-Downloader/issues/new?template=BUG_REPORT.yml", 0, 25);

                    ObservableList<Node> children = alertPane.getChildren();
                    children.addAll(retryLabel, hyperlink);

                    sendErrorPushNotification(detailArea, title, headerText);
                    invalidAction(detailArea, headerText);

                    Alert closeAlert = creator.createAlert(Alert.AlertType.ERROR, title, headerText, alertPane);
                    closeAlert.show();
                })
        );

        backgroundTask.setOnSucceeded(_ ->
                Platform.runLater(() ->
                {
                    File setupWizard = backgroundTask.getValue();

                    if (setupWizard == null)
                    {
                        String title = "Error while trying to install the latest version of YouTube-Downloader";
                        String headerText = "The installation was canceled due to unknown reasons.";
                        String contentText = "Retry installing the application and check if you have access to your internet and also check that your antivirus doesn't block anything.";

                        sendErrorPushNotification(detailArea, title, headerText);
                        invalidAction(detailArea, headerText);

                        Alert closeAlert = creator.createAlert(Alert.AlertType.ERROR, title, headerText, contentText);
                        closeAlert.show();
                        return;
                    }

                    String absoluteSetupWizardPath = setupWizard.getAbsolutePath();

                    String title = "Successfully downloaded newest version!";
                    String headerText = "Installation wizard was downloaded successfully!";
                    String contentText = "The YouTube-Downloader will be closed after button interaction and the installation wizard will be opened.";

                    sendInfoPushNotification(detailArea, title, headerText);
                    invalidAction(detailArea, headerText);

                    Alert closeAlert = creator.createAlert(Alert.AlertType.INFORMATION, title, headerText, contentText);
                    closeAlert.showAndWait();

                    hostServices.showDocument(absoluteSetupWizardPath);

                    System.exit(0);
                })
        );

        new Thread(backgroundTask).start();
    }

    public static boolean initApplicationUpdater(HostServices hostServices, TextArea detailArea)
    {
        NodeCreator creator = new NodeCreator();
        List<String> versionDetails = getLatestVersion(detailArea);

        if (versionDetails != null && !versionDetails.isEmpty())
        {
            String releaseLink = versionDetails.getFirst();
            String version = versionDetails.get(2);
            String markdown = versionDetails.get(3);

            AnchorPane alertPane = new AnchorPane();
            alertPane.setMinSize(805, 435);
            alertPane.setMaxSize(805, 435);

            String text = STR."Version \{version} is here! Do you want to install the newest version of the YouTube-Downloader?";

            Label label = creator.createLabel(text, 20, 0, false);
            Hyperlink hyperlink = creator.createHyperlink(hostServices, "Read here about the new version.", releaseLink, 16, 15);

            MarkdownView markdownView = creator.createMarkdownView(hostServices, markdown);
            ScrollPane scrollPane = creator.createScrollPane(markdownView, 20, 40);

            ObservableList<Node> children = alertPane.getChildren();
            children.addAll(label, hyperlink, scrollPane);

            String title = "New YouTube-Downloader version available!";
            String headerText = STR."Looks like there is a new version of the YouTube-Downloader (\{version}) available!";

            Platform.runLater(() ->
            {
                Alert updateAlert = creator.createAlert(Alert.AlertType.CONFIRMATION, title, headerText, alertPane);

                ObservableList<ButtonType> buttonTypes = updateAlert.getButtonTypes();

                ButtonType downloadButton = new ButtonType("Download", ButtonBar.ButtonData.OK_DONE);
                buttonTypes.setAll(downloadButton, ButtonType.CANCEL);

                if (!updateAlert.isShowing())
                {
                    updateAlert.showAndWait().ifPresent((bt) ->
                    {
                        if (bt.getButtonData() == ButtonBar.ButtonData.OK_DONE)
                        {
                            startDownloadTask(hostServices, creator, detailArea, versionDetails);
                        }
                    });
                }
            });
            return true;
        }
        return false;
    }
}
