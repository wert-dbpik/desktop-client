package ru.wert.tubus.chogori.chat;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.chat.util.ChatMaster;
import ru.wert.tubus.chogori.chat.util.UserOnline;
import ru.wert.tubus.chogori.images.BtnImages;
import ru.wert.tubus.client.entity.models.Room;
import ru.wert.tubus.client.entity.models.Roommate;
import ru.wert.tubus.client.entity.models.User;
import ru.wert.tubus.client.entity.serviceQUICK.UserQuickService;

import java.util.ArrayList;
import java.util.List;

import static ru.wert.tubus.chogori.application.services.ChogoriServices.CH_ROOMS;
import static ru.wert.tubus.chogori.chat.socketwork.ServerMessageHandler.roomsController;
import static ru.wert.tubus.chogori.images.BtnImages.DOT_BLUE_IMG;
import static ru.wert.tubus.chogori.images.BtnImages.SPACE_IMG;
import static ru.wert.tubus.chogori.setteings.ChogoriSettings.CH_CURRENT_USER;

@Slf4j
public class RoomsController {

    public static final String WRIGHT_YOURSELF = "Написать себе";

    @FXML
    private Button btnAddNewChatGroup;

    @FXML
    private ListView<UserOnline> listOfUsers;

    @FXML
    @Getter
    private ListView<Room> listOfRooms;

    @FXML
    private Tab tabPaneRooms;

    @FXML
    private Tab tabPaneUsers;

    private SideChat chat;

    @Getter
    public ObservableList<UserOnline> usersOnline = FXCollections.observableArrayList();

    @FXML
    void initialize() {
        log.info("Инициализация SideRoomsController");

        createListOfRooms();
        updateListOfRooms();
        tabPaneRooms.setOnSelectionChanged(e -> {
            if (tabPaneRooms.isSelected()) {
                log.debug("Вкладка 'Комнаты' выбрана, обновление списка комнат");
                updateListOfRooms();
            }
        });

        createListOfUsers();
        updateListOfUsers();

    }

    /**
     * Добавляет комнату в список listOfRooms, если она отсутствует.
     *
     * @param room Комната, которую нужно добавить.
     */
    public void addRoomIfAbsent(Room room) {
        if (room != null && !listOfRooms.getItems().contains(room)) {
            log.debug("Добавление комнаты в список: {}", room.getName());
            listOfRooms.getItems().add(room);
        }
    }

    public void refreshListOfUsers() {
        if (listOfUsers != null) {
            log.debug("Обновление списка пользователей");
            listOfUsers.refresh();
        }
    }

    public void init(SideChat chat) {
        log.info("Инициализация SideRoomsController с SideChat");
        this.chat = chat;
        roomsController = this;
    }

    /**
     * В методе создается пустой список, в который первым добавляется сам пользователь (написать себе)
     * Затем добавляется отсортированный список остальных пользователей за минусом Гостя
     */
    private void updateListOfUsers() {
        log.debug("Обновление списка пользователей");
        usersOnline.clear();
        for (User user : UserQuickService.users) {
            usersOnline.add(new UserOnline(user, user.isOnline()));
        }

        sortUsersOnlineByName();
        listOfUsers.setItems(usersOnline);
    }

    private void sortUsersOnlineByName() {
        log.debug("Сортировка пользователей по имени");
        String currentUserName = CH_CURRENT_USER.getName(); // Имя текущего пользователя

        usersOnline.sort((userOnline1, userOnline2) -> {
            String name1 = userOnline1.getUser().getName();
            String name2 = userOnline2.getUser().getName();

            // Если первый пользователь - это текущий пользователь, он должен быть выше
            if (name1.equals(currentUserName)) {
                return -1; // userOnline1 будет выше
            }
            // Если второй пользователь - это текущий пользователь, он должен быть выше
            if (name2.equals(currentUserName)) {
                return 1; // userOnline2 будет выше
            }
            // В остальных случаях сортируем по имени без учета регистра
            return name1.compareToIgnoreCase(name2);
        });
    }

    /**
     * В методе создается список комнат, в которых участвует текущий пользователь.
     * Список выводится в ListView.
     */
    private void updateListOfRooms() {
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

    private void createListOfUsers() {
        log.debug("Создание списка пользователей");
        tabPaneUsers.setGraphic(new ImageView(BtnImages.MAN_IMG));
        listOfUsers.setPlaceholder(new Label("Пользователей не найдено"));
        listOfUsers.setCellFactory(new Callback<ListView<UserOnline>, ListCell<UserOnline>>() {
            public ListCell<UserOnline> call(ListView<UserOnline> param) {
                return new ListCell<UserOnline>() {
                    private final ImageView dotImageView = new ImageView();
                    private final Label nameLabel = new Label();

                    {
                        // Устанавливаем размеры для ImageView
                        dotImageView.setFitWidth(10);
                        dotImageView.setFitHeight(10);
                    }

                    @Override
                    public void updateItem(UserOnline userOnline, boolean empty) {
                        super.updateItem(userOnline, empty);

                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setText(null);

                            // Устанавливаем имя пользователя
                            if (userOnline.getUser().equals(CH_CURRENT_USER))
                                nameLabel.setText(WRIGHT_YOURSELF);
                            else
                                nameLabel.setText(userOnline.getUser().getName());

                             nameLabel.setContextMenu(createContextMenu(userOnline.getUser()));
                             nameLabel.setOnMouseClicked(e -> {
                                if (e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2) {
                                    log.debug("Открытие чата с пользователем: {}", userOnline.getUser().getName());
                                    openOneToOneChat(userOnline.getUser());
                                    e.consume();
                                }
                            });
                            nameLabel.setStyle("-fx-font-size:14; -fx-text-fill:black; -fx-font-style: italic");
                            // Устанавливаем изображение в зависимости от статуса пользователя
                            if (userOnline.isOnline()) {
                                dotImageView.setImage(DOT_BLUE_IMG);
                            } else {
                                dotImageView.setImage(SPACE_IMG);
                            }

                            // Создаем контейнер для изображения и текста
                            HBox hbox = new HBox(dotImageView, nameLabel);
                            hbox.setStyle("-fx-alignment: center-left");
                            hbox.setSpacing(5); // Расстояние между изображением и текстом

                            // Устанавливаем контейнер в ячейку
                            setGraphic(hbox);
                        }
                    }
                };
            }
        });
    }

    private void openChat(Room room){
        log.debug("Открытие чата {}", room.getName());
        chat.showChatDialog(room);
    }

    private void openOneToOneChat(User interlocutor) {
        Long user1 = CH_CURRENT_USER.getId();
        Long user2 = interlocutor.getId();
        String roomName = "one-to-one:#" + Math.min(user1, user2) + "#" + Math.max(user1, user2);
        Room room = createNewRoomIfNeeded(roomName);

        // Делаем чат снова видимым для текущего пользователя
        Room updatedRoom = CH_ROOMS.setUserVisibility(room.getId(), CH_CURRENT_USER.getId(), true);
        if (updatedRoom != null) {
            log.debug("Чат с пользователем {} снова видим для текущего пользователя.", interlocutor.getName());
        } else {
            log.error("Не удалось сделать чат видимым для текущего пользователя.");
        }

        log.debug("Открытие чата с пользователем: {}", interlocutor.getName());
        chat.showChatDialog(room);
    }

    private ContextMenu createContextMenu(User interlocutor) {
        ContextMenu chatGroupContextMenu = new ContextMenu();
        MenuItem openChat = new MenuItem("Написать сообщение");
        openChat.setOnAction(e -> {
            log.debug("Открытие чата через контекстное меню с пользователем: {}", interlocutor.getName());
            openOneToOneChat(interlocutor);
        });
        chatGroupContextMenu.getItems().add(openChat);

        return chatGroupContextMenu;
    }

    /**
     * Создает новую комнату, если она не существует.
     *
     * @param roomName Имя комнаты.
     * @return Созданная или найденная комната.
     */
    private Room createNewRoomIfNeeded(String roomName) {
        // Поиск комнаты по имени
        Room room = CH_ROOMS.findByName(roomName);

        // Если комната не найдена, создаем новую
        if (room == null) {
            log.debug("Создание новой комнаты: {}", roomName);

            // Создаем новую комнату
            Room newRoom = new Room();
            newRoom.setName(roomName);
            newRoom.setCreatorId(CH_CURRENT_USER.getId());

            // Создаем объект Roommate для текущего пользователя
            Roommate currentUserRoommate = new Roommate();
            currentUserRoommate.setUserId(CH_CURRENT_USER.getId()); // Устанавливаем ID пользователя
            currentUserRoommate.setVisibleForUser(true); // По умолчанию чат виден
            currentUserRoommate.setMember(true); // По умолчанию пользователь является участником

            // Если Roommates теперь управляются отдельно, добавляем текущего пользователя в список участников
            if (newRoom.getRoommates() == null) {
                newRoom.setRoommates(new ArrayList<>()); // Инициализируем список, если он null
            }
            newRoom.getRoommates().add(currentUserRoommate);
            if(newRoom.getName().startsWith("one-to-one")){
                Roommate otherUserRoommate = new Roommate();
                otherUserRoommate.setUserId(ChatMaster.getSecondUserInOneToOneChat(newRoom).getId()); // Устанавливаем ID пользователя
                otherUserRoommate.setVisibleForUser(true); // По умолчанию чат виден
                otherUserRoommate.setMember(true); // По умолчанию пользователь является участником
                newRoom.getRoommates().add(otherUserRoommate);
            }

            // Сохраняем новую комнату в базе данных
            room = CH_ROOMS.save(newRoom);
        }

        return room;
    }

    private void createListOfRooms() {
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
                                    openChat(room);
                                    e.consume();
                                }
                            });

                            // Создаем контекстное меню
                            ContextMenu contextMenu = new ContextMenu();

                            // Пункт меню для удаления комнаты из списка
                            MenuItem deleteItem = new MenuItem("Удалить из списка");
                            deleteItem.setOnAction(event -> deleteRoomFromList(room));

                            // Пункт меню для выхода из чата (только для групповых чатов)
                            MenuItem exitItem = new MenuItem("Выйти из чата");
                            exitItem.setOnAction(event -> exitFromChat(room));

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

    private void deleteRoomFromList(Room room) {
        // Логика для удаления комнаты из списка
        if (room != null) {
            // Получаем ID текущего пользователя
            Long currentUserId = CH_CURRENT_USER.getId();

            // Скрываем комнату для пользователя
            Room updatedRoom = CH_ROOMS.setUserVisibility(room.getId(), currentUserId, false);
            if (updatedRoom != null) {
                // Удаляем комнату из списка
                listOfRooms.getItems().remove(room); // Удаляем комнату из ObservableList
            } else {
                log.error("Ошибка при скрытии комнаты.");
            }
        }
    }

    private void exitFromChat(Room room) {
        // Логика для выхода из чата
        if (room != null) {
            // Предположим, что currentUserId — это ID текущего пользователя
            Long currentUserId = CH_CURRENT_USER.getId(); // Нужно реализовать метод для получения ID текущего пользователя
            Room updatedRoom = CH_ROOMS.setUserMembership(room.getId(), currentUserId, false);
            if (updatedRoom != null) {
                System.out.println("Пользователь вышел из чата: " + room.getName());
                // Дополнительные действия, например, обновление UI
            } else {
                System.out.println("Ошибка при выходе из чата.");
            }
        }
    }
}
