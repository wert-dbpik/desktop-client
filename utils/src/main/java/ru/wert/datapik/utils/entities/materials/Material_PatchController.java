package ru.wert.datapik.utils.entities.materials;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.Getter;
import ru.wert.datapik.client.entity.models.Material;
import ru.wert.datapik.client.entity.models.MaterialGroup;
import ru.wert.datapik.utils.common.components.BtnGlobe;
import ru.wert.datapik.utils.common.components.BtnMenuMaterialsColumns;
import ru.wert.datapik.utils.common.treeView.Item_TreeView;

import static ru.wert.datapik.utils.services.ChogoriServices.CH_QUICK_MATERIALS;
import static ru.wert.datapik.utils.setteings.ChogoriSettings.CH_CURRENT_USER;
import static ru.wert.datapik.utils.statics.UtilStaticNodes.CH_SEARCH_FIELD;

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
            useContextMenu = CH_CURRENT_USER.getUserGroup().isEditMaterials();
        tableView = new Material_TableView(CH_QUICK_MATERIALS, treeView, "МАТЕРИАЛ", useContextMenu);
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
