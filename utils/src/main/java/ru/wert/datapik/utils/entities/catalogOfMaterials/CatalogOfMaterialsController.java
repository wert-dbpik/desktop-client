package ru.wert.datapik.utils.entities.catalogOfMaterials;

import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.Getter;
import ru.wert.datapik.client.entity.models.Material;
import ru.wert.datapik.client.entity.models.MaterialGroup;
import ru.wert.datapik.utils.common.tableView.CatalogTableView;
import ru.wert.datapik.utils.entities.material_groups.MaterialGroup_TreeView;
import ru.wert.datapik.utils.entities.material_groups._MaterialGroup_TreeViewPatch;
import ru.wert.datapik.utils.entities.materials.Material_Patch;
import ru.wert.datapik.utils.entities.materials.Material_PatchController;

import static ru.wert.datapik.utils.images.BtnImages.*;

public class CatalogOfMaterialsController {

    @FXML
    private VBox vbCatalog;

    @FXML
    private StackPane spMaterial;

    @FXML @Getter
    private HBox catalogButtons;

    @Getter
    private HBox materialsButtons;

    @FXML
    private Label lblCatalog;

    private MaterialGroup_TreeView<Material> catalogTreeView;

    @Getter private CatalogTableView<Material, MaterialGroup> materialTableView;



    @FXML
    void initialize() {

        //Создаем панели инструментов
        createCatalog_ToolBar();
//        createMaterials_ToolBar(true,  true);

        //Создаем связанные между собой панели каталога и изделий
        createCatalog_TreeView();
        createMaterials_TableView();

        catalogTreeView.setConnectedForm(materialTableView);

    }

    /**
     * дерево КАТАЛОГА
     */
    private void createCatalog_TreeView() {

        catalogTreeView = _MaterialGroup_TreeViewPatch.create();

        vbCatalog.getChildren().add(catalogTreeView);

//        catalogTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newGroup) -> {
//            materialTableView.updateCatalogView((TreeItem<MaterialGroup>) newGroup, false);
//        });

        catalogTreeView.setOnMouseClicked((e)->{
            if(e.getButton() == MouseButton.PRIMARY)
                materialTableView.updateTableView();
        });

    }

    /**
     * ТАБЛИЦА ИЗДЕЛИЙ
     */
    private void createMaterials_TableView() {

        Material_Patch materialPatch = new Material_Patch().create();
        Material_PatchController controller = materialPatch.getMaterialPatchController();

        controller.initMaterialsTableView(catalogTreeView, null, SelectionMode.SINGLE, true);
        controller.initMaterialsToolBar(true, true);
        materialTableView = controller.getTableView();
        materialsButtons = controller.getHboxButtons();

        Parent parent = materialPatch.getParent();
        spMaterial.getChildren().add(parent);

    }

    /**
     * ИНСТРУМЕНТАЛЬНАЯ ПАНЕЛЬ ДЛЯ КАТАЛОГА ИЗДЕЛИЙ
     */
    private void createCatalog_ToolBar(){
        //Кнопка "Свернуть каталог"
        Button btnCatalogRollUp = new Button();
        btnCatalogRollUp.setGraphic(new ImageView(BTN_ROLLUP_IMG));
        btnCatalogRollUp.setTooltip(new Tooltip("Свернуть каталог"));
        btnCatalogRollUp.setOnAction((e)->{
            catalogTreeView.foldTreeView();
        });

        //Кнопка "Развернуть каталог"
        Button btnCatalogRollDown = new Button();
        btnCatalogRollDown.setGraphic(new ImageView(BTN_ROLLDOWN_IMG));
        btnCatalogRollDown.setTooltip(new Tooltip("Развернуть каталог"));
        btnCatalogRollDown.setOnAction((e)->{
            catalogTreeView.unfoldTreeView();
        });


        //При клике правой кнопкой на надписи "КАТАЛОГ" открывается меню "Добавить" в корень
        lblCatalog.setOnMouseClicked(e -> {
            if(e.getButton().equals(MouseButton.SECONDARY)) {
                catalogTreeView.getSelectionModel().clearSelection();
                catalogTreeView.getContextMenu().show(lblCatalog, Side.BOTTOM, 0.0, 5.0);
            }
        });

        catalogButtons.getChildren().addAll(btnCatalogRollUp, btnCatalogRollDown);
    }

    /**
     * ИНСТРУМЕНТАЛЬНАЯ ПАНЕЛЬ для ТАБЛИЦЫ ИЗДЕЛИЙ
     */
//    private void createMaterials_ToolBar(boolean useBtnGlobe, boolean useBtnColumns){
//        Button btnMaterialsGlobe = new BtnGlobe<>(materialTableView);
//
////        Button btnMaterialsGlobe = new BtnGlobe(productTableView);
//
//        MenuButton btnMenuMaterialsColumns = new BtnMenuMaterialsColumns((Material_TableView) materialTableView);
//
//        if(useBtnGlobe) materialsButtons.getChildren().addAll(btnMaterialsGlobe);
//        if(useBtnColumns) materialsButtons.getChildren().add(btnMenuMaterialsColumns);
//    }


}
