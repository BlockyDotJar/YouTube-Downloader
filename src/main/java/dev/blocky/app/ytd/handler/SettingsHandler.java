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

import dev.blocky.app.ytd.windows.api.WindowsRegistry;
import javafx.scene.control.TextArea;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONObject;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static dev.blocky.app.ytd.handler.ActionHandler.invalidAction;
import static dev.blocky.app.ytd.handler.TrayIconHandler.sendErrorPushNotification;

public class SettingsHandler
{
    public static boolean usesImmersiveDarkMode(TextArea detailArea)
    {
        String json = SettingsHandler.readSettings(detailArea);
        JSONObject root = new JSONObject(json);

        return root.getBoolean("immersive-dark-mode");
    }

    public static void writeSettings(TextArea detailArea, String key, Object value)
    {
        try
        {
            String appHome = WindowsRegistry.getApplicationHome();
            File settingsFile = new File(STR."\{appHome}\\settings.json");

            if (!settingsFile.exists())
            {
                settingsFile.createNewFile();

                Path settingsPath = settingsFile.toPath();

                JSONObject root = new JSONObject("{ \"immersive-dark-mode\": false }");
                String json = root.toString(4);

                Files.writeString(settingsPath, json, StandardCharsets.UTF_8);
            }

            Path settingsPath = settingsFile.toPath();

            JSONObject root = new JSONObject(settingsPath);
            root.put(key, value);

            String json = root.toString(4);

            Files.writeString(settingsPath, json);
        }
        catch (Exception e)
        {
            sendErrorPushNotification(detailArea, e);
            invalidAction(detailArea, ExceptionUtils.getStackTrace(e));

        }
    }

    public static String readSettings(TextArea detailArea)
    {
        try
        {
            String appHome = WindowsRegistry.getApplicationHome();
            File settingsFile = new File(STR."\{appHome}\\settings.json");

            if (!settingsFile.exists())
            {
                settingsFile.createNewFile();

                Path settingsPath = settingsFile.toPath();

                JSONObject root = new JSONObject("{ \"immersive-dark-mode\": false }");
                String json = root.toString(4);

                Files.writeString(settingsPath, json, StandardCharsets.UTF_8);
            }

            Path settingsPath = settingsFile.toPath();
            return Files.readString(settingsPath);
        }
        catch (Exception e)
        {
            sendErrorPushNotification(detailArea, e);
            invalidAction(detailArea, ExceptionUtils.getStackTrace(e));
            return null;
        }
    }
}
