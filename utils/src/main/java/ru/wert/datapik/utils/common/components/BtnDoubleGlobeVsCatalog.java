package ru.wert.datapik.utils.common.components;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import ru.wert.datapik.client.entity.models.Folder;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.utils.common.tableView.ItemTableView;
import ru.wert.datapik.utils.entities.folders.Folder_TableView;

import static ru.wert.datapik.utils.images.BtnImages.*;
import static ru.wert.datapik.utils.services.ChogoriServices.CH_QUICK_FOLDERS;

public class BtnDoubleGlobeVsCatalog{

    private ItemTableView<Item> folderTableView;

    public BtnDoubleGlobeVsCatalog(ItemTableView<Item> folderTableView) {
        this.folderTableView = folderTableView;
    }

    public Button create(){

        BtnDouble btnGlobalOrCatalog = new BtnDouble(
                BTN_GLOBE_IMG, "Все комплекты чертежей",
                BTN_CATALOG_IMG, "Каталог");
        btnGlobalOrCatalog.setOnAction(e->{
            if(btnGlobalOrCatalog.getLogicProperty()) {
                //Все комплекты чертежей
                folderTableView.setGlobalOn(false);

            } else {
                folderTableView.setGlobalOn(true);

            }
            folderTableView.updateTableView();
        });
        return btnGlobalOrCatalog;
    }
}
