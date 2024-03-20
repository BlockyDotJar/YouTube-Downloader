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
package dev.blocky.app.ytd.entities;

import dev.blocky.app.ytd.windows.api.WindowsExplorer;
import dev.blocky.app.ytd.windows.api.WindowsRegistry;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.controlsfx.control.InfoOverlay;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;

import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static dev.blocky.app.ytd.handler.ActionHandler.invalidAction;
import static dev.blocky.app.ytd.handler.ActionHandler.validAction;
import static dev.blocky.app.ytd.handler.DownloadHandler.audioFormat;
import static dev.blocky.app.ytd.handler.DownloadHandler.audioQuality;
import static dev.blocky.app.ytd.handler.TrayIconHandler.sendErrorPushNotification;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.regex.Pattern.CASE_INSENSITIVE;

public class YTDownloader
{
    public static void downloadTrack(TextArea detailArea, String link, String videoID, InfoOverlay thumbnail, String title, String authorName)
    {
        String appHome = WindowsRegistry.getApplicationHome();

        File outputFile = new File(STR."\{appHome}\\Downloads\\\{title}.\{audioFormat}");
        String absoluteOutputFilePath = outputFile.getAbsolutePath();

        String audioAspect = String.valueOf(audioQuality);

        Task<Void> backgroundTask = new Task<>()
        {
            @Override
            protected Void call() throws Exception
            {
                ProcessBuilder downloadBuilder = new ProcessBuilder().command
                        (
                                STR."\{appHome}\\yt-dlp.exe",
                                "--extract-audio",
                                "--audio-format", audioFormat,
                                "--audio-quality", audioAspect,
                                "--output", absoluteOutputFilePath,
                                link
                        );

                Process downloadProcess = downloadBuilder.start();

                if (downloadProcess.waitFor() != 0)
                {
                    InputStream errorStream = downloadProcess.getErrorStream();

                    StringWriter writer = new StringWriter();
                    IOUtils.copy(errorStream, writer, UTF_8);

                    String error = writer.toString();

                    Platform.runLater(() -> invalidAction(detailArea, error));
                    return null;
                }

                return null;
            }
        };

        backgroundTask.setOnSucceeded(_ ->
                Platform.runLater(() ->
                {
                    try
                    {
                        validAction(detailArea, "Downloaded and converted track successfully...");

                        WindowsExplorer.highlightFile(detailArea, outputFile);

                        AudioFile audioFile = AudioFileIO.read(outputFile);
                        AudioHeader audioHeader = audioFile.getAudioHeader();

                        String bitRate = audioHeader.getBitRate();

                        long trackLength = audioHeader.getTrackLength();

                        Duration duration = Duration.ofSeconds(trackLength);

                        long SS = duration.toSecondsPart();
                        long MM = duration.toMinutesPart();
                        long HH = duration.toHours();

                        String formattedDuration = String.format("%02dh %02dm %02ds", HH, MM, SS);

                        String thumbnailText = STR."""
                                    Channel: \{authorName}
                                    Duration: \{formattedDuration}
                                    Bitrate: \{bitRate} kbit/s
                                """;

                        String imgLink = STR."https://img.youtube.com/vi/\{videoID}/mqdefault.jpg";

                        Image thumbnailImage = new Image(imgLink);

                        ImageView thumbnailView = new ImageView(thumbnailImage);
                        thumbnailView.setFitWidth(405);
                        thumbnailView.setFitHeight(227.81);

                        thumbnail.setContent(thumbnailView);
                        thumbnail.setText(thumbnailText);
                    }
                    catch (Exception e)
                    {
                        sendErrorPushNotification(detailArea, e);
                        invalidAction(detailArea, ExceptionUtils.getStackTrace(e));
                    }
                })
        );

        new Thread(backgroundTask).start();
    }

    public static String getVideoID(String url)
    {
        Pattern YOUTUBE_PATTERN = Pattern.compile("^http(s)?://(www.|m.|music.)?youtu(be.com|.be)/(watch[?]v=|shorts/)[\\w-]{11}((&|[?])[\\w=?&]+)?$", CASE_INSENSITIVE);
        Matcher YOUTUBE_MATCHER = YOUTUBE_PATTERN.matcher(url);

        if (YOUTUBE_MATCHER.matches())
        {
            int lastSlash = url.lastIndexOf('/');
            String videoIDRaw = url.substring(lastSlash + 1);

            if (videoIDRaw.startsWith("watch?v="))
            {
                return videoIDRaw.substring(8, 19);
            }

            return videoIDRaw.substring(0, 11);
        }
        return null;
    }
}
