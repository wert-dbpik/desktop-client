package ru.wert.datapik.utils.entities.drafts;

import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import javafx.scene.text.Text;
import ru.wert.datapik.client.entity.models.Draft;
import ru.wert.datapik.client.entity.models.Folder;
import ru.wert.datapik.utils.common.utils.ClipboardUtils;
import ru.wert.datapik.utils.entities.drafts.commands.Draft_DeleteCommand;
import ru.wert.datapik.winform.warnings.Warning2;

import java.util.Arrays;
import java.util.List;

import static ru.wert.datapik.utils.services.ChogoriServices.CH_QUICK_DRAFTS;
import static ru.wert.datapik.winform.warnings.WarningMessages.$ATTENTION;

public class Draft_Manipulator {

    private final Draft_TableView tableView;

    public Draft_Manipulator(Draft_TableView tableView) {
        this.tableView = tableView;

        tableView.setOnKeyPressed(e->{
            if (e.getCode() == KeyCode.DELETE) {
                List<Draft> selectedItems = tableView.getAllSelectedItems();
                if (selectedItems != null && !selectedItems.isEmpty()) {
                    tableView.getCommands().delete(e, selectedItems);
                }
            }

            if ((e.getCode() == KeyCode.C && e.isControlDown()) || (e.getCode() == KeyCode.INSERT && e.isControlDown())) {
                String str = cutItems(); //(CTRL + C) вырезаем
                ClipboardUtils.copyToClipboardText(str);
            }

        });

        tableView.setOnDragDetected(event->{
            Dragboard db = tableView.startDragAndDrop(TransferMode.MOVE);

            ClipboardContent context = new ClipboardContent();
            context.putString(cutItems());
            db.setContent(context);

            String shownString = "Чертежи";
            Text t = new Text(shownString);
            WritableImage image = t.snapshot(null, null);

            db.setDragViewOffsetY(25.0f);
            db.setDragView(image);

            event.consume();
        });
    }

    public String cutItems(){
        StringBuilder sb = new StringBuilder();
        List<Draft> selectedDrafts = tableView.getSelectionModel().getSelectedItems();

        sb.append("pik!");
        for(Draft draft: selectedDrafts){
            sb.append(" DR#");
            sb.append(draft.getId());
        }

        return sb.toString();

    }

    public boolean pastePossible(String str){
        if(str == null || !str.startsWith("pik!")) return false;
        str = str.replace("pik!", "");
        str = str.trim();
        String[] pasteData = str.split(" ", -1);
        for(String s : pasteData) {
            String clazz = Arrays.asList(s.split("#", -1)).get(0);
            List<Folder> selectedFolders = tableView.getSelectedFolders();
            if (!clazz.equals("PG") || selectedFolders == null || selectedFolders.size() > 1)
                return false;
        }

        return true;
    }


    public void pasteItems(String str){
        String[] pasteData = (str.replace("pik!", "").trim()).split(" ", -1);
        Folder selectedFolder = tableView.getSelectedFolders().get(0);
        for(String s : pasteData) {
            Long pastedItemId = Long.valueOf(Arrays.asList(s.split("#", -1)).get(1));
            Draft draft = CH_QUICK_DRAFTS.findById(pastedItemId);
            draft.setFolder(selectedFolder);
            CH_QUICK_DRAFTS.update(draft);
        }

    }
}
