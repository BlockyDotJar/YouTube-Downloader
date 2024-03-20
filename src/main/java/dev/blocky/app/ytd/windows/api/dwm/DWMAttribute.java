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

public enum DWMAttribute
{
    DWMSBT_TRANSIENTWINDOW(3),

    DWMWA_USE_IMMERSIVE_DARK_MODE(20),

    DWMWA_SYSTEMBACKDROP_TYPE(38);

    final int value;

    DWMAttribute(int value)
    {
        this.value = value;
    }
}
