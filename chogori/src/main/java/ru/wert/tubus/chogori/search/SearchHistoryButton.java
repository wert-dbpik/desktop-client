package ru.wert.tubus.chogori.search;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;

import static ru.wert.tubus.chogori.images.BtnImages.BTN_SEARCH_HISTORY_IMG;

public class SearchHistoryButton extends Button {

    public SearchHistoryButton() {
        setId("menu-button-no-arrow");
        setGraphic(new ImageView(BTN_SEARCH_HISTORY_IMG));
        setTooltip(new Tooltip("История поиска"));

        setOnAction(e->{
            new SearchHistoryPopup().showHistory();
        });
    }
}
