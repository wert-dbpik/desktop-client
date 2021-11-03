package ru.wert.datapik.utils.common.components;

import javafx.application.Platform;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.MenuButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import ru.wert.datapik.utils.entities.drafts.Draft_TableView;
import ru.wert.datapik.utils.entities.materials.Material_TableView;

import static ru.wert.datapik.utils.images.BtnImages.BTN_COLUMNS_IMG;

/**
 * Класс описывает кнопку, при нажатии на которую на экране рядом с кнопкой появляется всплывающее окно
 * с чек-боксами, содержащие статус выводимых в таблицу чертежей: ДЕЙСТВУЮЩИЕ, ЗАМЕНЕННЫЕ, АННУЛИРОВАННЫЕ
 */
public class BtnMenuMaterialsColumns extends MenuButton {

    public BtnMenuMaterialsColumns(Material_TableView tableView) {
        setMaxHeight(18.0);
        setMinHeight(18.0);
        setPrefHeight(18.0);

        setGraphic(new ImageView(BTN_COLUMNS_IMG));
        setTooltip(new Tooltip("Показать колонки"));

        setId("menu-button-no-arrow");

        //ID
        CustomMenuItem useId = new CustomMenuItem();
        CheckBox cbUseId = new CheckBox("ID");
        cbUseId.setSelected(tableView.isShowId());
        useId.setContent(cbUseId);
        useId.setHideOnClick(false);

        //МАТЕРИАЛ
        CustomMenuItem useName = new CustomMenuItem();
        CheckBox cbUseName = new CheckBox("Материал");
        cbUseName.setSelected(tableView.isShowName());
        useName.setContent(cbUseName);
        useName.setHideOnClick(false);

        //PART ID
        CustomMenuItem usePartId = new CustomMenuItem();
        CheckBox cbUsePartId = new CheckBox("PartId");
        cbUsePartId.setSelected(tableView.isShowPartId());
        usePartId.setContent(cbUsePartId);
        usePartId.setHideOnClick(false);

        //CAT ID
        CustomMenuItem useCatId = new CustomMenuItem();
        CheckBox cbUseCatId = new CheckBox("CatId");
        cbUseCatId.setSelected(tableView.isShowCatId());
        useCatId.setContent(cbUseCatId);
        useCatId.setHideOnClick(false);

        //РАСЧЕТНЫЙ ТИП
        CustomMenuItem useMatType = new CustomMenuItem();
        CheckBox cbUseMatType = new CheckBox("Расчетный тип");
        cbUseMatType.setSelected(tableView.isShowMatType());
        useMatType.setContent(cbUseMatType);
        useMatType.setHideOnClick(false);

        //S
        CustomMenuItem useParamS = new CustomMenuItem();
        CheckBox cbUseParamS = new CheckBox("S");
        cbUseParamS.setSelected(tableView.isShowParamS());
        useParamS.setContent(cbUseParamS);
        useParamS.setHideOnClick(false);

        //X
        CustomMenuItem useParamX = new CustomMenuItem();
        CheckBox cbUseParamX = new CheckBox("X");
        cbUseParamX.setSelected(tableView.isShowParamX());
        useParamX.setContent(cbUseParamX);
        useParamX.setHideOnClick(false);

        //ПРИМЕЧАНИЕ
        CustomMenuItem useNote = new CustomMenuItem();
        CheckBox cbUseNote = new CheckBox("Примечание");
        cbUseNote.setSelected(tableView.isShowNote());
        useNote.setContent(cbUseNote);
        useNote.setHideOnClick(false);

        getItems().addAll(useId, useName, usePartId, useCatId, useMatType, useParamS, useParamX, useNote);

        showingProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue) {

                Platform.runLater(() -> {
                    tableView.showTableColumns(cbUseId.isSelected(), cbUseName.isSelected(),
                            cbUsePartId.isSelected(), cbUseCatId.isSelected(), cbUseMatType.isSelected(),
                            cbUseParamS.isSelected(), cbUseParamX.isSelected(), cbUseNote.isSelected());
                    tableView.updateView();
                    tableView.refresh();
                });
            }
        });
    }
}
