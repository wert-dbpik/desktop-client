package ru.wert.datapik.utils.common.components;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;

import static ru.wert.datapik.utils.images.BtnImages.BTN_ADD_SEARCH_IMG;

public class BtnSearchFolder {

    private Button btnSearchFolder;

    public void create(Button btnSearchFolder){
        this.btnSearchFolder = btnSearchFolder;

        btnSearchFolder.setTooltip(new Tooltip("Найти/добавить\nпапку"));
        btnSearchFolder.setGraphic(new ImageView(BTN_ADD_SEARCH_IMG));
        btnSearchFolder.setOnAction(event -> {});
    }
}
