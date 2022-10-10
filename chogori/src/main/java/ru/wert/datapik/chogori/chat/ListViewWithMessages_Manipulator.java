package ru.wert.datapik.chogori.chat;

import com.twelvemonkeys.io.FileUtil;
import javafx.scene.control.ListView;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import ru.wert.datapik.client.entity.models.ChatMessage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.wert.datapik.chogori.statics.AppStatic.*;

public class ListViewWithMessages_Manipulator {

    private final ListView<ChatMessage> listView;
    private SideChatTalkController chatController;

    public ListViewWithMessages_Manipulator(ListView<ChatMessage> listView, SideChatTalkController chatController) {
        this.listView = listView;
        this.chatController = chatController;

        //Допускается добавление изображений, чертежей
        listView.setOnDragOver(event -> {
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
                    String ext = FileUtil.getExtension(file.getName().toLowerCase());
                    if (IMAGE_EXTENSIONS.contains(ext)) {
                        event.acceptTransferModes(TransferMode.MOVE);
                        event.consume();
                        return;
                    }
//                    event.acceptTransferModes(TransferMode.NONE);
                }
            } else if (db.hasString()){
                String str = db.getString();
                if(str.startsWith("pik! DR#") || str.startsWith("pik! F#") || str.startsWith("pik! PP#")){
                    event.acceptTransferModes(TransferMode.MOVE);
                    event.consume();
                    return;
                }

            }
                event.acceptTransferModes(TransferMode.NONE);
        });

        listView.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                List<File> acceptedFiles = new ArrayList<>();
                List<File> content = (List<File>) db.getContent(DataFormat.FILES);
                for (File f : content)
                    chatController.createPicsChatMessage(content);
            } else if(db.hasString()) {
                String str = db.getString();
                if (str.startsWith("pik! DR#")) {
                    chatController.createDraftsChatMessage(str);
                } else if(str.startsWith("pik! F#")){
                    chatController.createFoldersChatMessage(str);
                } else if(str.startsWith("pik! PP#")) {
                    chatController.createPassportsChatMessage(str);
                }

            }
        });
    }




}
