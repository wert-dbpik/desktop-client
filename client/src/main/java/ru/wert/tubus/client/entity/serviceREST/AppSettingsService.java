package ru.wert.tubus.client.entity.serviceREST;

import retrofit2.Call;
import ru.wert.tubus.client.entity.api_interfaces.AppSettingsApiInterface;
import ru.wert.tubus.client.entity.models.AppSettings;
import ru.wert.tubus.client.entity.models.User;
import ru.wert.tubus.client.entity.service_interfaces.IAppSettingsService;
import ru.wert.tubus.client.interfaces.ItemService;
import ru.wert.tubus.client.retrofit.RetrofitClient;
import ru.wert.tubus.client.utils.BLlinks;

import java.io.IOException;
import java.util.List;

public class AppSettingsService implements IAppSettingsService, ItemService<AppSettings> {

    private static AppSettingsService instance;
    private AppSettingsApiInterface api;

    private AppSettingsService() {
        BLlinks.appSettingsService = this;
        api = RetrofitClient.getInstance().getRetrofit().create(AppSettingsApiInterface.class);
    }

    public AppSettingsApiInterface getApi() {
        return api;
    }

    public static AppSettingsService getInstance() {
        if (instance == null)
            instance = new AppSettingsService();
        return instance;
    }

    @Override
    public AppSettings findById(Long id) {
        try {
            Call<AppSettings> call = api.getById(id);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public AppSettings findByName(String name) {
        try {
            Call<AppSettings> call = api.getAllByName(name);
            return call.execute().body();
        } catch (IOException e) {
            return null;
        }
    }
    @Override
    public List<AppSettings> findAllByUser(User user) {
        Long userId = user.getId();
        try {
            Call<List<AppSettings>> call = api.getAllByUserId(userId);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<AppSettings> findAll() {
        try {
            Call<List<AppSettings>> call = api.getAll();
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<AppSettings> findAllByText(String text) {
        try {
            Call<List<AppSettings>> call = api.getAllByText(text);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public AppSettings save(AppSettings entity) {
        try {
            Call<AppSettings> call = api.create(entity);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean update(AppSettings entity) {
        try {
            Call<Void> call = api.update(entity);
            return call.execute().isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(AppSettings entity) {
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
