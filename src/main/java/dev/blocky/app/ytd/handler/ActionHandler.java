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

import javafx.collections.ObservableList;
import javafx.scene.control.TextArea;

import static java.lang.StringTemplate.STR;

public class ActionHandler
{
    public static void validAction(TextArea detailArea, String text)
    {
        ObservableList<String> styleClass = detailArea.getStyleClass();

        if (styleClass.contains("invalid-text-area"))
        {
            detailArea.clear();
        }

        styleClass.removeAll("invalid-text-area");

        String currentText = detailArea.getText();
        String actualText = "";

        if (currentText != null && !currentText.isBlank())
        {
            actualText = STR."\{currentText}\n";
        }

        detailArea.setText(actualText + text);
    }

    public static void invalidAction(TextArea detailArea, String text)
    {
        ObservableList<String> styleClass = detailArea.getStyleClass();

        if (!styleClass.contains("invalid-text-area"))
        {
            detailArea.clear();
        }

        styleClass.add("invalid-text-area");

        String currentText = detailArea.getText();
        String actualText = "";

        if (currentText != null && !currentText.isBlank())
        {
            actualText = STR."\{currentText}\n";
        }

        detailArea.setText(actualText + text);
    }
}
