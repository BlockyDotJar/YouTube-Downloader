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

import javafx.application.HostServices;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import one.jpro.platform.mdfx.MarkdownView;
import org.controlsfx.control.InfoOverlay;
import org.controlsfx.control.ToggleSwitch;

public class NodeCreator
{
    public Button createButton(String text, double x, double y, boolean disabled)
    {
        Button button = new Button(text);
        button.setDisable(disabled);
        button.setTranslateX(x);
        button.setTranslateY(y);
        return button;
    }

    public TextField createTextField(String promptText, double x, double y)
    {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        textField.setMinWidth(425);
        textField.setTranslateX(x);
        textField.setTranslateY(y);
        return textField;
    }

    public <T> ComboBox<T> createComboBox(String text, double x, double y, ObservableList<T> items)
    {
        ComboBox<T> comboBox = new ComboBox<>(items);
        comboBox.setPromptText(text);
        comboBox.setTranslateX(x);
        comboBox.setTranslateY(y);
        return comboBox;
    }

    public TextArea createTextArea(String text, double x, double y)
    {
        TextArea textArea = new TextArea();
        textArea.setPrefSize(425, 50);
        textArea.setEditable(false);
        textArea.setTranslateX(x);
        textArea.setTranslateY(y);
        textArea.setText(text);
        return textArea;
    }

    public Region createRegion(double x, double y)
    {
        Region region = new Region();

        ObservableList<String> styleClasses = region.getStyleClass();
        styleClasses.add("region");

        region.setPrefSize(425, 248);
        region.setTranslateX(x);
        region.setTranslateY(y);
        return region;
    }

    public InfoOverlay createInfoOverlay(double x, double y)
    {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(405);
        imageView.setFitHeight(227.81);

        InfoOverlay infoOverlay = new InfoOverlay(imageView, null);
        infoOverlay.setPrefSize(405, 227.81);
        infoOverlay.setTranslateX(x);
        infoOverlay.setTranslateY(y);
        return infoOverlay;
    }

    public Alert createAlert(Alert.AlertType alertType, String title, String headerText, Node content)
    {
        Alert alert = new Alert(alertType);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setContent(content);

        alert.setTitle(title);
        alert.setHeaderText(headerText);
        return alert;
    }

    public Alert createAlert(Alert.AlertType alertType, String title, String headerText, String contentText)
    {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        return alert;
    }

    public MarkdownView createMarkdownView(HostServices hostServices, String markdown)
    {
        MarkdownView markdownView = new MarkdownView(markdown)
        {
            @Override
            public void setLink(Node node, String link, String description)
            {
                node.setOnMouseClicked(_ -> hostServices.showDocument(link));
            }
        };

        Insets insets = new Insets(10, 10, 10, 10);

        markdownView.setMinWidth(755);
        markdownView.setMaxWidth(755);
        markdownView.setPadding(insets);

        return markdownView;
    }

    public ScrollPane createScrollPane(Node node, double x, double y)
    {
        ScrollPane scrollPane = new ScrollPane(node);
        scrollPane.setMinSize(765, 390);
        scrollPane.setMaxSize(765, 390);
        scrollPane.setFitToHeight(true);
        scrollPane.setTranslateX(x);
        scrollPane.setTranslateY(y);
        return scrollPane;
    }

    public Label createLabel(String text, double x, double y, boolean appearanceLabel)
    {
        Label label = new Label(text);

        ObservableList<String> styleClasses = label.getStyleClass();

        if (appearanceLabel)
        {
            styleClasses.add("appearance-label");
        }

        label.setTranslateX(x);
        label.setTranslateY(y);
        return label;
    }

    public Hyperlink createHyperlink(HostServices hostServices, String text, String releaseLink, double x, double y)
    {
        Hyperlink hyperlink = new Hyperlink(text);
        hyperlink.setTranslateX(x);
        hyperlink.setTranslateY(y);
        hyperlink.setOnAction(_ -> hostServices.showDocument(releaseLink));
        return hyperlink;
    }

    public ToggleSwitch createToggleSwitch(double x, double y, boolean selected)
    {
        ToggleSwitch toggleSwitch = new ToggleSwitch();
        toggleSwitch.setSelected(selected);
        toggleSwitch.setTranslateX(x);
        toggleSwitch.setTranslateY(y);
        return toggleSwitch;
    }
}
