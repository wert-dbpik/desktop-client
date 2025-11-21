package ru.wert.tubus.chogori.help;

import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.text.*;
import javafx.util.Callback;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.client.entity.models.VersionDesktop;
import ru.wert.tubus.client.entity.serviceREST.VersionDesktopService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

@Slf4j
public class VersionInfoController {

    @FXML
    private ListView<VersionDesktop> lvVersionInfo;

    @FXML
    void initialize(){

        lvVersionInfo.setCellFactory(new Callback<ListView<VersionDesktop>, ListCell<VersionDesktop>>() {
            @Override
            public ListCell<VersionDesktop> call(ListView<VersionDesktop> param) {
                return new ListCell<VersionDesktop>() {
                    @Override
                    protected void updateItem(VersionDesktop item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            // Создаем TextFlow для разного форматирования строк
                            TextFlow textFlow = new TextFlow();

                            // Первая строка - номер версии (bold, 14)
                            Text versionText = new Text(item.getName() + "\n");
                            versionText.setFont(Font.font("System", FontWeight.BOLD, 14));

                            // Вторая строка - дата (курсив, 10)
                            String formattedDate = formatDate(item.getData());
                            Text dateText = new Text(formattedDate + "\n");
                            dateText.setFont(Font.font("System", FontPosture.ITALIC, 10));

                            // Третья строка - примечание (12)
                            Text noteText = new Text("");
                            if (item.getNote() != null && !item.getNote().isEmpty()) {
                                noteText.setText(item.getNote());
                            }
                            noteText.setFont(Font.font("System", 12));

                            textFlow.getChildren().addAll(versionText, dateText, noteText);
                            setGraphic(textFlow);
                            setText(null);
                        }
                    }
                };
            }
        });

        lvVersionInfo.getItems().addAll(VersionDesktopService.getInstance().findAll());
        Collections.reverse(lvVersionInfo.getItems());

    }

    private String formatDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return "";
        }

        try {
            // Пытаемся распарсить дату-время из формата ISO
            if (dateString.contains("T")) {
                LocalDateTime dateTime = LocalDateTime.parse(dateString);
                DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                return dateTime.format(outputFormatter);
            }
            // Для дат в формате с точками
            else if (dateString.contains(".")) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                LocalDate date = LocalDate.parse(dateString, formatter);
                DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                return date.format(outputFormatter);
            } else {
                // Если формат неизвестен, возвращаем исходную строку
                return dateString;
            }
        } catch (Exception e) {
            // В случае ошибки парсинга возвращаем исходную строку
            log.warn("Не удалось отформатировать дату: {}", dateString);
            return dateString;
        }
    }

}
