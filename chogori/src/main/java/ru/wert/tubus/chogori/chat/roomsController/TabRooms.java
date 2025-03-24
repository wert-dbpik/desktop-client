package ru.wert.tubus.chogori.chat.roomsController;

import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.chat.util.ChatMaster;
import ru.wert.tubus.chogori.chat.util.UserOnline;
import ru.wert.tubus.chogori.images.BtnImages;
import ru.wert.tubus.client.entity.models.Room;
import ru.wert.tubus.client.entity.models.Roommate;
import ru.wert.tubus.client.entity.models.User;

import java.util.ArrayList;
import java.util.List;

import static ru.wert.tubus.chogori.application.services.ChogoriServices.CH_ROOMS;
import static ru.wert.tubus.chogori.chat.roomsController.RoomsController.WRIGHT_YOURSELF;
import static ru.wert.tubus.chogori.images.BtnImages.DOT_BLUE_IMG;
import static ru.wert.tubus.chogori.images.BtnImages.SPACE_IMG;
import static ru.wert.tubus.chogori.setteings.ChogoriSettings.CH_CURRENT_USER;

@Slf4j
public class TabRooms {
    private final Tab tabPaneRooms;
    private final ListView<Room> listOfRooms;
    private RoomsController controller;

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
                    private final Label nameLabel = new Label();

                    {
                        // Устанавливаем размеры для ImageView
                        dotImageView.setFitWidth(10);
                        dotImageView.setFitHeight(10);
                    }

                    @Override
                    public void updateItem(Room room, boolean empty) {
                        super.updateItem(room, empty);

                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setText(null);

                            // Устанавливаем имя комнаты
                            String roomName = ChatMaster.getRoomName(room.getName());
                            if (room.getName().startsWith("one-to-one")) {
                                User secondUser = ChatMaster.getSecondUserInOneToOneChat(room);
                                if (secondUser != null && secondUser.getId().equals(CH_CURRENT_USER.getId())) {
                                    roomName = WRIGHT_YOURSELF; // "Написать себе"
                                }
                            }
                            nameLabel.setText(roomName);
                            nameLabel.setStyle("-fx-font-style: italic; -fx-text-fill: black");

                            // Устанавливаем изображение в зависимости от статуса пользователя в one-to-one чате
                            if (room.getName().startsWith("one-to-one")) {
                                User secondUser = ChatMaster.getSecondUserInOneToOneChat(room);
                                if (secondUser != null && secondUser.isOnline()) {
                                    dotImageView.setImage(DOT_BLUE_IMG);
                                } else {
                                    dotImageView.setImage(SPACE_IMG);
                                }
                            } else {
                                dotImageView.setImage(SPACE_IMG); // Для групповых чатов или других типов
                            }

                            // Создаем контейнер для изображения и текста
                            HBox hbox = new HBox(dotImageView, nameLabel);
                            hbox.setStyle("-fx-alignment: center-left");
                            hbox.setSpacing(5); // Расстояние между изображением и текстом

                            // Устанавливаем контейнер в ячейку
                            setGraphic(hbox);

                            // Обработка двойного клика для открытия чата
                            nameLabel.setOnMouseClicked(e -> {
                                if (e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2) {
                                    log.debug("Открытие чата в комнате: {}", room.getName());
                                    controller.openChat(room);
                                    e.consume();
                                }
                            });

                            // Создаем контекстное меню
                            ContextMenu contextMenu = new ContextMenu();

                            // Пункт меню для удаления комнаты из списка
                            MenuItem deleteItem = new MenuItem("Удалить из списка");
                            deleteItem.setOnAction(event -> controller.deleteRoomFromList(room));

                            // Пункт меню для выхода из чата (только для групповых чатов)
                            MenuItem exitItem = new MenuItem("Выйти из чата");
                            exitItem.setOnAction(event -> controller.exitFromChat(room));

                            // Очищаем контекстное меню и добавляем пункты
                            contextMenu.getItems().clear();
                            contextMenu.getItems().add(deleteItem);

                            // Добавляем пункт "Выйти из чата" только если имя комнаты начинается с "group"
                            if (room.getName().startsWith("group")) {
                                contextMenu.getItems().add(exitItem);
                            }

                            // Устанавливаем контекстное меню для метки
                            nameLabel.setContextMenu(contextMenu);
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
                    User secondUser = ChatMaster.getSecondUserInOneToOneChat(room);

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
            String name1 = ChatMaster.getRoomName(room1.getName());
            String name2 = ChatMaster.getRoomName(room2.getName());
            return name1.compareToIgnoreCase(name2);
        });

        // Сортируем групповые чаты по имени
        groupRooms.sort((room1, room2) -> {
            String name1 = ChatMaster.getRoomName(room1.getName());
            String name2 = ChatMaster.getRoomName(room2.getName());
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
