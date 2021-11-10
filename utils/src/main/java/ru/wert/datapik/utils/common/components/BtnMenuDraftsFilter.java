package ru.wert.datapik.utils.common.components;

import javafx.application.Platform;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.MenuButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import ru.wert.datapik.utils.entities.drafts.Draft_TableView;

import static ru.wert.datapik.utils.images.BtnImages.BTN_FILTER_IMG;

/**
 * Класс описывает кнопку, при нажатии на которую на экране рядом с кнопкой появляется всплывающее окно
 * с чек-боксами, содержащие статус выводимых в таблицу чертежей: ДЕЙСТВУЮЩИЕ, ЗАМЕНЕННЫЕ, АННУЛИРОВАННЫЕ
 */
public class BtnMenuDraftsFilter extends MenuButton {

    public BtnMenuDraftsFilter(Draft_TableView tableView) {
        setId("patchButton");


        setGraphic(new ImageView(BTN_FILTER_IMG));
        setTooltip(new Tooltip("Показать фильтр"));

        setId("menu-button-no-arrow");

        CustomMenuItem legal = new CustomMenuItem();
        CheckBox cbLegal = new CheckBox("Действующие");
        cbLegal.setSelected(tableView.isShowLegal());
        legal.setContent(cbLegal);
        legal.setHideOnClick(false);

        CustomMenuItem changed = new CustomMenuItem();
        CheckBox cbChanged = new CheckBox("Замененные");
        cbChanged.setSelected(tableView.isShowChanged());
        changed.setContent(cbChanged);
        changed.setHideOnClick(false);

        CustomMenuItem annulled = new CustomMenuItem();
        CheckBox cbAnnulled = new CheckBox("Аннулированные");
        cbAnnulled.setSelected(tableView.isShowAnnulled());
        annulled.setContent(cbAnnulled);
        annulled.setHideOnClick(false);

        getItems().addAll(legal, changed, annulled);

        showingProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue) {
                tableView.setShowLegal(cbLegal.isSelected());
                tableView.setShowChanged(cbChanged.isSelected());
                tableView.setShowAnnulled(cbAnnulled.isSelected());

                Platform.runLater(() -> {
                    tableView.updateView();
                    tableView.refresh();
                });
            }
        });
    }
}
