package ru.wert.tubus.chogori.entities.material_groups;

import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import ru.wert.tubus.client.entity.models.MaterialGroup;
import ru.wert.tubus.chogori.application.services.ChogoriServices;
import ru.wert.tubus.chogori.setteings.ChogoriSettings;

public class _MaterialGroup_TreeViewPatch {

    public static MaterialGroup_TreeView create(){
        MaterialGroup rootMaterialGroup = new MaterialGroup(0L, "Материал");
        rootMaterialGroup.setId(1L);
        boolean useContextMenu = ChogoriSettings.CH_CURRENT_USER.getUserGroup().isEditMaterials();
        MaterialGroup_TreeView catalogTreeView = new MaterialGroup_TreeView(ChogoriServices.CH_MATERIAL_GROUPS, rootMaterialGroup, useContextMenu);
        catalogTreeView.buildTree();

        VBox.setVgrow(catalogTreeView, Priority.ALWAYS);

        return catalogTreeView;
    }
}
