package ru.wert.datapik.utils.entities.material_groups;

import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import ru.wert.datapik.client.entity.models.MaterialGroup;

import static ru.wert.datapik.utils.services.ChogoriServices.CH_MATERIAL_GROUPS;

public class _MaterialGroup_TreeViewPatch {

    public static MaterialGroup_TreeView create(){
        MaterialGroup rootMaterialGroup = new MaterialGroup(0L, "Материал");
        rootMaterialGroup.setId(1L);
        MaterialGroup_TreeView catalogTreeView = new MaterialGroup_TreeView(CH_MATERIAL_GROUPS, rootMaterialGroup);
        catalogTreeView.buildTree();

        VBox.setVgrow(catalogTreeView, Priority.ALWAYS);

        return catalogTreeView;
    }
}
