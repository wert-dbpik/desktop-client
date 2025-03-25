package ru.wert.tubus.chogori.chat.dialog.dialogListCell;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import ru.wert.tubus.client.entity.models.Draft;
import ru.wert.tubus.client.entity.models.Folder;
import ru.wert.tubus.client.entity.models.Message;
import ru.wert.tubus.chogori.setteings.ChogoriSettings;
import ru.wert.tubus.client.entity.models.Passport;

import static ru.wert.tubus.chogori.application.services.ChogoriServices.*;

/**
 * Класс для управления контекстным меню сообщений чата
 */
public class MessageContextMenu {
    private final ContextMenu contextMenu = new ContextMenu();
    private final MenuItem copyItem = new MenuItem("Копировать");
    private final MenuItem deleteItem = new MenuItem("Удалить");
    private final MenuItem forwardItem = new MenuItem("Переслать");
    private final MenuItem editItem = new MenuItem("Изменить");

    private Message currentMessage;
    private final Runnable onDelete;
    private final Runnable onForward;
    private final Runnable onEdit;

    /**
     * Конструктор контекстного меню
     * @param onDelete обработчик удаления сообщения
     * @param onForward обработчик пересылки сообщения
     * @param onEdit обработчик редактирования сообщения
     */
    public MessageContextMenu(Runnable onDelete, Runnable onForward, Runnable onEdit) {
        this.onDelete = onDelete;
        this.onForward = onForward;
        this.onEdit = onEdit;

        initMenuItems();
        setupContextMenu();
    }

    /**
     * Инициализация пунктов меню
     */
    private void initMenuItems() {
        copyItem.setOnAction(e -> handleCopyAction());
        deleteItem.setOnAction(e -> {
            if (onDelete != null) onDelete.run();
        });
        forwardItem.setOnAction(e -> {
            if (onForward != null) onForward.run();
        });
        editItem.setOnAction(e -> {
            if (onEdit != null) onEdit.run();
        });
    }

    /**
     * Настройка контекстного меню
     */
    private void setupContextMenu() {
        contextMenu.getItems().addAll(copyItem, deleteItem, forwardItem, editItem);
    }

    /**
     * Обновление видимости пунктов меню в зависимости от типа сообщения
     */
    public void updateMenuItemsVisibility() {
        if (currentMessage == null) return;

        boolean isTextMessage = currentMessage.getType() == Message.MessageType.CHAT_TEXT;
        boolean isOwnMessage = isOutgoingMessage(currentMessage);

        editItem.setVisible(isTextMessage && isOwnMessage);
    }

    /**
     * Обработчик копирования текста сообщения
     */
    private void handleCopyAction() {
        if (currentMessage == null || currentMessage.getText() == null) return;

        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        String clipboardText;
        switch(currentMessage.getType()){
            case CHAT_DRAFTS:
                Long draftId = Long.valueOf(currentMessage.getText());
                Draft draft = CH_QUICK_DRAFTS.findById(draftId);
                clipboardText = draft != null ? draft.toUsefulString() : "Чертеж не найден";
                break;
            case CHAT_FOLDERS:
                Long folderId = Long.valueOf(currentMessage.getText());
                Folder folder = CH_QUICK_FOLDERS.findById(folderId);
                clipboardText = folder != null ? folder.toUsefulString() : "Комплект чертежей не найден";
                break;
            case CHAT_PASSPORTS:
                Long passportId = Long.valueOf(currentMessage.getText());
                Passport passport = CH_QUICK_PASSPORTS.findById(passportId);
                clipboardText = passport != null ? passport.toUsefulString() : "Пасспорт не найден";
                break;
            default:
                clipboardText = currentMessage.getText();
                break;
        }
        content.putString(clipboardText);
        clipboard.setContent(content);
    }

    /**
     * Проверка, является ли сообщение исходящим
     */
    private boolean isOutgoingMessage(Message msg) {
        return msg.getSenderId() != null &&
                msg.getSenderId().equals(ChogoriSettings.CH_CURRENT_USER.getId());
    }

    /**
     * Установка текущего сообщения
     */
    public void setCurrentMessage(Message message) {
        this.currentMessage = message;
        updateMenuItemsVisibility();
    }

    /**
     * Получение контекстного меню
     */
    public ContextMenu getContextMenu() {
        return contextMenu;
    }
}