package ru.wert.tubus.chogori.entities.drafts;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.text.Text;
import org.apache.commons.io.FilenameUtils;
import ru.wert.tubus.chogori.components.FileFwdSlash;
import ru.wert.tubus.client.entity.models.Draft;
import ru.wert.tubus.client.entity.models.Folder;
import ru.wert.tubus.chogori.common.utils.ClipboardUtils;
import ru.wert.tubus.chogori.application.services.ChogoriServices;
import ru.wert.tubus.chogori.setteings.ChogoriSettings;
import ru.wert.tubus.winform.enums.EDraftType;
import ru.wert.tubus.winform.enums.EOperation;
import ru.wert.tubus.winform.warnings.Warning2;
import ru.wert.tubus.winform.window_decoration.WindowDecoration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.wert.tubus.chogori.statics.AppStatic.*;
import static ru.wert.tubus.winform.statics.WinformStatic.WF_MAIN_STAGE;
import static ru.wert.tubus.winform.statics.WinformStatic.WF_TEMPDIR;

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

        //Допускается добавление директории, где хотя б один файл соответствует списку
        //Если не выделено ни одной папки, то добавление файлов не допускается
        tableView.setOnDragOver(event -> {
            if(!ChogoriSettings.CH_CURRENT_USER_GROUP.isEditDrafts()) return;
            Dragboard db = event.getDragboard();
            //Если один из форматов не является FILES, то перетаскивание не допускается
            if (db.hasFiles()) {
                List<File> allFiles = new ArrayList<>();
                List<File> content = (List<File>) db.getContent(DataFormat.FILES);
                for (File f : content) {
                    try {
                        if (f.isDirectory()) {
                            List<Path> filesInFolder = Files.walk(f.toPath()).collect(Collectors.toList());
                            for (Path p : filesInFolder) allFiles.add(p.toFile());
                        } else if (f.isFile())
                            allFiles.add(f);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                for (File file : allFiles) {
                    String ext = FilenameUtils.getExtension(file.getName().toLowerCase());
                    if (PDF_EXTENSIONS.contains(ext) ||
                            IMAGE_EXTENSIONS.contains(ext) ||
                            SOLID_EXTENSIONS.contains(ext)||
                            DXF_EXTENSIONS.contains(ext)
                    ) {
                        if(tableView.getModifyingItem() == null){
                            event.acceptTransferModes(TransferMode.NONE);
                        } else {
                            event.acceptTransferModes(TransferMode.MOVE);
                            event.consume();
                            return;
                        }
                    }
                    event.acceptTransferModes(TransferMode.NONE);
                }
            } else
                event.acceptTransferModes(TransferMode.NONE);
        });

        tableView.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                List<File> acceptedFiles = new ArrayList<>();
                List<File> content = (List<File>) db.getContent(DataFormat.FILES);
                for (File f : content) {
                    try {
                        if (f.isDirectory()) {

                            List<Path> filesInFolder = Files.walk(f.toPath())
                                    .filter(file -> PDF_EXTENSIONS.contains(FilenameUtils.getExtension(file.toFile().getName().toLowerCase())) ||
                                                    IMAGE_EXTENSIONS.contains(FilenameUtils.getExtension(file.toFile().getName().toLowerCase())) ||
                                            SOLID_EXTENSIONS.contains(FilenameUtils.getExtension(file.toFile().getName().toLowerCase())) ||
                                            DXF_EXTENSIONS.contains(FilenameUtils.getExtension(file.toFile().getName().toLowerCase()))
                                    )
                                    .collect(Collectors.toList());
                            for (Path p : filesInFolder)
                                acceptedFiles.add(p.toFile());
                        } else if (f.isFile()) {
                            if (PDF_EXTENSIONS.contains(FilenameUtils.getExtension(f.getName().toLowerCase()))
                            || IMAGE_EXTENSIONS.contains(FilenameUtils.getExtension(f.getName().toLowerCase()))
                            || SOLID_EXTENSIONS.contains(FilenameUtils.getExtension(f.getName().toLowerCase()))
                            || DXF_EXTENSIONS.contains(FilenameUtils.getExtension(f.getName().toLowerCase()))
                            ) {
                                acceptedFiles.add(f);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (!acceptedFiles.isEmpty()) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/drafts/draftACC.fxml"));
                        Parent parent = loader.load();
                        Draft_ACCController controller = loader.getController();
                        controller.addDroppedFiles(EOperation.ADD, tableView, tableView.getCommands(), acceptedFiles);
                        new WindowDecoration(EOperation.ADD.getName(), parent, true, WF_MAIN_STAGE);
                        event.consume();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public String cutItems() {
        StringBuilder sb = new StringBuilder();
        List<Draft> selectedDrafts = tableView.getSelectionModel().getSelectedItems();

        sb.append("pik!");
        for (Draft draft : selectedDrafts) {
            sb.append(" DR#");
            sb.append(draft.getId());
        }

        return sb.toString();

    }

    public boolean pastePossible(String str) {
        if (str == null || !str.startsWith("pik!")) return false;
        str = str.replace("pik!", "");
        str = str.trim();
        String[] pasteData = str.split(" ", -1);
        for (String s : pasteData) {
            String clazz = Arrays.asList(s.split("#", -1)).get(0);
            List<Folder> selectedFolders = tableView.getSelectedFolders();
            if (!clazz.equals("DR") || selectedFolders == null || selectedFolders.size() > 1)
                return false;
        }

        return true;
    }


    public void pasteItems(String str) {
        if(!Warning2.create("ВНИМАНИЕ!",
                "Вы уверены, что хотите сделать перемещение?",
                "Отменить будет сложнее.")) return; //ОК, true - уверен

        String[] pasteData = (str.replace("pik!", "").trim()).split(" ", -1);
        Folder selectedFolder = tableView.getSelectedFolders().get(0);
        for (String s : pasteData) {
            Long pastedItemId = Long.valueOf(Arrays.asList(s.split("#", -1)).get(1));
            Draft draft = ChogoriServices.CH_QUICK_DRAFTS.findById(pastedItemId);
            draft.setFolder(selectedFolder);
            ChogoriServices.CH_QUICK_DRAFTS.update(draft);
            Platform.runLater(()->tableView.updateRoutineTableView(Collections.singletonList(draft), false));
        }

        ClipboardUtils.clear();

    }

    public void goDraftsForward() {
        int index = tableView.getSelectionModel().getSelectedIndex();
        int rows = tableView.getItems().size();
        tableView.getSelectionModel().clearSelection(index);
        if(index == -1) {
            if (rows == 1)
                tableView.getSelectionModel().selectFirst();
            else
                tableView.getSelectionModel().select(1);
        }else {
            if(index == rows-1) index=-1;
            tableView.getSelectionModel().select(index + 1);
        }
    }

    public void goDraftsBackward() {
        int index = tableView.getSelectionModel().getSelectedIndex();
        int rows = tableView.getItems().size();
        tableView.getSelectionModel().clearSelection(index);
        if(index == -1)
            tableView.getSelectionModel().select(rows-1);
        else {
            if(index == 0) index=rows;
            tableView.getSelectionModel().select(index - 1);
        }
    }

    public void downloadDrafts(List<Draft> selectedDrafts){
        List<Draft> draftsToBeDownloaded = new ArrayList<>();
        for(Draft d : selectedDrafts){
           String ext = d.getExtension().toLowerCase();
           if(ext.equals("dxf"))
               draftsToBeDownloaded.add(d);
        }
        new TaskDownloadDXFDocks(draftsToBeDownloaded);
    }
}
