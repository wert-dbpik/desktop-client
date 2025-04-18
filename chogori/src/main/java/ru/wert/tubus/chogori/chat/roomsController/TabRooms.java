package ru.wert.tubus.chogori.chat.roomsController;

import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.chat.util.ChatStaticMaster;
import ru.wert.tubus.chogori.components.BlinkingImageView;
import ru.wert.tubus.chogori.images.BtnImages;
import ru.wert.tubus.client.entity.models.Message;
import ru.wert.tubus.client.entity.models.Room;
import ru.wert.tubus.client.entity.models.Roommate;
import ru.wert.tubus.client.entity.models.User;

import java.util.ArrayList;
import java.util.List;

import static ru.wert.tubus.chogori.application.services.ChogoriServices.CH_MESSAGES;
import static ru.wert.tubus.chogori.application.services.ChogoriServices.CH_ROOMS;
import static ru.wert.tubus.chogori.chat.roomsController.RoomsController.WRITE_YOURSELF;
import static ru.wert.tubus.chogori.chat.util.ChatStaticMaster.getSecondUserInOneToOneChat;
import static ru.wert.tubus.chogori.images.BtnImages.*;
import static ru.wert.tubus.chogori.setteings.ChogoriSettings.CH_CURRENT_USER;

@Slf4j
public class TabRooms {
    private final Tab tabPaneRooms;
    @Getter
    private final ListView<Room> listOfRooms;
    private final RoomsController controller;

    public TabRooms(RoomsController controller) {
        this.controller = controller;
        this.tabPaneRooms = controller.getTabPaneRooms();
        this.listOfRooms = controller.getListOfRooms();
    }

    public void createListOfRooms() {
        log.debug("Создание списка комнат");
        tabPaneRooms.setGraphic(new ImageView(BtnImages.CHATS_IMG));
        Label placeholder = new Label("Ни одного чата\nеще не создано");
        placeholder.setStyle("-fx-text-fill: saddlebrown; -fx-font-size: 14; -fx-font-style: italic; -fx-font-weight: bold");
        listOfRooms.setPlaceholder(placeholder);

        listOfRooms.setCellFactory(new Callback<ListView<Room>, ListCell<Room>>() {
            public ListCell<Room> call(ListView<Room> param) {
                return new ListCell<Room>() {
                    private final ImageView dotImageView = new ImageView();
                    private final BlinkingImageView unreadMessagesIcon = new BlinkingImageView(CHAT_LETTER_IMG);

                    private final Label nameLabel = new Label();
                    private final HBox hbox = new HBox();

                    {
                        // Устанавливаем размеры для изображений
                        dotImageView.setFitWidth(10);
                        dotImageView.setFitHeight(10);
                        unreadMessagesIcon.setFitWidth(16);
                        unreadMessagesIcon.setFitHeight(16);
                        unreadMessagesIcon.setVisible(false);

                        // Настройка контейнера
                        hbox.setStyle("-fx-alignment: center-left");
                        hbox.setSpacing(5);
                        hbox.getChildren().addAll(dotImageView, nameLabel, unreadMessagesIcon);
                    }

                    @Override
                    public void updateItem(Room room, boolean empty) {
                        super.updateItem(room, empty);

                        if (empty || room == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setText(null);

                            // Устанавливаем имя комнаты
                            String roomName = ChatStaticMaster.getRoomName(room.getName());
                            if (room.getName().startsWith("one-to-one")) {
                                User secondUser = getSecondUserInOneToOneChat(room);
                                if (secondUser != null && secondUser.getId().equals(CH_CURRENT_USER.getId())) {
                                    roomName = WRITE_YOURSELF; // "Написать себе"
                                }
                            }
                            nameLabel.setText(roomName);
                            nameLabel.setStyle("-fx-font-style: italic; -fx-text-fill: black");

                            // Устанавливаем изображение статуса для one-to-one чатов
                            if (room.getName().startsWith("one-to-one")) {
                                User secondUser = getSecondUserInOneToOneChat(room);
                                if (secondUser != null && secondUser.isOnline()) {
                                    dotImageView.setImage(DOT_BLUE_IMG);
                                } else {
                                    dotImageView.setImage(SPACE_IMG);
                                }
                            } else {
                                dotImageView.setImage(SPACE_IMG);
                            }

                            // Проверяем наличие непрочитанных сообщений для этой комнаты
                            boolean hasUnreadMessages = !CH_MESSAGES.findUndeliveredMessagesByRoomAndSecondUser(
                                    room.getId(),
                                    getSecondUserInOneToOneChat(room).getId())
                                    .isEmpty();

                            unreadMessagesIcon.startBlinking();
                            unreadMessagesIcon.setVisible(hasUnreadMessages);

                            // Устанавливаем контейнер в ячейку
                            setGraphic(hbox);

                            // Обработка клика по строке
                            setOnMouseClicked(e -> {
                                if (e.getButton().equals(MouseButton.PRIMARY)) {
                                    // Двойной клик - открываем чат
                                    if (e.getClickCount() == 2) {
                                        log.debug("Открытие чата в комнате: {}", room.getName());
                                        unreadMessagesIcon.setVisible(false);
                                        controller.openChat(room);
                                        e.consume();
                                    }
                                }
                            });

                            // Контекстное меню (остается без изменений)
                            ContextMenu contextMenu = new ContextMenu();
                            MenuItem deleteItem = new MenuItem("Удалить из списка");
                            deleteItem.setOnAction(event -> controller.deleteRoomFromList(room));
                            MenuItem exitItem = new MenuItem("Выйти из чата");
                            exitItem.setOnAction(event -> controller.exitFromChat(room));

                            contextMenu.getItems().clear();
                            contextMenu.getItems().add(deleteItem);
                            if (room.getName().startsWith("group")) {
                                contextMenu.getItems().add(exitItem);
                            }
                            setContextMenu(contextMenu);
                        }
                    }
                };
            }
        });
    }

    /**
     * В методе создается список комнат, в которых участвует текущий пользователь.
     * Список выводится в ListView.
     */
    public void updateListOfRooms() {
        log.debug("Обновление списка комнат");

        // Создаем три списка: для чата "Написать себе", для one-to-one чатов и для групповых чатов
        List<Room> writeYourselfRoom = new ArrayList<>();
        List<Room> oneToOneRooms = new ArrayList<>();
        List<Room> groupRooms = new ArrayList<>();

        // Получаем все комнаты из базы данных
        List<Room> allRooms = CH_ROOMS.findAll();

        // Разделяем комнаты на соответствующие списки
        for (Room room : allRooms) {
            // Проверяем, является ли текущий пользователь участником комнаты
            boolean isCurrentUserInRoom = room.getRoommates().stream()
                    .anyMatch(roommate -> roommate.getUserId().equals(CH_CURRENT_USER.getId()));

            // Проверяем, видна ли комната для пользователя
            boolean isRoomVisible = room.getRoommates().stream()
                    .filter(roommate -> roommate.getUserId().equals(CH_CURRENT_USER.getId()))
                    .findFirst()
                    .map(Roommate::isVisibleForUser)
                    .orElse(false);

            // Добавляем комнату в соответствующий список, если пользователь является участником и комната видима
            if (isCurrentUserInRoom && isRoomVisible) {
                if (room.getName().startsWith("one-to-one")) {
                    // Получаем второго пользователя в one-to-one чате
                    User secondUser = getSecondUserInOneToOneChat(room);

                    // Если второй пользователь - это текущий пользователь, это чат "Написать себе"
                    if (secondUser != null && secondUser.getId().equals(CH_CURRENT_USER.getId())) {
                        writeYourselfRoom.add(room); // Чат "Написать себе"
                    } else {
                        oneToOneRooms.add(room); // One-to-one чаты
                    }
                } else if (room.getName().startsWith("group")) {
                    groupRooms.add(room); // Групповые чаты
                }
            }
        }

        // Сортируем one-to-one чаты по имени
        oneToOneRooms.sort((room1, room2) -> {
            String name1 = ChatStaticMaster.getRoomName(room1.getName());
            String name2 = ChatStaticMaster.getRoomName(room2.getName());
            return name1.compareToIgnoreCase(name2);
        });

        // Сортируем групповые чаты по имени
        groupRooms.sort((room1, room2) -> {
            String name1 = ChatStaticMaster.getRoomName(room1.getName());
            String name2 = ChatStaticMaster.getRoomName(room2.getName());
            return name1.compareToIgnoreCase(name2);
        });

        // Объединяем списки: сначала чат "Написать себе", затем one-to-one чаты, затем групповые
        List<Room> sortedRooms = new ArrayList<>();
        sortedRooms.addAll(writeYourselfRoom);
        sortedRooms.addAll(oneToOneRooms);
        sortedRooms.addAll(groupRooms);

        // Устанавливаем обновленный и отсортированный список комнат в ListView
        listOfRooms.setItems(FXCollections.observableArrayList(sortedRooms));
    }
}
