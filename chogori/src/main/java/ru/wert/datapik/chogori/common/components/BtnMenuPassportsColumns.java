package ru.wert.datapik.chogori.common.components;

import javafx.application.Platform;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.MenuButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import ru.wert.datapik.chogori.entities.passports.Passport_TableView;

import static ru.wert.datapik.chogori.images.BtnImages.BTN_COLUMNS_IMG;
import static ru.wert.datapik.chogori.setteings.ChogoriSettings.CH_CURRENT_USER;

/**
 * Класс описывает кнопку, при нажатии на которую на экране рядом с кнопкой появляется всплывающее окно
 * с чек-боксами, содержащие статус выводимых в таблицу чертежей: ДЕЙСТВУЮЩИЕ, ЗАМЕНЕННЫЕ, АННУЛИРОВАННЫЕ
 */
public class BtnMenuPassportsColumns extends MenuButton {

    public BtnMenuPassportsColumns(Passport_TableView tableView) {
        setId("patchButton");

        setGraphic(new ImageView(BTN_COLUMNS_IMG));
        setTooltip(new Tooltip("Показать колонки"));

        setId("menu-button-no-arrow");

        //ID
        CustomMenuItem useId = new CustomMenuItem();
        CheckBox cbUseId = new CheckBox("ID");
        cbUseId.setSelected(tableView.isShowId());
        useId.setContent(cbUseId);
        useId.setHideOnClick(false);

        //ИДЕНТИФИКАТОР
        CustomMenuItem useIdentity = new CustomMenuItem();
        CheckBox cbUseIdentity = new CheckBox("Идентификатор");
        cbUseIdentity.setSelected(tableView.isShowIdentity());
        useIdentity.setContent(cbUseIdentity);
        useIdentity.setHideOnClick(false);

        if(CH_CURRENT_USER.getUserGroup().isAdministrate())
            getItems().addAll(useId);
        getItems().addAll(useIdentity);

        showingProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue) {

                Platform.runLater(() -> {
                    tableView.showTableColumns(cbUseId.isSelected(), cbUseIdentity.isSelected());
                    tableView.updateView();
                    tableView.refresh();
                });
            }
        });
    }
}
