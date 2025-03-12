package ru.wert.tubus.client.entity.serviceQUICK;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.wert.tubus.client.entity.models.User;
import ru.wert.tubus.client.entity.serviceREST.UserService;

import java.util.Optional;

public class UserQuickService {

    private static volatile UserQuickService instance; // volatile для потокобезопасности
    public static ObservableList<User> users; // Не static, чтобы каждый экземпляр имел свою копию
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
            users = FXCollections.observableArrayList(service.findAll());
        } else {
            users = FXCollections.observableArrayList(); // Инициализация пустым списком, если service == null
        }
    }

    // Метод для получения списка пользователей
    public ObservableList<User> findAll() {
        return users;
    }

    // Метод для поиска пользователя по ID
    public Optional<User> fetchUserById(Long id) {
        if (users == null || id == null) {
            return Optional.empty();
        }
        return users.stream()
                .filter(user -> id.equals(user.getId()))
                .findFirst();
    }
}
