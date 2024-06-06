package ru.wert.tubus.chogori.entities.materials;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.Getter;
import ru.wert.tubus.client.entity.models.Material;
import ru.wert.tubus.client.entity.models.MaterialGroup;
import ru.wert.tubus.chogori.components.BtnGlobe;
import ru.wert.tubus.chogori.components.BtnMenuMaterialsColumns;
import ru.wert.tubus.chogori.common.treeView.Item_TreeView;
import ru.wert.tubus.chogori.application.services.ChogoriServices;
import ru.wert.tubus.chogori.setteings.ChogoriSettings;

public class Material_PatchController {

    @FXML
    @Getter
    private HBox hboxButtons;

    @FXML
    private VBox vboxMain;

    @Getter private Material_TableView tableView;
    private Object modifyingClass; //класс, от которого зависит отображаемый список в таблице (Folder, Product, Material)
    private SelectionMode mode; //SelectionMode.SINGLE, SelectionMode.MULTIPLE
//    @Getter private MenuButton btnShowFilter;
    @Getter private MenuButton btnShowColumns;


    public void initMaterialsTableView(Item_TreeView<Material, MaterialGroup> treeView, Object modifyingClass, SelectionMode mode, boolean useContextMenu){
        if(useContextMenu) //Решается окончательно использование контекстного меню
            useContextMenu = ChogoriSettings.CH_CURRENT_USER.getUserGroup().isEditMaterials();
        tableView = new Material_TableView(ChogoriServices.CH_QUICK_MATERIALS, treeView, "МАТЕРИАЛ", useContextMenu);
        tableView.setModifyingClass(modifyingClass);
        tableView.getSelectionModel().setSelectionMode(mode);
        VBox.setVgrow(tableView, Priority.ALWAYS);
        vboxMain.getChildren().add(tableView);

//        CH_SEARCH_FIELD.setSearchableTableController(tableView);
        tableView.updateView();

    }

    public void initMaterialsToolBar(boolean useBtnGlobe, boolean useBtnColumns){
        Button btnGlobe = new BtnGlobe<>(tableView);
        MenuButton btnMenuColumns = new BtnMenuMaterialsColumns(tableView);

        if(useBtnGlobe) hboxButtons.getChildren().addAll(btnGlobe);
        if(useBtnColumns) hboxButtons.getChildren().add(btnMenuColumns);
    }


}
