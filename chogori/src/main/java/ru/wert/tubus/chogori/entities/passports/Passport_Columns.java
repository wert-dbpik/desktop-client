package ru.wert.tubus.chogori.entities.passports;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import ru.wert.tubus.client.entity.models.Passport;
import ru.wert.tubus.chogori.components.VBoxPassport;
import ru.wert.tubus.chogori.setteings.ChogoriSettings;

import static ru.wert.tubus.chogori.statics.Comparators.createIntegerComparatorForStringColumn;
import static ru.wert.tubus.chogori.statics.Comparators.createLabelComparator;

public class Passport_Columns {

    /**
     * ID
     */
    public static TableColumn<Passport, String> createTcId(){
        TableColumn<Passport, String> tcId = new TableColumn<>("ID");
        tcId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tcId.setStyle("-fx-alignment: CENTER;");
        tcId.setComparator(createIntegerComparatorForStringColumn(tcId));
        return tcId;
    };

    /**
     * ПАССПОРТ - ИДЕНТИФИКАТОР
     */
    public static TableColumn<Passport, VBox> createTcPassport(){
        TableColumn<Passport, VBox> tcPassport = new TableColumn<>("Идентификатор");
        //Passport выводится в виде label
        tcPassport.setCellValueFactory(cd -> {

            Passport passport = cd.getValue();
            VBoxPassport vBoxPassport = new VBoxPassport(passport, "");
            return new ReadOnlyObjectWrapper<>(vBoxPassport);

        });

        tcPassport.setComparator((o1, o2) -> {
            String num1 = ((Label)o1.lookup("#number")).getText();
            String num2 = ((Label)o2.lookup("#number")).getText();

            return num1.compareTo(num2);
        });

        tcPassport.setMinWidth(150);
        return tcPassport;
    };

    /**
     * ДЕЦИМАЛЬНЫЙ НОМЕР
     */
    public static TableColumn<Passport, Label> createTcPassportNumber() {
        TableColumn<Passport, Label> tcPassportNumber = new TableColumn<>("Дец.номер");

        tcPassportNumber.setCellValueFactory(cd -> {
            Passport passport = cd.getValue();
            String prefix = passport.getPrefix().getName().equals("-") ? "" : passport.getPrefix().getName() + ".";
            if(!ChogoriSettings.CH_SHOW_PREFIX && passport.getPrefix().equals(ChogoriSettings.CH_DEFAULT_PREFIX)) prefix = "";
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
        tcPassportNumber.setComparator(createLabelComparator(tcPassportNumber));
        tcPassportNumber.setMinWidth(150);
        return tcPassportNumber;
    }

    /**
     * НАИМЕНОВАНИЕ
     */
    public static TableColumn<Passport, Label> createTcPassportName() {
        TableColumn<Passport, Label> tcPassportName = new TableColumn<>("Наименование");

        tcPassportName.setCellValueFactory(cd -> {
            Passport passport = cd.getValue();
            String name = passport.getName();

            Label lblName = new Label(name);

            return new ReadOnlyObjectWrapper<>(lblName);

        });
        tcPassportName.setComparator(createLabelComparator(tcPassportName));
        tcPassportName.setMinWidth(150);
        return tcPassportName;
    }

//    /**
//     * Если CH_SHOW_PREFIX = false, то дефолтный префикс отсекается, остальные остаются
//     */
//    public static String getDecNumber(Passport passport) {
//        if(passport == null || passport.getNumber() == null) return "";
//        String decNumber = passport.getNumber();
//        if(!CH_SHOW_PREFIX){
//            String prefix = decNumber.split("\\.", -1)[0];
//            if(prefix != null && !prefix.equals("-") && !prefix.equals("")){
//                Prefix foundPrefix = CH_QUICK_PREFIXES.findByName(prefix);
//                if(foundPrefix != null && foundPrefix != CH_DEFAULT_PREFIX)
//                    decNumber = decNumber.substring(prefix.length()+1);
//            }
//        }
//        return decNumber;
//    }
//
//    public static String getFolderFullName(Passport passport){
//        if(passport == null) return "";
//        if (passport.getNumber() == null ||
//                passport.getNumber().equals("-") ||
//                passport.getNumber().equals("")
//        )
//            return passport.getName();
//        return getDecNumber(passport) + ", " + passport.getName();
//    }

}
