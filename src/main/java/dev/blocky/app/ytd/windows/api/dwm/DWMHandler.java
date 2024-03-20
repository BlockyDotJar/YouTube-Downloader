/**
 * custom_window - A small collection of utility methods to customize a JavaFX stage.
 * Copyright (C) 2022 M. Oguz Tas (mimoguz - https://github.com/mimoguz)
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
package dev.blocky.app.ytd.windows.api.dwm;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;

public class DWMHandler
{
    private final WinDef.HWND hwnd;

    private DWMHandler(WinDef.HWND hwnd)
    {
        this.hwnd = hwnd;
    }

    public static void dwmSetBooleanValue(DWMAttribute attribute, boolean value)
    {
        DWMHandler window = findWindow();

        if (window == null)
        {
            return;
        }

        WinDef.BOOL bool = new WinDef.BOOL(value);
        WinDef.BOOLByReference boolByReference = new WinDef.BOOLByReference(bool);

        DWMSupport.INSTANCE.DwmSetWindowAttribute
                (
                        window.hwnd,
                        attribute.value,
                        boolByReference,
                        WinDef.BOOL.SIZE
                );
    }

    public static void dwmSetIntValue(DWMAttribute attribute, int value)
    {
        DWMHandler window = findWindow();

        if (window == null)
        {
            return;
        }

        WinDef.DWORD dword = new WinDef.DWORD(value);
        WinDef.DWORDByReference dwordByReference = new WinDef.DWORDByReference(dword);

        DWMSupport.INSTANCE.DwmSetWindowAttribute
                (
                        window.hwnd,
                        attribute.value,
                        dwordByReference,
                        WinDef.DWORD.SIZE
                );
    }

    public static DWMHandler findWindow()
    {
        WinDef.HWND hwnd = User32.INSTANCE.FindWindow(null, "YouTube-Downloader");

        if (hwnd != null)
        {
            return new DWMHandler(hwnd);
        }

        return null;
    }

    public static void setAcrylicStyle(boolean useImmersiveDarkMode)
    {
        dwmSetBooleanValue(DWMAttribute.DWMWA_USE_IMMERSIVE_DARK_MODE, useImmersiveDarkMode);
        dwmSetIntValue(DWMAttribute.DWMWA_SYSTEMBACKDROP_TYPE, DWMAttribute.DWMSBT_TRANSIENTWINDOW.value);
    }
}
