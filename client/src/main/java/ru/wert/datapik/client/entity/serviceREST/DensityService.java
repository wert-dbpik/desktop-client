package ru.wert.datapik.client.entity.serviceREST;

import javafx.collections.FXCollections;

import javafx.collections.ObservableList;
import retrofit2.Call;
import ru.wert.datapik.client.entity.api_interfaces.DensityApiInterface;
import ru.wert.datapik.client.entity.models.Density;
import ru.wert.datapik.client.entity.service_interfaces.IDensityService;
import ru.wert.datapik.client.interfaces.ItemService;
import ru.wert.datapik.client.retrofit.RetrofitClient;
import ru.wert.datapik.client.utils.BLlinks;

import java.io.IOException;
import java.util.List;

public class DensityService implements IDensityService, ItemService<Density> {

    private static DensityService instance;
    private DensityApiInterface api;

    private DensityService() {
        BLlinks.densityService = this;
        api = RetrofitClient.getInstance().getRetrofit().create(DensityApiInterface.class);
    }

    public DensityApiInterface getApi() {
        return api;
    }

    public static DensityService getInstance() {
        if (instance == null)
            return new DensityService();
        return instance;
    }

    @Override
    public Density findById(Long id) {
        try {
            Call<Density> call = api.getById(id);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public Density findByName(String name) {
        try {
            Call<Density> call = api.getByName(name);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Density findByValue(double value) {
        try {
            Call<Density> call = api.getByValue(value);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ObservableList<Density> findAll() {
        try {
            Call<List<Density>> call = api.getAll();
            return FXCollections.observableArrayList(call.execute().body());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ObservableList<Density> findAllByText(String text) {
        try {
            Call<List<Density>> call = api.getAllByText(text);
            return FXCollections.observableArrayList(call.execute().body());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Density save(Density entity) {
        try {
            Call<Density> call = api.create(entity);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean update(Density entity) {
        try {
            Call<Void> call = api.update(entity);
            return call.execute().isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(Density entity) {
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
