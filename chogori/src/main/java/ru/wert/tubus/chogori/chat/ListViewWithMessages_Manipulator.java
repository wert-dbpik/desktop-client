package ru.wert.tubus.chogori.chat;

import javafx.scene.control.ListView;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import ru.wert.tubus.client.entity.models.Message;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.wert.tubus.chogori.statics.AppStatic.*;

@Slf4j
public class ListViewWithMessages_Manipulator {

    private final ListView<Message> listView;
    private SideRoomDialogController chatController;

    public ListViewWithMessages_Manipulator(ListViewDialog listView, SideRoomDialogController chatController) {
        this.listView = listView;
        this.chatController = chatController;

        // Обработка события перетаскивания файлов над ListView
        listView.setOnDragOver(event -> {
            Dragboard db = event.getDragboard();
            log.debug("Начало обработки события перетаскивания над ListView");

            // Если перетаскиваемые данные содержат файлы
            if (db.hasFiles()) {
                List<File> allFiles = new ArrayList<>();
                List<File> content = (List<File>) db.getContent(DataFormat.FILES);
                log.debug("Обнаружены файлы для перетаскивания: {}", content.size());

                // Обработка каждого файла или папки
                for (File f : content) {
                    try {
                        if (f.isDirectory()) {
                            // Если это папка, собираем все файлы внутри
                            List<Path> filesInFolder = Files.walk(f.toPath()).collect(Collectors.toList());
                            for (Path p : filesInFolder) allFiles.add(p.toFile());
                            log.debug("Добавлены файлы из папки: {}", f.getName());
                        } else if (f.isFile()) {
                            allFiles.add(f);
                            log.debug("Добавлен файл: {}", f.getName());
                        }
                    } catch (IOException e) {
                        log.error("Ошибка при обработке файла или папки: {}", e.getMessage(), e);
                    }
                }

                // Проверка расширения файлов и разрешение перетаскивания для изображений
                for (File file : allFiles) {
                    String ext = FilenameUtils.getExtension(file.getName().toLowerCase());
                    if (IMAGE_EXTENSIONS.contains(ext)) {
                        event.acceptTransferModes(TransferMode.MOVE);
                        event.consume();
                        log.debug("Разрешено перетаскивание для файла с расширением изображения: {}", file.getName());
                        return;
                    }
                }
            } else if (db.hasString()) {
                // Если перетаскиваемые данные содержат строку
                String str = db.getString();
                log.debug("Обнаружена строка для перетаскивания: {}", str);

                // Проверка строки на соответствие определенным шаблонам
                if (str.startsWith("pik! DR#") || str.startsWith("pik! F#") || str.startsWith("pik! PP#")) {
                    event.acceptTransferModes(TransferMode.MOVE);
                    event.consume();
                    log.debug("Разрешено перетаскивание для строки: {}", str);
                    return;
                }
            }

            // Если ни одно из условий не выполнено, перетаскивание запрещено
            event.acceptTransferModes(TransferMode.NONE);
            log.debug("Перетаскивание запрещено, так как данные не соответствуют допустимым форматам");
        });

        // Обработка события завершения перетаскивания
        listView.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            log.debug("Начало обработки события завершения перетаскивания");

            if (db.hasFiles()) {
                // Если перетаскиваемые данные содержат файлы
                List<File> acceptedFiles = new ArrayList<>();
                List<File> content = (List<File>) db.getContent(DataFormat.FILES);
                log.debug("Файлы успешно перетащены: {}", content.size());

                // Создание сообщения с изображениями
                listView.createPicsChatMessage(content);
                log.debug("Создано сообщение с изображениями");
            } else if (db.hasString()) {
                // Если перетаскиваемые данные содержат строку
                String str = db.getString();
                log.debug("Строка успешно перетащена: {}", str);

                // Создание сообщения в зависимости от типа строки
                if (str.startsWith("pik! DR#")) {
                    listView.createDraftsChatMessage(str);
                    log.debug("Создано сообщение с чертежом");
                } else if (str.startsWith("pik! F#")) {
                    listView.createFoldersChatMessage(str);
                    log.debug("Создано сообщение с папкой");
                } else if (str.startsWith("pik! PP#")) {
                    listView.createPassportsChatMessage(str);
                    log.debug("Создано сообщение с паспортом");
                }
            }
        });
    }
}
