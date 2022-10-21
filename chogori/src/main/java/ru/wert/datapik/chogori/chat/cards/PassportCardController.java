package ru.wert.datapik.chogori.chat.cards;

import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import ru.wert.datapik.chogori.application.passports.OpenPassportsEditorTask;
import ru.wert.datapik.chogori.application.passports.PassportsEditorController;
import ru.wert.datapik.chogori.common.components.VBoxPassport;
import ru.wert.datapik.chogori.tabs.AppTab;
import ru.wert.datapik.client.entity.models.Passport;

import static ru.wert.datapik.chogori.application.services.ChogoriServices.CH_PASSPORTS;
import static ru.wert.datapik.chogori.chat.SideRoomDialogController.MESSAGE_WIDTH;
import static ru.wert.datapik.chogori.statics.AppStatic.CHAT_WIDTH;
import static ru.wert.datapik.chogori.statics.UtilStaticNodes.CH_TAB_PANE;

/**
 * Controller писывает поведение карточки с наименованием комплекта чертежей, передаваемой через ЧАТ
 */
public class PassportCardController {

    @FXML
    private VBox vbPassportsName;


    public void init(String strId) {
        Passport passport = CH_PASSPORTS.findById(Long.valueOf(strId));
        String passportName = passport.getName();
        VBoxPassport vb = new VBoxPassport(passport, "00");
        vb.setStyle("-fx-padding: 0 10 0 10;");
        vb.setPrefWidth(CHAT_WIDTH * MESSAGE_WIDTH);
        vb.setId("draftInChat");

        vbPassportsName.getChildren().add(vb);


         /* При двойном клике на карточку, производится проверка
         * если вкладка с чертежами уже открыта, то данные карточки передаются в существующий контроллер
         * если вкладка закрыта, то контроллер вкладки создается вновь*/
        vbPassportsName.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2) {

                AppTab pane = CH_TAB_PANE.tabIsAvailable("Картотека");
                if(pane != null){
                    PassportsEditorController controller = (PassportsEditorController) pane.getTabController();
                    controller.openPassportFromChat(passport);
                } else {
                    OpenPassportsEditorTask openPassportsTask = new OpenPassportsEditorTask();
                    openPassportsTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
                            t -> {
                                PassportsEditorController controller = openPassportsTask.getValue();
                                controller.openPassportFromChat(passport);
                            });

                    Thread thread = new Thread(openPassportsTask);
                    thread.setDaemon(true);
                    thread.start();
                }

            }

        });

    }
}
