package ru.wert.tubus.client.entity.serviceREST;

import retrofit2.Call;
import ru.wert.tubus.client.entity.api_interfaces.PassportApiInterface;
import ru.wert.tubus.client.entity.models.Passport;
import ru.wert.tubus.client.entity.models.Prefix;
import ru.wert.tubus.client.entity.service_interfaces.IPassportService;
import ru.wert.tubus.client.interfaces.ItemService;
import ru.wert.tubus.client.retrofit.RetrofitClient;
import ru.wert.tubus.client.utils.BLlinks;

import java.io.IOException;
import java.util.List;

public class PassportService implements IPassportService, ItemService<Passport> {

    private static PassportService instance;
    private PassportApiInterface api;

    private PassportService() {
        BLlinks.passportService = this;
        api = RetrofitClient.getInstance().getRetrofit().create(PassportApiInterface.class);
    }

    public PassportApiInterface getApi() {
        return api;
    }

    public static PassportService getInstance() {
        if (instance == null)
            instance =  new PassportService();
        return instance;
    }

    @Override
    public Passport findById(Long id) {
        try {
            Call<Passport> call = api.getById(id);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Passport> findAll() {
        try {
            Call<List<Passport>> call = api.getAll();
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Passport findByPrefixIdAndNumber(Prefix prefix, String number) {

        try {
            Call<Passport> call = api.getByPrefixIdAndNumber(prefix.getId(), number);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Passport> findAllByName(String name) {
        try {
            Call<List<Passport>> call = api.getAllByName(name);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Passport> findAllByText(String text) {
        try {
            Call<List<Passport>> call = api.getAllByText(text);;
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Passport save(Passport entity) {
        try {
            Call<Passport> call = api.create(entity);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean update(Passport entity) {
        try {
            Call<Void> call = api.update(entity);
            return call.execute().isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(Passport entity) {
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
