package ru.wert.tubus.chogori.components;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;

import static ru.wert.tubus.chogori.images.BtnImages.BTN_ROLLDOWN_IMG;

public class BtnRollDown extends Button {

    public BtnRollDown() {
        setId("patchButton");
        setGraphic(new ImageView(BTN_ROLLDOWN_IMG));
        setTooltip(new Tooltip("Развернуть"));
    }
}
