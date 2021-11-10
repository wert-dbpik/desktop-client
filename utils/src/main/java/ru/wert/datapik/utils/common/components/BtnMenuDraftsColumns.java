package ru.wert.datapik.utils.common.components;

import javafx.application.Platform;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.MenuButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import ru.wert.datapik.utils.entities.drafts.Draft_TableView;

import static ru.wert.datapik.utils.images.BtnImages.BTN_COLUMNS_IMG;

/**
 * Класс описывает кнопку, при нажатии на которую на экране рядом с кнопкой появляется всплывающее окно
 * с чек-боксами, содержащие статус выводимых в таблицу чертежей: ДЕЙСТВУЮЩИЕ, ЗАМЕНЕННЫЕ, АННУЛИРОВАННЫЕ
 */
public class BtnMenuDraftsColumns extends MenuButton {

    public BtnMenuDraftsColumns(Draft_TableView tableView) {

        setGraphic(new ImageView(BTN_COLUMNS_IMG));
        setTooltip(new Tooltip("Показать колонки"));

        setId("patchButton");

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

        //ТИП ДОКУМЕНТА
        CustomMenuItem useDraftType = new CustomMenuItem();
        CheckBox cbUseDraftType = new CheckBox("Тип/стр");
        cbUseDraftType.setSelected(tableView.isShowDraftType());
        useDraftType.setContent(cbUseDraftType);
        useDraftType.setHideOnClick(false);

        //СТАТУС
        CustomMenuItem useStatus = new CustomMenuItem();
        CheckBox cbUseStatus = new CheckBox("Статус");
        cbUseStatus.setSelected(tableView.isShowStatus());
        useStatus.setContent(cbUseStatus);
        useStatus.setHideOnClick(false);

        //ИСХОДНОЕ НАИМЕНОВАНИЕ ФАЙЛА
        CustomMenuItem useInitialName = new CustomMenuItem();
        CheckBox cbUseInitialName = new CheckBox("Исходное наименование");
        cbUseInitialName.setSelected(tableView.isShowInitialName());
        useInitialName.setContent(cbUseInitialName);
        useInitialName.setHideOnClick(false);

        //ДОКУМЕНТ СОЗДАН
        CustomMenuItem useCreationTime = new CustomMenuItem();
        CheckBox cbUseCreationTime = new CheckBox("Документ создан");
        cbUseCreationTime.setSelected(tableView.isShowCreationTime());
        useCreationTime.setContent(cbUseCreationTime);
        useCreationTime.setHideOnClick(false);

        //ПРИМЕЧАНИЕ
        CustomMenuItem useNote = new CustomMenuItem();
        CheckBox cbUseNote = new CheckBox("Примечание");
        cbUseNote.setSelected(tableView.isShowNote());
        useNote.setContent(cbUseNote);
        useNote.setHideOnClick(false);

        getItems().addAll(useId, useIdentity, useDraftType, useStatus, useInitialName, useCreationTime, useNote);

        showingProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue) {

                Platform.runLater(() -> {
                    tableView.showTableColumns(cbUseId.isSelected(), cbUseIdentity.isSelected(),
                            cbUseDraftType.isSelected(), cbUseStatus.isSelected(), cbUseInitialName.isSelected(),
                            cbUseCreationTime.isSelected(), cbUseNote.isSelected());
                    tableView.updateView();
                    tableView.refresh();
                });
            }
        });
    }
}
