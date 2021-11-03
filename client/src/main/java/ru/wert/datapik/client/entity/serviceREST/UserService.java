package ru.wert.datapik.client.entity.serviceREST;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.extern.slf4j.Slf4j;
import retrofit2.Call;
import ru.wert.datapik.client.entity.api_interfaces.UserApiInterface;
import ru.wert.datapik.client.entity.models.User;
import ru.wert.datapik.client.entity.service_interfaces.IUserService;
import ru.wert.datapik.client.interfaces.ItemService;
import ru.wert.datapik.client.retrofit.RetrofitClient;
import ru.wert.datapik.client.utils.BLlinks;

import java.io.IOException;
import java.util.List;

@Slf4j
public class UserService implements IUserService, ItemService<User> {

    private static UserService instance;
    private UserApiInterface api;

    private UserService() {
        BLlinks.userService = this;
        api = RetrofitClient.getInstance().getRetrofit().create(UserApiInterface.class);
    }

    public UserApiInterface getApi() {
        return api;
    }

    public static UserService getInstance() {
        if (instance == null)
            return new UserService();
        return instance;
    }

    @Override
    public User findById(Long id) {
        try {
            Call<User> call = api.getById(id);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public User findByName(String name) {
        try {
            Call<User> call = api.getByName(name);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User findByPassword(String pass) {
        try {
            Call<User> call = api.getByPassword(pass);
            return call.execute().body();
        } catch (IOException e) {
            log.info("Неудачная попытка войти по паролю {}", pass);
        }
        return null;
    }

    @Override
    public ObservableList<User> findAll() {
        try {
            Call<List<User>> call = api.getAll();
            return FXCollections.observableArrayList(call.execute().body());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ObservableList<User> findAllByText(String text) {
        try {
            Call<List<User>> call = api.getAllByText(text);
            return FXCollections.observableArrayList(call.execute().body());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User save(User entity) {
        try {
            Call<User> call = api.create(entity);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean update(User entity) {
        try {
            Call<Void> call = api.update(entity);
            return call.execute().isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(User entity) {
        Long id = entity.getId();
        try {
            Call<Void> call = api.deleteById(id);
            return call.execute().isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
