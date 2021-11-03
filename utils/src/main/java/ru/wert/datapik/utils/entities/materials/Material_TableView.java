package ru.wert.datapik.utils.entities.materials;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import lombok.Getter;
import lombok.Setter;
import ru.wert.datapik.client.entity.models.Material;
import ru.wert.datapik.client.entity.models.MaterialGroup;
import ru.wert.datapik.client.interfaces.ItemService;
import ru.wert.datapik.utils.common.contextMenuACC.FormView_ACCController;
import ru.wert.datapik.utils.common.tableView.CatalogTableView;
import ru.wert.datapik.utils.common.tableView.RoutineTableView;
import ru.wert.datapik.utils.common.treeView.Item_TreeView;
import ru.wert.datapik.utils.entities.materials.commands._Material_Commands;

import java.util.ArrayList;
import java.util.List;

import static ru.wert.datapik.utils.services.ChogoriServices.CH_MATERIALS;

public class Material_TableView extends CatalogTableView<Material, MaterialGroup> {

    private static final String accWindowRes = "/utils-fxml/materials/materialACC.fxml";
    @Getter private final _Material_Commands commands;
    @Getter@Setter private String searchedText = "";
    @Setter private Object modifyingClass;
    @Getter@Setter private Object modifyingItem; //Product или Folder
    @Getter@Setter  private List<Material> currentItemList = new ArrayList<>(); //Searchable
    @Getter@Setter private FormView_ACCController<Material> accController;

    @Getter
    ListProperty<Material> preparedList = new SimpleListProperty<>();
    
    private TableColumn<Material, String> tcId; //Id
    private TableColumn<Material, String> tcPartId; //Part
    private TableColumn<Material, String> tcCatId; //Catalog Id
    private TableColumn<Material, String> tcName; //Catalog Id
    private TableColumn<Material, String> tcMatType; //Расчетный тип материала
    private TableColumn<Material, String> tcParamS; //Толщина, Периметр и т.д
    private TableColumn<Material, String> tcParamX; //Плотность, погонная масса
    private TableColumn<Material, String> tcNote; //Примечание


    //Показывать колонки
    @Getter@Setter private boolean showId; //Id
    @Getter@Setter private boolean showPartId; //Part
    @Getter@Setter private boolean showCatId; //Catalog Id
    @Getter@Setter private boolean showName; //Наименование
    @Getter@Setter private boolean showMatType; //Расчетный тип материала
    @Getter@Setter private boolean showParamS; //Толщина, Периметр и т.д
    @Getter@Setter private boolean showParamX; //Плотность, погонная масса
    @Getter@Setter private boolean showNote; //Примечание


    public Material_TableView(ItemService<Material> itemService, Item_TreeView<Material, MaterialGroup> catalogTree, String itemName, boolean useContextMenu) {
        super(itemService, catalogTree, itemName);

        commands = new _Material_Commands(this);

        //Создаем изначальное контекстное меню, чтобы оно могло открыться при клике в пустом месте
        if(useContextMenu) createContextMenu();
    }

    @Override
    public void setTableColumns() {

        tcId = Material_Columns.createTcId(); //Id
        tcPartId = Material_Columns.createTcPartId(); //Part
        tcCatId = Material_Columns.createTcCatId(); //Catalog Id
        tcName = Material_Columns.createTcName(); //Наименование
        tcMatType = Material_Columns.createTcMatType(); //Расчетный тип материала
        tcParamS = Material_Columns.createTcParamS(); //Толщина, Периметр и т.д
        tcParamX = Material_Columns.createTcParamX(); //Плотность, погонная масса
        tcNote = Material_Columns.createTcNote(); //Примечание

        getColumns().addAll(tcId, tcPartId, tcCatId, tcName, tcMatType, tcParamS, tcParamX, tcNote);

    }

    @Override
    public String getAccWindowRes() {
        return accWindowRes;
    }

    /**
     * Метод выключает ненужные столбцы
     */
    public void showTableColumns(boolean useTcId, boolean useTcPartId, boolean useTcCatId, boolean useTcName, boolean useTcMatType,
                                 boolean useTcParamS, boolean useTcParamX, boolean useTcNote){
        tcId.setVisible(useTcId);
        showId = useTcId;

        tcPartId.setVisible(useTcPartId);
        showId = useTcPartId;

        tcCatId.setVisible(useTcCatId);
        showCatId = useTcCatId;

        tcName.setVisible(useTcName);
        showCatId = useTcName;

        tcMatType.setVisible(useTcMatType);
        showMatType = useTcMatType;

        tcParamS.setVisible(useTcParamS);
        showParamS = useTcParamS;

        tcParamX.setVisible(useTcParamX);
        showParamX = useTcParamX;

        tcNote.setVisible(useTcNote);
        showNote = useTcNote;

    }

    @Override
    public void createContextMenu() {
        contextMenu = new Material_ContextMenu(this, commands, accWindowRes);
        setContextMenu(contextMenu);
    }

    @Override
    public List<Material> prepareList() {
        List<Material> list = new ArrayList<>();
        if(modifyingClass instanceof MaterialGroup){
            if(modifyingItem == null)
                list = CH_MATERIALS.findAll();
            else {
//                list = CH_MATERIALS.findAllBy((MaterialGroup)modifyingItem);
            }
        }

        preparedList.set(FXCollections.observableArrayList(list));

        return list;
    }

    @Override //Searchable
    public void setCurrentItemSearchedList(List<Material> currentItemList) {
        this.currentItemList = currentItemList;
    }


}
