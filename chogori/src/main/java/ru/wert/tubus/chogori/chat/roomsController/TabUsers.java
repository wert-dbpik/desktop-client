package ru.wert.tubus.chogori.chat.roomsController;

import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.chat.util.UserOnline;
import ru.wert.tubus.chogori.images.BtnImages;
import ru.wert.tubus.client.entity.models.User;
import ru.wert.tubus.client.entity.serviceQUICK.UserQuickService;

import static ru.wert.tubus.chogori.application.services.ChogoriServices.CH_USERS;
import static ru.wert.tubus.chogori.chat.roomsController.RoomsController.WRIGHT_YOURSELF;
import static ru.wert.tubus.chogori.images.BtnImages.DOT_BLUE_IMG;
import static ru.wert.tubus.chogori.images.BtnImages.SPACE_IMG;
import static ru.wert.tubus.chogori.setteings.ChogoriSettings.CH_CURRENT_USER;

@Slf4j
public class TabUsers {
    private final Tab tabPaneUsers;
    private final ListView<UserOnline> listOfUsers;
    private RoomsController controller;


    public TabUsers(RoomsController controller) {
        this.controller = controller;
        this.tabPaneUsers = controller.getTabPaneUsers();
        this.listOfUsers = controller.getListOfUsers();
    }

    public void createListOfUsers() {
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
                                    controller.openOneToOneChat(userOnline.getUser());
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

    /**
     * В методе создается пустой список, в который первым добавляется сам пользователь (написать себе)
     * Затем добавляется отсортированный список остальных пользователей за минусом Гостя
     */
    public void updateListOfUsers() {
        log.debug("Обновление списка пользователей");
        controller.getUsersOnline().clear();
        for (User user : UserQuickService.users) {
            controller.getUsersOnline().add(new UserOnline(user, user.isOnline()));
        }

        sortUsersOnlineByName();
        listOfUsers.setItems(controller.getUsersOnline());
    }

    private ContextMenu createContextMenu(User interlocutor) {
        ContextMenu chatGroupContextMenu = new ContextMenu();
        MenuItem openChat = new MenuItem("Написать сообщение");
        openChat.setOnAction(e -> {
            log.debug("Открытие чата через контекстное меню с пользователем: {}", interlocutor.getName());
            controller.openOneToOneChat(interlocutor);
        });
        chatGroupContextMenu.getItems().add(openChat);

        return chatGroupContextMenu;
    }

    private void sortUsersOnlineByName() {
        log.debug("Сортировка пользователей по имени");
        String currentUserName = CH_CURRENT_USER.getName(); // Имя текущего пользователя

        controller.getUsersOnline().sort((userOnline1, userOnline2) -> {
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

}
