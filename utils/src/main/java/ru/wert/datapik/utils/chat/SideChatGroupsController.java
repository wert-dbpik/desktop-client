package ru.wert.datapik.utils.chat;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import ru.wert.datapik.client.entity.models.ChatGroup;
import ru.wert.datapik.client.entity.models.User;
import ru.wert.datapik.utils.statics.Comparators;

import java.util.ArrayList;
import java.util.List;

import static ru.wert.datapik.utils.images.BtnImages.CHATS_IMG;
import static ru.wert.datapik.utils.images.BtnImages.MAN_IMG;
import static ru.wert.datapik.utils.services.ChogoriServices.*;
import static ru.wert.datapik.utils.setteings.ChogoriSettings.CH_CURRENT_USER;

public class SideChatGroupsController {

    @FXML
    private Button btnAddNewChatGroup;

    @FXML
    private ListView<User> listOfUsers;

    @FXML
    private ListView<ChatGroup> listOfChats;

    @FXML
    private Tab tabPaneChats;

    @FXML
    private Tab tabPaneUsers;

    private SideChat chat;

    @FXML
    void initialize(){
        createListOfUsers();
        createListOfChatGroups();

        updateListOfUsers();
        updateListOfGroups();

        //Временно
        btnAddNewChatGroup.setOnAction(e->{
            chat.showChatTalk();
        });
    }

    public void init(SideChat chat){
        this.chat = chat;
    }

    /**
     * В методе создается пустой список, в который первым добавляется сам пользователь (написать себе)
     * Затем добавляется отсортированный список остальных пользователей за минусом Гостя
     */
    private void updateListOfUsers() {
        ArrayList<User > users = new ArrayList<>();
        users.add(CH_CURRENT_USER);

        List<User> allUsers = CH_USERS.findAll();

        for(int i=0; i< allUsers.size(); i++){
            User u = allUsers.get(i);
            if(u.getName().equals("Гость") || u.equals(CH_CURRENT_USER))
                allUsers.remove(u);
        }
        allUsers.sort(Comparators.usefulStringComparator());
        users.addAll(allUsers);
        listOfUsers.getItems().clear();
        listOfUsers.setItems(FXCollections.observableArrayList(users));
    }

    /**
     * В методе создается пустой список, который выводится в ListView, если в базе еще нет чатов
     */
    private void updateListOfGroups() {
        List<ChatGroup> groups = new ArrayList<>();
        List<ChatGroup> allGroups = CH_CHAT_GROUPS.findAll();

        if(allGroups != null && !allGroups.isEmpty())
            groups.addAll(allGroups);
        listOfChats.getItems().clear();
        listOfChats.setItems(FXCollections.observableArrayList(groups));

    }


    private void createListOfUsers() {
        tabPaneUsers.setGraphic(new ImageView(MAN_IMG));
        listOfUsers.setPlaceholder(new Label("Пользователей не найдено"));
        listOfUsers.setCellFactory(new Callback<ListView<User>, ListCell<User>>() {
            public ListCell<User> call(ListView<User> param) {
                final Label userLabel = new Label();
                final ListCell<User> cell = new ListCell<User>() {
                    @Override
                    public void updateItem(User item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        }  else {
                            setText(null);
                            if(item.equals(CH_CURRENT_USER))
                                userLabel.setText("Написать себе");
                            else
                                userLabel.setText(item.getName());
                            userLabel.setStyle("-fx-font-size:14; -fx-text-fill:black; -fx-font-style: italic");
                            setGraphic(userLabel);
                        }
                    }
                };
                return cell;
            }
        });
    }

    private void createListOfChatGroups() {
        tabPaneChats.setGraphic(new ImageView(CHATS_IMG));
        Label placeholder = new Label("Ни одного чата\nеще не создано");
        placeholder.setStyle("-fx-text-fill: saddlebrown; -fx-font-size: 14; -fx-font-style: italic; -fx-font-weight: bold");
        listOfChats.setPlaceholder(placeholder);
        listOfChats.setCellFactory(new Callback<ListView<ChatGroup>, ListCell<ChatGroup>>() {
            public ListCell<ChatGroup> call(ListView<ChatGroup> param) {
                final Label userLabel = new Label();
                final ListCell<ChatGroup> cell = new ListCell<ChatGroup>() {
                    @Override
                    public void updateItem(ChatGroup item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setText(null);
                            userLabel.setText(item.getName());
                            userLabel.setStyle("-fx-font-style: italic");
                            setGraphic(userLabel);
                        }
                    }
                };
                return cell;
            }
        });
    }

}
