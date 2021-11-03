package ru.wert.datapik.utils.common.components;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;

import static ru.wert.datapik.utils.images.BtnImages.BTN_ADD_SEARCH_IMG;

public class BtnSearchProduct {

    private Button btnSearchProduct;

    public void create(Button btnSearchProduct){
        this.btnSearchProduct = btnSearchProduct;

        btnSearchProduct.setTooltip(new Tooltip("Найти/добавить\nизделие"));
        btnSearchProduct.setGraphic(new ImageView(BTN_ADD_SEARCH_IMG));
        btnSearchProduct.setOnAction(event -> {});
    }
}
