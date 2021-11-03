package ru.wert.datapik.utils.entities.folders;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import ru.wert.datapik.client.entity.models.Folder;

public class Folder_Columns {

    /**
     * ID
     */
    public static TableColumn<Folder, String> createTcId(){
        TableColumn<Folder, String> tcId = new TableColumn<>("ID");
        tcId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tcId.setStyle("-fx-alignment: CENTER;");
        return tcId;
    };

    /**
     * ДЕЦИМАЛЬНЫЙ НОМЕР
     */
    public static TableColumn<Folder, String> createTcDecNumber(){
        TableColumn<Folder, String> tcDecNumber = new TableColumn<>("Децимальный\nномер");
        tcDecNumber.setCellValueFactory(cd->{
            Folder folder = cd.getValue();
            String decNumber = folder.getDecNumber();
            return new ReadOnlyStringWrapper(decNumber);
        });
        tcDecNumber.setMinWidth(120);
        return tcDecNumber;
    };

    /**
     * ПОЛНОЕ НАИМЕНОВАНИЕ НОМЕР
     */
    public static TableColumn<Folder, String> createTcFullName(){
        TableColumn<Folder, String> tcFullName = new TableColumn<>("Полное\nНаименование");
        tcFullName.setCellValueFactory(cd->{
            Folder folder = cd.getValue();
            String fullName = folder.toUsefulString();
            return new ReadOnlyStringWrapper(fullName);
        });
        tcFullName.setMinWidth(120);
        return tcFullName;
    };



    /**
     * НАИМЕНОВАНИЕ
     */
    public static TableColumn<Folder, String> createTcName(){
        TableColumn<Folder, String> tcName = new TableColumn<>("Наименование");
        tcName.setCellValueFactory(new PropertyValueFactory<>("name"));
        return tcName;
    };


    /**
     * ПРИМЕЧАНИЕ
     */
    public static TableColumn<Folder, String> createTcNote(){
        TableColumn<Folder, String> tcNote = new TableColumn<>("Примечание");
        tcNote.setCellValueFactory(new PropertyValueFactory<>("note"));
        tcNote.setMinWidth(120);
        return tcNote;
    };
}
