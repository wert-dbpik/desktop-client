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


    @Override
    public User findByName(String name) {
        return null;
    }

    @Override
    public User findByPassword(String pass) {
        return null;
    }

    @Override
    public List<Room> subscribeRoom(User user, Room room) {
        return null;
    }

    @Override
    public List<Room> unsubscribeRoom(User user, Room room) {
        return null;
    }

    @Override
    public User findById(Long id) {
        return null;
    }

    @Override
    public User save(User user) {
        return null;
    }

    @Override
    public boolean update(User user) {
        return false;
    }

    @Override
    public boolean delete(User user) {
        return false;
    }

    @Override
    public List<User> findAll() {
        return null;
    }

    @Override
    public List<User> findAllByText(String text) {
        return null;
    }
}
