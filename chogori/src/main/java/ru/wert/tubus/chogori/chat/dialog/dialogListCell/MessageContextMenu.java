package ru.wert.tubus.chogori.chat.dialog.dialogListCell;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.chat.dialog.dialogController.DialogController;
import ru.wert.tubus.chogori.chat.dialog.dialogListView.DialogListView;
import ru.wert.tubus.chogori.chat.socketwork.socketservice.SocketService;
import ru.wert.tubus.chogori.chat.util.ChatMaster;
import ru.wert.tubus.client.entity.models.*;
import ru.wert.tubus.client.entity.serviceREST.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.wert.tubus.chogori.application.services.ChogoriServices.*;
import static ru.wert.tubus.chogori.setteings.ChogoriSettings.CH_CURRENT_USER;

/**
 * Класс для управления контекстным меню сообщений чата
 */
@Slf4j
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

    public void forwardMessage(Message currentMessage, ListView<Message> listView){
        List<User> allUsers = UserService.getInstance().findAll();

        // Создаем список сообщений для пересылки (в данном случае одно сообщение)
        List<Message> messagesToForward = new ArrayList<>();
        messagesToForward.add(currentMessage);

        ForwardMessageDialog.show(
                listView.getScene().getWindow(),
                messagesToForward,
                allUsers,
                () -> {
                    // Логика пересылки сообщений выбранным пользователям
                    List<User> recipients = ForwardMessageDialog.selectedUsers;
                    if (recipients != null && !recipients.isEmpty()) {
                        for (User recipient : recipients) {
                            try {
                                // Здесь должна быть логика отправки сообщения каждому получателю
                                log.debug("Пересылка сообщения {} пользователю {}",
                                        currentMessage.getId(), recipient.getName());
                                Message forwardMessage = new Message();
                                Room room = ChatMaster.fetchOneToOneRoom(recipient);
                                forwardMessage.setRoomId(room.getId());
                                forwardMessage.setStatus(Message.MessageStatus.RECEIVED);
                                forwardMessage.setType(currentMessage.getType());
                                forwardMessage.setSenderId(CH_CURRENT_USER.getId());
                                forwardMessage.setText(currentMessage.getText());
                                forwardMessage.setCreationTime(LocalDateTime.now());

                                SocketService.sendMessage(forwardMessage);
                                sendMessageToOpenRoom(room, currentMessage);

                            } catch (IllegalArgumentException e) {
                                log.error("Ошибка создания чата: {}", e.getMessage());
                            } catch (RuntimeException e) {
                                log.error("Ошибка сохранения комнаты: {}", e.getMessage());
                            }
                        }
                    }
                }
        );
    }


    private void sendMessageToOpenRoom(Room room, Message message){
        List<DialogListView> openRooms = DialogController.openRooms;
        for(DialogListView dlv : DialogController.openRooms){
            if(dlv.getRoom().equals(room)) {
                dlv.getRoomMessages().add(message);
                return;
            }
        }

    }

    /**
     * Проверка, является ли сообщение исходящим
     */
    private boolean isOutgoingMessage(Message msg) {
        return msg.getSenderId() != null &&
                msg.getSenderId().equals(CH_CURRENT_USER.getId());
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