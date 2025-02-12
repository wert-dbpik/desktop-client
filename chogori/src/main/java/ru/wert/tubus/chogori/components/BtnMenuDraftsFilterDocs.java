package ru.wert.tubus.chogori.components;

import javafx.application.Platform;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.MenuButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import ru.wert.tubus.chogori.entities.drafts.Draft_TableView;
import ru.wert.tubus.client.entity.models.Draft;

import java.util.Collections;

import static ru.wert.tubus.chogori.images.BtnImages.BTN_FILTER_DOCS_IMG;
import static ru.wert.tubus.chogori.images.BtnImages.BTN_FILTER_IMG;

/**
 * Класс описывает кнопку, при нажатии на которую на экране рядом с кнопкой появляется всплывающее окно
 * с чек-боксами, содержащие типы документов выводимых в таблицу чертежей: ЧЕРТЕЖИ, 3D, Развертки
 */
public class BtnMenuDraftsFilterDocs extends MenuButton {

    public BtnMenuDraftsFilterDocs(Draft_TableView tableView) {
//        setId("patchButton");


        setGraphic(new ImageView(BTN_FILTER_DOCS_IMG));
        setTooltip(new Tooltip("Показать фильтр документов"));

        setId("menu-button-no-arrow");

        CustomMenuItem itemDraftDocs = new CustomMenuItem();
        CheckBox cbDrafts = new CheckBox("Чертежи");
        cbDrafts.setSelected(tableView.isShowDraftDocks());
        itemDraftDocs.setContent(cbDrafts);
        itemDraftDocs.setHideOnClick(false);

        CustomMenuItem itemDFXDocks = new CustomMenuItem();
        CheckBox cbDFXDocks = new CheckBox("Развертки");
        cbDFXDocks.setSelected(tableView.isShowDFXDocks());
        itemDFXDocks.setContent(cbDFXDocks);
        itemDFXDocks.setHideOnClick(false);

        getItems().addAll(itemDraftDocs, itemDFXDocks);

        showingProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue) {
                tableView.setShowDraftDocks(cbDrafts.isSelected());
                tableView.setShowDFXDocks(cbDFXDocks.isSelected());

                Platform.runLater(() -> {
                    Draft selectedDraft = tableView.getSelectionModel().getSelectedItem();
                    tableView.updateRoutineTableView(Collections.singletonList(selectedDraft), false);
                    tableView.refresh();
                });
            }
        });

    }
}
