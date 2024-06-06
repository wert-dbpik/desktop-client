package ru.wert.tubus.chogori.components;

import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ExpandButton extends Button {

    private final String text1;
    private final Image image1;
    private final String text2;
    private final Image image2;
    private double draftPos;
    private double previewerPos;

    /**
     * Конструктор
     * @param text1 String tooltip РАЗВЕРНУТЬ
     * @param image1 Image картинка
     * @param text2 String tooltip СВЕРНУТЬ
     * @param image2 Image картинка
     */
    public ExpandButton(String text1, Image image1,
                        String text2, Image image2,
                        SplitPane.Divider draftDivider,
                        SplitPane.Divider previewerDivider) {
        this.text1 = text1;
        this.image1 = image1;
        this.text2 = text2;
        this.image2 = image2;

        setId("patchButton");

        setButtonExpand();

        setOnAction(event -> {
            if(draftPos < 0.2 || previewerPos < 0.2) {
                draftDivider.setPosition(0.4);
                previewerDivider.setPosition(0.4);
                setButtonExpand();
            } else {
                draftDivider.setPosition(0.0);
                previewerDivider.setPosition(0.0);
                setButtonReduce();
            }
        });

        draftDivider.positionProperty().addListener((observable, oldValue, newValue) -> {
            draftPos = newValue.doubleValue();
            if(newValue.doubleValue() < 0.2){
                setButtonReduce();
            } else {
                setButtonExpand();
            }
        });

        previewerDivider.positionProperty().addListener((observable, oldValue, newValue) -> {
            previewerPos = newValue.doubleValue();
            if(newValue.doubleValue() < 0.2){
                setButtonReduce();
            } else {
                setButtonExpand();
            }
        });


    }

    /**
     * Кнопка РАЗВЕРНУТЬ
     */
    public void setButtonExpand(){
        setTooltip(new Tooltip(text1));
        setGraphic(new ImageView(image1));
    }

    /**
     * Кнопка СВЕРНУТЬ
     */
    public void setButtonReduce(){
        setTooltip(new Tooltip(text2));
        setGraphic(new ImageView(image2));
    }
}
