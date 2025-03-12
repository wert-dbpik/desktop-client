package ru.wert.tubus.client.entity.serviceQUICK;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.wert.tubus.client.entity.models.Folder;
import ru.wert.tubus.client.entity.models.User;
import ru.wert.tubus.client.entity.serviceREST.FolderService;
import ru.wert.tubus.client.entity.serviceREST.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserQuickService {

    private static UserQuickService instance;
    private static ObservableList<User> users;
    private static UserService service = UserService.getInstance();

    private UserQuickService() {
        reload();
    }

    public static UserQuickService getInstance() {
        if (instance == null)
            return new UserQuickService();
        return instance;
    }

    public static void reload(){
        while(true) {
            if(service != null) {
                users = FXCollections.observableArrayList(service.findAll());
                break;
            }
        }
    }

    public ObservableList<User> findAll(){
        return users;
    }

    public Optional<User> fetchUserById(Long id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }


}
