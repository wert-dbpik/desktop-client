package ru.wert.datapik.utils.common.components;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;

import static ru.wert.datapik.utils.images.BtnImages.BTN_ROLLUP_IMG;

public class BtnRollUp extends Button {

    public BtnRollUp() {
        setId("patchButton");
        setGraphic(new ImageView(BTN_ROLLUP_IMG));
        setTooltip(new Tooltip("Свернуть"));
    }
}
