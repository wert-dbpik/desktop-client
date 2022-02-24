package ru.wert.datapik.utils.entities.drafts;

import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.text.Text;
import ru.wert.datapik.client.entity.models.Draft;
import ru.wert.datapik.utils.common.tableView.RoutineTableView;

import java.util.List;

public class Draft_Manipulator {

    private final RoutineTableView<Draft> tableView;


    public Draft_Manipulator(RoutineTableView<Draft> tableView) {
        this.tableView = tableView;

        tableView.setOnDragDetected(event->{
            Dragboard db = tableView.startDragAndDrop(TransferMode.MOVE);

            ClipboardContent context = new ClipboardContent();
            context.putString(cutDrafts());
            db.setContent(context);

            String shownString = "Чертежи";
            Text t = new Text(shownString);
            WritableImage image = t.snapshot(null, null);

            db.setDragViewOffsetY(25.0f);
            db.setDragView(image);

            event.consume();
        });
    }

    public String cutDrafts(){
        StringBuilder sb = new StringBuilder();
        List<Draft> selectedDrafts = tableView.getSelectionModel().getSelectedItems();

        sb.append("pik!");
        for(Draft draft: selectedDrafts){
            sb.append(" DR#");
            sb.append(draft.getId());
        }

        return sb.toString();

    }
}
