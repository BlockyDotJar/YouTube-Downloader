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
package dev.blocky.app.ytd.windows.api;

import javafx.scene.control.TextArea;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import static dev.blocky.app.ytd.handler.ActionHandler.invalidAction;
import static dev.blocky.app.ytd.handler.TrayIconHandler.sendErrorPushNotification;

public class WindowsExplorer
{
    public static void highlightFile(TextArea detailArea, File file) throws IOException, InterruptedException
    {
        String absoluteFilePath = file.getAbsolutePath();

        ProcessBuilder builder = new ProcessBuilder().command
                (
                        "explorer.exe",
                        "/select,",
                        STR."\"\{absoluteFilePath}\""
                );

        Process process = builder.start();

        if (process.waitFor() != 0)
        {
            InputStream errorStream = process.getErrorStream();

            StringWriter writer = new StringWriter();
            IOUtils.copy(errorStream, writer, StandardCharsets.UTF_8);

            String stackTrace = writer.toString();

            if (!stackTrace.isBlank())
            {
                invalidAction(detailArea, stackTrace);
                sendErrorPushNotification(detailArea, new IllegalStateException(stackTrace));
            }
        }
    }
}
