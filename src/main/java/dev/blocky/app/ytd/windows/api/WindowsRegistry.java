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

import com.sun.jna.platform.win32.WinReg;

import static com.sun.jna.platform.win32.Advapi32Util.registryGetIntValue;
import static com.sun.jna.platform.win32.Advapi32Util.registryGetStringValue;
import static java.lang.StringTemplate.STR;

public class WindowsRegistry
{
    public static String getApplicationHome()
    {
        WinReg.HKEY hKey = WinReg.HKEY_LOCAL_MACHINE;
        String registryKey = "SOFTWARE\\WOW6432Node\\YouTube-Downloader";
        String registryValue = "YouTube-Downloader_HOME";

        String appHome = registryGetStringValue(hKey, registryKey, registryValue);

        if (appHome == null)
        {
            String programFilesX86 = System.getProperty("ProgramFiles(x86)");
            appHome = STR."\{programFilesX86}\\YouTube-Downloader";
        }

        return appHome;
    }

    public static String getApplicationVersion()
    {
        WinReg.HKEY hKey = WinReg.HKEY_LOCAL_MACHINE;
        String registryKey = "SOFTWARE\\WOW6432Node\\YouTube-Downloader";
        String registryValue = "YouTube-Downloader_Version";

        return registryGetStringValue(hKey, registryKey, registryValue);
    }

    public static String getWindowsDisplayVersion()
    {
        WinReg.HKEY hKey = WinReg.HKEY_LOCAL_MACHINE;
        String registryKey = "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion";
        String registryValue = "DisplayVersion";

        return registryGetStringValue(hKey, registryKey, registryValue);
    }

    public static int getMessageDuration()
    {
        WinReg.HKEY hKey = WinReg.HKEY_CURRENT_USER;
        String registryKey = "Control Panel\\Accessibility";
        String registryValue = "MessageDuration";

        return registryGetIntValue(hKey, registryKey, registryValue);
    }
}
