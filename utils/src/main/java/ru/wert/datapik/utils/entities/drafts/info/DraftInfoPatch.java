package ru.wert.datapik.utils.entities.drafts.info;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;
import ru.wert.datapik.client.entity.models.Draft;
import ru.wert.datapik.winform.window_decoration.WindowDecoration;

import java.io.IOException;

import static ru.wert.datapik.winform.statics.WinformStatic.WF_MAIN_STAGE;

public class DraftInfoPatch {

    public void create(Draft draft, Event event){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/utils-fxml/drafts/draftInfo.fxml"));
            Parent parent = loader.load();
            DraftInfoController controller = loader.getController();
            controller.init(draft);

            Stage owner = (event == null)?
                    WF_MAIN_STAGE: (Stage)((Node)event.getSource()).getScene().getWindow();

            new WindowDecoration("Информация о чертеже", parent, false, owner, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
