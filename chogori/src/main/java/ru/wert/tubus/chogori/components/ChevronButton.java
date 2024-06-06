package ru.wert.tubus.chogori.components;

import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ChevronButton extends Button {
    private final String text1;
    private final Image image1;
    private final String text2;
    private final Image image2;
    private double draftPos;

    /**
     * Конструктор
     * @param text1 String tooltip РАЗВЕРНУТЬ
     * @param image1 Image картинка
     * @param text2 String tooltip СВЕРНУТЬ
     * @param image2 Image картинка
     * @param divider SplitPane.Divider
     */
    public ChevronButton(String text1, Image image1, double posMax, // Позиция, когда шеврон меняет направление
                         String text2, Image image2, double posStart, //Исходная позиция при открытии окна
                         SplitPane.Divider divider) {
        this.text1 = text1;
        this.image1 = image1;
        this.text2 = text2;
        this.image2 = image2;

        setId("patchButton");

        //Первоначальное состояние
        setButtonRollFwd();

        setOnAction(event -> {
            if(draftPos > posMax) {
                divider.setPosition(posStart);
                setButtonRollFwd();
            } else {
                divider.setPosition(1.0);
                setButtonRollBack();
            }
        });

        divider.positionProperty().addListener((observable, oldValue, newValue) -> {
            draftPos = newValue.doubleValue();
            if(newValue.doubleValue() > posMax){
                setButtonRollBack();
            } else
                setButtonRollFwd();
        });

    }

    /**
     * Кнопка РАЗВЕРНУТЬ
     */
    public void setButtonRollFwd(){
        setTooltip(new Tooltip(text1));
        setGraphic(new ImageView(image1));
    }

    /**
     * Кнопка СВЕРНУТЬ
     */
    public void setButtonRollBack(){
        setTooltip(new Tooltip(text2));
        setGraphic(new ImageView(image2));
    }
}
