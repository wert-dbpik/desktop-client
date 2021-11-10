package ru.wert.datapik.utils.common.components;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Getter;



public class BtnDouble extends Button {

    @Getter boolean logic = true;

    public BtnDouble(Image image1, String hint1, Image image2, String hint2 ) {
        super();

        setId("patchButton");

        switchBtn1(new ImageView(image1), hint1);
        //Кнопку нажали
        setOnMousePressed(e->{
            if(logic)
                switchBtn2(new ImageView(image2), hint2);
            else
                switchBtn1(new ImageView(image1), hint1);
        });
    }

    public boolean getLogicProperty(){
        return logic;
    }

    /**
     * Устанавливаем кнопку №1
     * @param image1 Изображение
     * @param text1 Подсказка
     */
    private void switchBtn1(ImageView image1, String text1){
        setGraphic(image1);
        setTooltip(new Tooltip(text1));
        logic = true;
    }

    /**
     * Устанавливаем кнопку №2
     * @param image2 Изображение
     * @param text2 Подсказка
     */
    private void switchBtn2(ImageView image2, String text2){
        setGraphic(image2);
        setTooltip(new Tooltip(text2));
        logic = false;
    }


}
