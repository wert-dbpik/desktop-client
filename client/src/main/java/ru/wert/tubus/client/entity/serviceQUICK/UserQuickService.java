package ru.wert.tubus.client.entity.serviceQUICK;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.wert.tubus.client.entity.models.Room;
import ru.wert.tubus.client.entity.models.User;
import ru.wert.tubus.client.entity.serviceREST.UserService;
import ru.wert.tubus.client.entity.service_interfaces.IUserService;

import java.util.List;

public class UserQuickService implements IUserService {

    private static volatile UserQuickService instance; // volatile для потокобезопасности
    public static ObservableList<User> LOADED_USERS; // Не static, чтобы каждый экземпляр имел свою копию
    private final UserService service = UserService.getInstance();

    // Приватный конструктор
    private UserQuickService() {
        reload();
    }

    // Метод для получения экземпляра синглтона
    public static UserQuickService getInstance() {
        if (instance == null) {
            synchronized (UserQuickService.class) { // Синхронизация для потокобезопасности
                if (instance == null) {
                    instance = new UserQuickService();
                }
            }
        }
        return instance;
    }

    // Метод для перезагрузки данных
    public void reload() {
        if (service != null) {
            LOADED_USERS = FXCollections.observableArrayList(service.findAll());
        } else {
            LOADED_USERS = FXCollections.observableArrayList(); // Инициализация пустым списком, если service == null
        }
    }
    
    // Метод для получения списка пользователей
    public ObservableList<User> findAll() {
        return LOADED_USERS;
    }

    // Метод для поиска пользователя по ID
    public Optional<User> fetchUserById(Long id) {
        if (LOADED_USERS == null || id == null) {
            return Optional.empty();
        }
        return LOADED_USERS.stream()
                .filter(user -> id.equals(user.getId()))
                .findFirst();

    }
}
