package ru.wert.datapik.utils.entities.materials;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import ru.wert.datapik.client.entity.models.AnyPart;
import ru.wert.datapik.client.entity.models.Material;
import ru.wert.datapik.utils.statics.Wrappers;

public class Material_Columns {

    /**
     * ID
     */
    public static TableColumn<Material, String> createTcId(){
        TableColumn<Material, String> tcId = new TableColumn<>("ID");
        tcId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tcId.setStyle("-fx-alignment: CENTER;");
        tcId.setMinWidth(40);
        return tcId;
    };

    /**
     * МАТЕРИАЛ
     */
    public static TableColumn<Material, String> createTcName(){
        TableColumn<Material, String> tcName = new TableColumn<>("Материал");
        tcName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tcName.setMinWidth(150);
        return tcName;
    };

    /**
     * PartId
     */
    public static TableColumn<Material, String> createTcPartId(){
        TableColumn<Material, String> tcPartId = new TableColumn<>("PartId");
        tcPartId.setCellValueFactory(cd -> {
            AnyPart part = cd.getValue().getAnyPart();
            String partId = "";
            if(part != null) partId = String.valueOf(cd.getValue().getAnyPart().getId());
            return new ReadOnlyStringWrapper(partId);
        });
        tcPartId.setMinWidth(40);
        tcPartId.setStyle("-fx-alignment: CENTER;");
        return tcPartId;
    };

    /**
     * CatId
     */
    public static TableColumn<Material, String> createTcCatId(){
        TableColumn<Material, String> tcCatId = new TableColumn<>("CatId");
        tcCatId.setCellValueFactory(cd -> {
            String catId = String.valueOf(cd.getValue().getCatalogGroup().getId());
            return new ReadOnlyStringWrapper(catId);
        });
        tcCatId.setMinWidth(40);
        tcCatId.setStyle("-fx-alignment: CENTER;");
        return tcCatId;
    };

    /**
     * MatType
     */
    public static TableColumn<Material, String> createTcMatType(){
        TableColumn<Material, String> tcMatType = new TableColumn<>("Расчетный тип");
        tcMatType.setCellValueFactory(cd -> Wrappers.getNameFromItem(cd.getValue().getMatType()));
        tcMatType.setMinWidth(120);
        tcMatType.setSortable(false);
        tcMatType.setStyle("-fx-alignment: CENTER;");
        return tcMatType;
    };

    /**
     * S
     */
    public static TableColumn<Material, String> createTcParamS(){
        TableColumn<Material, String> tcParamS = new TableColumn<>("S");
        tcParamS.setCellValueFactory(cd -> Wrappers.getStringFromDouble(cd.getValue().getParamS()));
        tcParamS.setMinWidth(40);
        tcParamS.setSortable(false);
        tcParamS.setStyle("-fx-alignment: CENTER;");
        return tcParamS;
    };

    /**
     * X
     */
    public static TableColumn<Material, String> createTcParamX(){
        TableColumn<Material, String> tcParamX = new TableColumn<>("X");
        tcParamX.setCellValueFactory(cd -> Wrappers.getStringFromDouble(cd.getValue().getParamX()));
        tcParamX.setCellValueFactory(new PropertyValueFactory<>("paramX"));
        tcParamX.setMinWidth(80);
        tcParamX.setSortable(false);
        tcParamX.setStyle("-fx-alignment: CENTER;");
        return tcParamX;
    }

    /**
     * ПРИМЕЧАНИЕ
     */
    public static TableColumn<Material, String> createTcNote(){
        TableColumn<Material, String> tcNote = new TableColumn<>("Примечание");
        tcNote.setCellValueFactory(new PropertyValueFactory<>("note"));
        tcNote.setMinWidth(120);
        tcNote.setSortable(false);
        return tcNote;
    };

}