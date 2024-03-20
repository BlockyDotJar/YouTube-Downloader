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

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static dev.blocky.app.ytd.handler.ActionHandler.invalidAction;

public class TrayIconHandler
{
    private static final SystemTray tray = SystemTray.getSystemTray();

    private static void sendPushNotification(TextArea detailArea, TrayIcon.MessageType messageType, String caption, String text)
    {
        try
        {
            Image image = Toolkit.getDefaultToolkit().createImage("icon.png");

            TrayIcon trayIcon = new TrayIcon(image);
            trayIcon.setImageAutoSize(true);
            trayIcon.addMouseListener(new MouseAdapter()
            {
                @Override
                public void mouseClicked(MouseEvent e)
                {
                    tray.remove(trayIcon);
                }
            });

            tray.add(trayIcon);

            trayIcon.displayMessage(caption, text, messageType);

            int messageDuration = WindowsRegistry.getMessageDuration();

            TimeUnit.SECONDS.sleep(messageDuration);

            TrayIcon[] trayIconsRaw = tray.getTrayIcons();
            List<TrayIcon> trayIcons = Arrays.asList(trayIconsRaw);

            if (trayIcons.contains(trayIcon))
            {
                tray.remove(trayIcon);
            }
        }
        catch (Exception e)
        {
            sendErrorPushNotification(detailArea, e);
            invalidAction(detailArea, ExceptionUtils.getStackTrace(e));
        }
    }

    public static void sendInfoPushNotification(TextArea detailArea, String caption, String text)
    {
        sendPushNotification(detailArea, TrayIcon.MessageType.INFO, caption, text);
    }

    public static void sendWarningPushNotification(TextArea detailArea, String caption, String text)
    {
        sendPushNotification(detailArea, TrayIcon.MessageType.WARNING, caption, text);
    }

    public static void sendErrorPushNotification(TextArea detailArea, String caption, String text)
    {
        sendPushNotification(detailArea, TrayIcon.MessageType.ERROR, caption, text);
    }

    public static void sendErrorPushNotification(TextArea detailArea, Exception exception)
    {
        Class<?> exceptionClass = exception.getClass();
        String caption = exceptionClass.getName();
        String text = exception.getMessage();

        sendErrorPushNotification(detailArea, caption, text);
    }
}
