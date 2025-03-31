package ru.wert.tubus.client.entity.serviceQUICK;

import javafx.collections.FXCollections;
import ru.wert.tubus.client.entity.models.Room;
import ru.wert.tubus.client.entity.models.User;
import ru.wert.tubus.client.entity.serviceREST.UserService;
import ru.wert.tubus.client.entity.service_interfaces.IUserService;
import ru.wert.tubus.client.interfaces.ItemService;

import java.util.List;

public class UserQuickService implements IUserService, ItemService<User> {

    private static volatile UserQuickService instance; // volatile для потокобезопасности
    public static List<User> LOADED_USERS; // Не static, чтобы каждый экземпляр имел свою копию
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


    @Override
    public User findByName(String name) {
        User user = null;
        for(User u : LOADED_USERS){
            if(u.getName().equals(name)) {
                user = u;
                break;
            }
        }
        return user;
    }

    @Override
    public User findByPassword(String pass) {
        User user = null;
        for(User u : LOADED_USERS){
            if(u.getPassword().equals(pass)) {
                user = u;
                break;
            }
        }
        return user;
    }

    @Override
    public List<Room> subscribeRoom(User user, Room room) {
        List<Room> rooms = service.subscribeRoom(user, room);
        reload();
        return rooms;
    }

    @Override
    public List<Room> unsubscribeRoom(User user, Room room) {
        List<Room> rooms = service.unsubscribeRoom(user, room);
        reload();
        return rooms;
    }

    @Override
    public User findById(Long id) {
        return null;
    }

    @Override
    public User save(User user) {
        User res = service.save(user);
        reload();
        return res;
    }

    @Override
    public boolean update(User user) {
        boolean res = service.update(user);
        reload();
        return res;
    }

    @Override
    public boolean delete(User user) {
        boolean res = service.delete(user);
        reload();
        return res;
    }

    @Override
    public List<User> findAll() {
        return LOADED_USERS;
    }

    @Override
    public List<User> findAllByText(String text) {
        return null;
    }
}
