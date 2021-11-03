package ru.wert.datapik.utils.entities.drafts;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import ru.wert.datapik.client.entity.models.Draft;
import ru.wert.datapik.client.entity.models.Passport;
import ru.wert.datapik.utils.common.components.VBoxPassport;
import ru.wert.datapik.utils.popups.HintPopup;
import ru.wert.datapik.winform.enums.EDraftStatus;
import ru.wert.datapik.winform.enums.EDraftType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

import static ru.wert.datapik.winform.statics.WinformStatic.parseLDTtoNormalDate;

public class Draft_Columns {

    /**
     * ID
     */
    public static TableColumn<Draft, String> createTcId(){
        TableColumn<Draft, String> tcId = new TableColumn<>("ID");
        tcId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tcId.setStyle("-fx-alignment: CENTER;");
        tcId.setMinWidth(40);
        return tcId;
    };

    /**
     * ПАССПОРТ - ИДЕНТИФИКАТОР
     */
    public static TableColumn<Draft, VBox> createTcPassport(){
        TableColumn<Draft, VBox> tcPassport = new TableColumn<>("Идентификатор");
        //Passport выводится в виде label
        tcPassport.setCellValueFactory(cd -> {

            Passport passport = cd.getValue().getPassport();
            VBoxPassport vBoxPassport = new VBoxPassport(passport, "00");
            return new ReadOnlyObjectWrapper<>(vBoxPassport);

        });

        tcPassport.setComparator(new Comparator<VBox>() {
            @Override
            public int compare(VBox o1, VBox o2) {
                String num1 = ((Label)o1.lookup("#number")).getText();
                String num2 = ((Label)o2.lookup("#number")).getText();

                return num1.compareTo(num2);
            }
        });

        tcPassport.setMinWidth(150);
        return tcPassport;
    };

    /**
     * ДЕЦИМАЛЬНЫЙ НОМЕР
     */
    public static TableColumn<Draft, Label> createTcDraftNumber() {
        TableColumn<Draft, Label> tcDraftNumber = new TableColumn<>("Децимальный\nномер");

        tcDraftNumber.setCellValueFactory(cd -> {
            Passport passport = cd.getValue().getPassport();
            String prefix = passport.getPrefix().getName().equals("-") ? "" : passport.getPrefix().getName() + ".";
            String decNumber = prefix + passport.getNumber();

            Label lblNumber = new Label(decNumber);

            switch(passport.getNumber().substring(0,1)){
                case "7" :
                    lblNumber.setStyle("-fx-text-fill: darkgreen; -fx-font-size: 14;  -fx-font-weight: bold;");
                    break;
                case "3" :
                    lblNumber.setStyle("-fx-text-fill: darkblue; -fx-font-size: 14;  -fx-font-weight: bold;");
                    break;
                case "4" :
                    lblNumber.setStyle("-fx-text-fill: saddlebrown; -fx-font-size: 14;  -fx-font-weight: bold;");
                    break;
                default :
                    lblNumber.setStyle("-fx-text-fill: black; -fx-font-size: 14;  -fx-font-weight: bold;");
            }

            return new ReadOnlyObjectWrapper<>(lblNumber);

        });
        tcDraftNumber.setMinWidth(150);
        return tcDraftNumber;
    }

    /**
     * НАИМЕНОВАНИЕ
     */
    public static TableColumn<Draft, Label> createTcDraftName() {
        TableColumn<Draft, Label> tcDraftName = new TableColumn<>("Наименование");

        tcDraftName.setCellValueFactory(cd -> {
            Passport passport = cd.getValue().getPassport();
            String name = passport.getName();

            Label lblName = new Label(name);

            return new ReadOnlyObjectWrapper<>(lblName);

        });
        tcDraftName.setMinWidth(150);
        return tcDraftName;
    }

    /**
     * СТАТУС
     */
    public static TableColumn<Draft, Label> createTcStatus(){
        TableColumn<Draft, Label> tcStatus = new TableColumn<>("Статус");
        tcStatus.setCellValueFactory(cd->{
            HintPopup hint;
            Draft draft = cd.getValue();
            Integer statusId = draft.getStatus();
            EDraftStatus status = EDraftStatus.getStatusById(statusId);
            String str = "";
            Label lblStatus = new Label();
            if(status != null){
//                if(status.equals(EDraftStatus.LEGAL)){

                    lblStatus.setText(status.getStatusName());
                    String hintText = draft.getStatusUser().getName() + "\n" + parseLDTtoNormalDate(draft.getStatusTime());

                hint = new HintPopup(lblStatus, hintText, 3.0);

                lblStatus.setOnMouseEntered(e->{
                    hint.showHint();

                });
                lblStatus.setOnMouseExited(e->{
                    hint.closeHint();
                });
            }
            return new ReadOnlyObjectWrapper<>(lblStatus);
        });
        tcStatus.setStyle("-fx-alignment: CENTER;");
        tcStatus.setMinWidth(100);
        return tcStatus;
    };

    /**
     * ПАПКА ХРАНЕНИЯ
     */
    public static TableColumn<Draft, String> createTcFolder(){
        TableColumn<Draft, String> tcFolder = new TableColumn<>("Папка\nхранения");
        tcFolder.setCellValueFactory(new PropertyValueFactory<>("folder"));
        tcFolder.setStyle("-fx-alignment: CENTER;");
        tcFolder.setMinWidth(120);
        return tcFolder;
    };

    /**
     * ИСХОДНОЕ НАЗВАНИЕ ФАЙЛА
     */
    public static TableColumn<Draft, String> createTcInitialDraftName(){
        TableColumn<Draft, String> tcInitialDraftName = new TableColumn<>("Исходное\nнаименование");
        tcInitialDraftName.setCellValueFactory(new PropertyValueFactory<>("initialDraftName"));
        tcInitialDraftName.setStyle("-fx-alignment: CENTER;");
        tcInitialDraftName.setMinWidth(120);
        return tcInitialDraftName;
    };

    /**
     * ТИП ДОКУМЕНТА И СТРАНИЦА
     */
    public static TableColumn<Draft, String> createTcDraftType(){
        TableColumn<Draft, String> tcDraftType = new TableColumn<>("Тип/стр");
        tcDraftType.setCellValueFactory(cd->{
            Draft draft = cd.getValue();
            EDraftType type = EDraftType.getDraftTypeById(draft.getDraftType());
            assert type != null;
            String str = type.getShortName() + "-" + draft.getPageNumber();

            return new ReadOnlyStringWrapper(str);
        });
        tcDraftType.setSortable(false);
        tcDraftType.setMinWidth(80);
        tcDraftType.setStyle("-fx-alignment: CENTER;");
        return tcDraftType;
    };

    /**
     * ИНФОРМАЦИЯ О СОЗДАНИИ
     */
    public static TableColumn<Draft, String> createTcCreation(){
        TableColumn<Draft, String> tcCreation = new TableColumn<>("Документ\nсоздан");
        tcCreation.setCellValueFactory(cd->{
            Draft draft = cd.getValue();
            String str = parseLDTtoNormalDate(draft.getCreationTime());
            return new ReadOnlyStringWrapper(str);
        });

        tcCreation.setStyle("-fx-alignment: CENTER;");
        tcCreation.setMinWidth(120);
        return tcCreation;
    };



    /**
     * ПРИМЕЧАНИЕ
     */
    public static TableColumn<Draft, String> createTcNote(){
        TableColumn<Draft, String> tcNote = new TableColumn<>("Примечание");
        tcNote.setCellValueFactory(new PropertyValueFactory<>("note"));
        tcNote.setMinWidth(120);
        tcNote.setSortable(false);
        return tcNote;
    };
}
