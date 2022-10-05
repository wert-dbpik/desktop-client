package ru.wert.datapik.chogori.entities.material_groups.commands;

import javafx.scene.control.TreeItem;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.entity.models.Material;
import ru.wert.datapik.client.entity.models.MaterialGroup;
import ru.wert.datapik.chogori.common.commands.ICommand;
import ru.wert.datapik.chogori.entities.material_groups.MaterialGroup_TreeView;
import ru.wert.datapik.winform.warnings.Warning1;
import ru.wert.datapik.winform.warnings.Warning2;

import java.util.ArrayList;
import java.util.List;

import static ru.wert.datapik.chogori.services.ChogoriServices.*;
import static ru.wert.datapik.winform.warnings.WarningMessages.*;

@Slf4j
public class MaterialGroup_DeleteCommand implements ICommand {

    private MaterialGroup item;
    private MaterialGroup_TreeView<MaterialGroup> treeView;

    private List<MaterialGroup> materialGroups, notDeletedMaterialGroups;
    private List<Material> materials, notDeletedMaterials;


    /**
     *
     * @param treeView MaterialGroup_TreeView
     */
    public MaterialGroup_DeleteCommand(MaterialGroup item, MaterialGroup_TreeView<MaterialGroup> treeView) {
        this.item = item;
        this.treeView = treeView;

        materialGroups = new ArrayList<>();
        materials = new ArrayList<>();

        notDeletedMaterials = new ArrayList<>();
        notDeletedMaterialGroups = new ArrayList<>();
    }

    @Override
    public void execute() {
        //После удаления таблица "подтянется вверх" и поэтому нужна позиция первого из удаляемых элементов
        int row = treeView.getSelectionModel().getSelectedIndex();
        TreeItem<MaterialGroup> treeItem = treeView.getSelectionModel().getSelectedItem();
            try {

                List<TreeItem<MaterialGroup>> allDeletingTreeItems  = treeView.findAllChildren(treeItem);

                //Посчитаем удаляемые папки
                for(TreeItem<MaterialGroup> mgItem : allDeletingTreeItems){
                    materialGroups.add(mgItem.getValue());
                }
                materialGroups.add(treeItem.getValue()); //Добавляем исходную

                //Посчитаем удаляемые изделия
                for(MaterialGroup pg : materialGroups){
                    materials.addAll(CH_QUICK_MATERIALS.findAllByGroupId(pg.getId()));
                }

                //Произведем удаление
                if(materials.isEmpty()){ //Если папки пустые
                    deleteMaterialGroups();
                } else {
                    boolean answer = Warning2.create($ATTENTION, String.format("Вы уверены, что хотите удалить папку '%s'\n" +
                            "и все входящие в папку материалы?", item.getName()), "Востановить удаленное будет трудно!");
                    if(answer) {
                        deleteProducts();
                        deleteMaterialGroups();
                    }

                }

                update(row);

                if(!notDeletedMaterialGroups.isEmpty() || !notDeletedMaterials.isEmpty())
                    Warning1.create($ATTENTION, "Некоторые элементы не удалось удалить!",
                            "Возможно, они имеют зависимости");

            } catch (Exception e) {
                Warning1.create($ATTENTION, $ERROR_WHILE_DELETING_ITEM, $ITEM_IS_BUSY_MAYBE);
                log.error("При удалении группы материалов {} произошла ошибка {} по причине {}",
                        item.getName(), e.getMessage(), e.getCause());
            }

    }

    private void deleteMaterialGroups() {
        for(MaterialGroup mg : materialGroups){
            boolean res = CH_MATERIAL_GROUPS.delete(mg);
            if(res){
                log.info("Удалена группа материалов {}", mg.getName());
            } else {
                log.info("Не удалось удалить группу материалов {}", mg.getName());
                notDeletedMaterialGroups.add(mg);
            }
        }
    }

    private void deleteProducts(){
        for(Material m : materials){
            boolean res = CH_MATERIALS.delete(m);
            if(res){
                log.info("Удален материал {}", m.toUsefulString());
            } else {
                log.info("Не удалось удалить материал {}", m.getName());
                notDeletedMaterials.add(m);
            }
        }
    }

    private void update(int row) {
        treeView.updateView();
        treeView.getSelectionModel().select(row);
        treeView.scrollTo(row);
    }
}
