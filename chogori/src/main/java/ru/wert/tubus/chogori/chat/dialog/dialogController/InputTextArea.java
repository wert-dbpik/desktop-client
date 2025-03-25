package ru.wert.tubus.chogori.chat.dialog.dialogController;

import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;

public class InputTextArea {

    // Переменные для управления высотой текстового поля

    private final Text textHolder = new Text();
    private double oldHeight = 0;

    private final TextArea taMessageText;
    private final SplitPane splitPane;
    private final Button btnSend;

    public InputTextArea(DialogController controller) {
        this.taMessageText = controller.getTaMessageText();
        this.splitPane = controller.getSplitPane();
        this.btnSend = controller.getBtnSend();
    }

    /**
     * Настройка текстового поля ввода сообщений.
     */
    public void createTextArea() {
        SplitPane.Divider divider = splitPane.getDividers().get(0);

        // Применение CSS стилей к текстовому полю
        taMessageText.setId("blobTextArea");

        // Настройка автоматического изменения высоты текстового поля
        textHolder.textProperty().bind(taMessageText.textProperty());
        textHolder.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            textHolder.setWrappingWidth(taMessageText.getWidth() - 10);
            if (oldHeight != newValue.getHeight()) {
                oldHeight = newValue.getHeight();

                double newHeight = textHolder.getLayoutBounds().getHeight() + 30;
                divider.setPosition(1.0 - newHeight / splitPane.getHeight());
                taMessageText.resize(taMessageText.getWidth(), newHeight);
            }
        });

        taMessageText.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            textHolder.setWrappingWidth(taMessageText.getWidth() - 10);
            double newHeight = textHolder.getLayoutBounds().getHeight() + 30;
            divider.setPosition(1.0 - newHeight / splitPane.getHeight());
            taMessageText.resize(taMessageText.getWidth(), newHeight);
        });

        // Обработка нажатия Ctrl+Enter для отправки сообщения
        taMessageText.setOnKeyPressed(e -> {
            if (taMessageText.isFocused() &&
                    e.isControlDown() &&
                    e.getCode().equals(KeyCode.ENTER))
                btnSend.fire();
        });
    }

    /**
     * Устанавливает текст в поле ввода и устанавливает фокус
     * @param text Текст для установки
     */
    public void setTextWithFocus(String text) {
        taMessageText.setText(text);
        taMessageText.requestFocus();
        taMessageText.positionCaret(text.length());
    }

}
