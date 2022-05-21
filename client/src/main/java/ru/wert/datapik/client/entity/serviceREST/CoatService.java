package ru.wert.datapik.client.entity.serviceREST;


import retrofit2.Call;
import ru.wert.datapik.client.entity.api_interfaces.CoatApiInterface;
import ru.wert.datapik.client.entity.models.Coat;
import ru.wert.datapik.client.entity.service_interfaces.ICoatService;
import ru.wert.datapik.client.interfaces.ItemService;
import ru.wert.datapik.client.retrofit.RetrofitClient;
import ru.wert.datapik.client.utils.BLlinks;

import java.io.IOException;
import java.util.List;

public class CoatService implements ICoatService, ItemService<Coat> {

    private static CoatService instance;
    private CoatApiInterface api;

    private CoatService() {
        BLlinks.coatService = this;
        api = RetrofitClient.getInstance().getRetrofit().create(CoatApiInterface.class);
    }

    public CoatApiInterface getApi() {
        return api;
    }

    public static CoatService getInstance() {
        if (instance == null)
            return new CoatService();
        return instance;
    }

    @Override
    public Coat findById(Long id) {
        try {
            Call<Coat> call = api.getById(id);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public Coat findByName(String name) {
        try {
            Call<Coat> call = api.getByName(name);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Coat> findAll() {
        try {
            Call<List<Coat>> call = api.getAll();
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Coat> findAllByText(String text) {
        try {
            Call<List<Coat>> call = api.getAllByText(text);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Coat save(Coat entity) {
        try {
            Call<Coat> call = api.create(entity);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean update(Coat entity) {
        try {
            Call<Void> call = api.update(entity);
            return call.execute().isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(Coat entity) {
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
