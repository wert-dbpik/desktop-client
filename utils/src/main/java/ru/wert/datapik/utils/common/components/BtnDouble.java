package ru.wert.datapik.utils.common.components;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Getter;
import lombok.Setter;


public class BtnDouble extends Button {

    @Getter@Setter
    boolean logic;
    private final ImageView imageOFF;
    private final String textOFF;
    private final ImageView imageON;
    private final String textON;

    public BtnDouble(Image imageOFF, String textOFF, Image imageON, String textON, boolean initState ) {
        super();
        this.imageOFF = new ImageView(imageOFF);
        this.textOFF = textOFF;
        this.imageON = new ImageView(imageON);
        this.textON = textON;

        setId("patchButton");

        switchButton(initState);
        //Кнопку нажали
        setOnMousePressed(e->{
            switchButton(!logic);
        });
    }

    public boolean getLogicProperty(){
        return logic;
    }

    private void switchButton(boolean state) {
        if (state) {
            setGraphic(imageON);
            setTooltip(new Tooltip(textON));
        } else {
            setGraphic(imageOFF);
            setTooltip(new Tooltip(textOFF));
        }
        logic = state;

    }

}
