package ru.wert.tubus.chogori.entities.passports;

import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.text.Text;
import ru.wert.tubus.client.entity.models.Passport;

import java.util.List;

public class Passport_Manipulator {

    private final Passport_TableView tableView;

    public Passport_Manipulator(Passport_TableView tableView) {
        this.tableView = tableView;

        tableView.setOnDragDetected(event->{
            Dragboard db = tableView.startDragAndDrop(TransferMode.MOVE);

            ClipboardContent context = new ClipboardContent();
            context.putString(copyToClipboard());
            db.setContent(context);

            String shownString = "Чертежи";
            Text t = new Text(shownString);
            WritableImage image = t.snapshot(null, null);

            db.setDragViewOffsetY(25.0f);
            db.setDragView(image);

            event.consume();
        });


    }

    public String copyToClipboard() {
        StringBuilder sb = new StringBuilder();
        List<Passport> selectedPassports = tableView.getSelectionModel().getSelectedItems();

        sb.append("pik!");
        for (Passport draft : selectedPassports) {
            sb.append(" PP#");
            sb.append(draft.getId());
        }

        return sb.toString();

    }

}
