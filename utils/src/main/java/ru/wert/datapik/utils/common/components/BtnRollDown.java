package ru.wert.datapik.utils.common.components;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;

import static ru.wert.datapik.utils.images.BtnImages.BTN_ROLLDOWN_IMG;
import static ru.wert.datapik.utils.images.BtnImages.BTN_ROLLUP_IMG;

public class BtnRollDown extends Button {

    public BtnRollDown() {
        setId("patchButton");
        setGraphic(new ImageView(BTN_ROLLDOWN_IMG));
        setTooltip(new Tooltip("Развернуть"));
    }
}
