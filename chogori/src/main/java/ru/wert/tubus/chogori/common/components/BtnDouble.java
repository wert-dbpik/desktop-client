package ru.wert.tubus.chogori.common.components;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class BtnDouble extends Button {


    private BooleanProperty stateProperty = new SimpleBooleanProperty();
    public BooleanProperty getStateProperty(){return stateProperty;}

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

        stateProperty.addListener((observable, oldValue, newValue) -> {
            switchButton(newValue);
        });

        setId("patchButton");

        switchButton(initState);
        //Кнопку нажали
        setOnMousePressed(e->{
            switchButton(!stateProperty.get());
        });
    }

    private void switchButton(boolean state) {
        if (state) {
            setGraphic(imageON);
            setTooltip(new Tooltip(textON));
        } else {
            setGraphic(imageOFF);
            setTooltip(new Tooltip(textOFF));
        }
        stateProperty.set(state);

    }

}
