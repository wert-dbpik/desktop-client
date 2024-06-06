package ru.wert.tubus.chogori.components;

import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.control.ComboBox;

public class AppComboSkin<T> extends ComboBoxListViewSkin<T> {

    public AppComboSkin(ComboBox<T> comboBox) {
        super(comboBox);
        getSkinnable().focusedProperty().addListener((source, ov, nv) -> {
            if (!nv) {
                setTextFromTextFieldIntoComboBoxValue();
            }
        });
    }


}
