package ru.wert.datapik.client.entity.serviceREST;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import retrofit2.Call;
import ru.wert.datapik.client.entity.api_interfaces.MatTypeApiInterface;
import ru.wert.datapik.client.entity.models.MatType;
import ru.wert.datapik.client.entity.service_interfaces.IMatTypeService;
import ru.wert.datapik.client.interfaces.ItemService;
import ru.wert.datapik.client.retrofit.RetrofitClient;
import ru.wert.datapik.client.utils.BLlinks;

import java.io.IOException;
import java.util.List;

public class MatTypeService implements IMatTypeService, ItemService<MatType> {

    private static MatTypeService instance;
    private MatTypeApiInterface api;

    private MatTypeService() {
        BLlinks.matTypeService = this;
        api = RetrofitClient.getInstance().getRetrofit().create(MatTypeApiInterface.class);
    }

    public MatTypeApiInterface getApi() {
        return api;
    }

    public static MatTypeService getInstance() {
        if (instance == null)
            return new MatTypeService();
        return instance;
    }

    @Override
    public MatType findById(Long id) {
        try {
            Call<MatType> call = api.getById(id);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public MatType findByName(String name) {
        try {
            Call<MatType> call = api.getByName(name);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ObservableList<MatType> findAll() {
        try {
            Call<List<MatType>> call = api.getAll();
            return FXCollections.observableArrayList(call.execute().body());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ObservableList<MatType> findAllByText(String text) {
        try {
            Call<List<MatType>> call = api.getAllByText(text);
            return FXCollections.observableArrayList(call.execute().body());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public MatType save(MatType entity) {
        try {
            Call<MatType> call = api.create(entity);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean update(MatType entity) {
        try {
            Call<Void> call = api.update(entity);
            return call.execute().isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(MatType entity) {
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
