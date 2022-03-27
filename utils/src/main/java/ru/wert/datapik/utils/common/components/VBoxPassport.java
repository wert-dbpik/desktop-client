package ru.wert.datapik.utils.common.components;

import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import ru.wert.datapik.client.entity.models.Passport;
import ru.wert.datapik.client.entity.models.Prefix;
import ru.wert.datapik.utils.popups.CopyPopup;

import static ru.wert.datapik.utils.setteings.ChogoriSettings.*;

public class VBoxPassport extends VBox {
    private final CopyPopup copyPopupControl = new CopyPopup();

    public VBoxPassport(Passport passport, String variant) {
        String decNumber = passport.getNumber();
        if(!variant.equals("00")) decNumber += variant;
        Prefix prefix = passport.getPrefix();
        if(CH_SHOW_PREFIX)
            if(!prefix.equals(CH_DEFAULT_PREFIX) || !prefix.getName().equals("-"))
                decNumber = prefix.getName() + "." + decNumber;



        setSpacing(0.0);
        Label lbDecNumber = new Label(decNumber);
        lbDecNumber.setId("number");
//        lbDecNumber.setStyle("-fx-text-fill: black; -fx-font-size: 4mm; -fx-font-weight: bold");
        lbDecNumber.setOnMouseClicked((e)->{
            if(e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2){
                copyPopupControl.showHint(lbDecNumber);
            }
        });

        switch(passport.getNumber().substring(0,1)){
            case "7" :
                lbDecNumber.setStyle("-fx-text-fill: darkgreen; -fx-font-size: 14;  -fx-font-weight: bold;");
                break;
            case "3" :
                lbDecNumber.setStyle("-fx-text-fill: darkblue; -fx-font-size: 14;  -fx-font-weight: bold;");
                break;
            case "4" :
                lbDecNumber.setStyle("-fx-text-fill: saddlebrown; -fx-font-size: 14;  -fx-font-weight: bold;");
                break;
            default :
                lbDecNumber.setStyle("-fx-text-fill: black; -fx-font-size: 14;  -fx-font-weight: bold;");
        }

        String name = passport.getName();

        Label lbName = new Label(name);
        lbName.setStyle("-fx-text-fill: black; -fx-font-size: 14;  -fx-font-style: oblique");



        getChildren().addAll(lbName, lbDecNumber);

    }

}
