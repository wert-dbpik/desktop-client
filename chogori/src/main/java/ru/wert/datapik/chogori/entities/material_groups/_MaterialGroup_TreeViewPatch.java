package ru.wert.datapik.chogori.entities.material_groups;

import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import ru.wert.datapik.client.entity.models.MaterialGroup;

import static ru.wert.datapik.chogori.application.services.ChogoriServices.CH_MATERIAL_GROUPS;
import static ru.wert.datapik.chogori.setteings.ChogoriSettings.CH_CURRENT_USER;

public class _MaterialGroup_TreeViewPatch {

    public static MaterialGroup_TreeView create(){
        MaterialGroup rootMaterialGroup = new MaterialGroup(0L, "Материал");
        rootMaterialGroup.setId(1L);
        boolean useContextMenu = CH_CURRENT_USER.getUserGroup().isEditMaterials();
        MaterialGroup_TreeView catalogTreeView = new MaterialGroup_TreeView(CH_MATERIAL_GROUPS, rootMaterialGroup, useContextMenu);
        catalogTreeView.buildTree();

        VBox.setVgrow(catalogTreeView, Priority.ALWAYS);

        return catalogTreeView;
    }
}
