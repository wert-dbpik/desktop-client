package ru.wert.tubus.chogori.crashReport;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import org.json.JSONObject;
import org.json.JSONArray;
import ru.wert.tubus.client.interfaces.UpdatableTabController;

import java.util.Iterator;

public class ReportController implements UpdatableTabController {

    @FXML
    private AnchorPane apMain;

    public void init(String json) {
        // Создаем WebView для отображения форматированного JSON
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();

        // Форматируем JSON с учетом специальных полей
        String formattedHtml = formatJsonToHtml(json);

        // Загружаем HTML в WebView
        webEngine.loadContent(formattedHtml);

        // Растягиваем WebView на всю панель
        apMain.getChildren().add(webView);
        AnchorPane.setTopAnchor(webView, 0.0);
        AnchorPane.setBottomAnchor(webView, 0.0);
        AnchorPane.setLeftAnchor(webView, 0.0);
        AnchorPane.setRightAnchor(webView, 0.0);
    }

    private String formatJsonToHtml(String jsonString) {
        StringBuilder htmlBuilder = new StringBuilder();

        // HTML-шапка с CSS стилями
        htmlBuilder.append("<html><head><style>")
                .append("body { font-family: Arial, sans-serif; font-size: 14px; line-height: 1.5; }")
                .append(".level1 { font-weight: bold; color: #0000FF; }")
                .append(".level2 { color: #0000FF; }")
                .append(".value { color: #8B4513; }")
                .append(".multiline { white-space: pre-wrap; color: #8B4513; font-family: monospace; }")
                .append("</style></head><body>");

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            htmlBuilder.append("<div class='json-container'>");
            processJsonObject(jsonObject, htmlBuilder, 0);
            htmlBuilder.append("</div>");
        } catch (Exception e) {
            htmlBuilder.append("<div style='color:red'>Ошибка при парсинге JSON: ")
                    .append(e.getMessage())
                    .append("</div>");
        }

        htmlBuilder.append("</body></html>");
        return htmlBuilder.toString();
    }

    private void processJsonObject(JSONObject jsonObject, StringBuilder htmlBuilder, int level) {
        Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            Object value = jsonObject.get(key);

            // Отступ в зависимости от уровня вложенности
            htmlBuilder.append("<div style='margin-left:").append(level * 20).append("px'>");

            // Определяем стиль для ключа
            String keyClass = (level == 0) ? "level1" : "level2";
            htmlBuilder.append("<span class='").append(keyClass).append("'>")
                    .append(key)
                    .append(": </span>");

            // Обработка значения
            if (value instanceof JSONObject) {
                htmlBuilder.append("<div>");
                processJsonObject((JSONObject) value, htmlBuilder, level + 1);
                htmlBuilder.append("</div>");
            } else if (value instanceof JSONArray) {
                htmlBuilder.append("<div>");
                processJsonArray((JSONArray) value, htmlBuilder, level + 1);
                htmlBuilder.append("</div>");
            } else {
                String stringValue = value.toString();

                // Специальная обработка для многострочных полей
                if (key.equals("LOGCAT") || key.equals("STACK_TRACE")) {
                    // Экранируем HTML-теги и заменяем \n на <br>
                    stringValue = escapeHtml(stringValue).replace("\n", "<br>");
                    htmlBuilder.append("<div class='multiline'>").append(stringValue).append("</div>");
                } else {
                    htmlBuilder.append("<span class='value'>").append(stringValue).append("</span>");
                }
            }

            htmlBuilder.append("</div>");
        }
    }

    private void processJsonArray(JSONArray jsonArray, StringBuilder htmlBuilder, int level) {
        for (int i = 0; i < jsonArray.length(); i++) {
            Object value = jsonArray.get(i);
            htmlBuilder.append("<div style='margin-left:").append(level * 20).append("px'>");

            if (value instanceof JSONObject) {
                processJsonObject((JSONObject) value, htmlBuilder, level + 1);
            } else if (value instanceof JSONArray) {
                processJsonArray((JSONArray) value, htmlBuilder, level + 1);
            } else {
                htmlBuilder.append("<span class='value'>").append(value.toString()).append("</span>");
            }

            htmlBuilder.append("</div>");
        }
    }

    /**
     * Экранирует HTML-символы в строке
     */
    private String escapeHtml(String input) {
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    @Override
    public void updateTab() {

    }
}
