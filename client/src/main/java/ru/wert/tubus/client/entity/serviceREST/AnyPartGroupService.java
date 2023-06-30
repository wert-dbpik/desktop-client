package ru.wert.tubus.client.entity.serviceREST;

import retrofit2.Call;
import ru.wert.tubus.client.entity.api_interfaces.AnyPartGroupApiInterface;
import ru.wert.tubus.client.entity.models.AnyPartGroup;
import ru.wert.tubus.client.entity.service_interfaces.IAnyPartGroupService;
import ru.wert.tubus.client.interfaces.ItemService;
import ru.wert.tubus.client.retrofit.RetrofitClient;
import ru.wert.tubus.client.utils.BLlinks;

import java.io.IOException;
import java.util.List;

public class AnyPartGroupService implements IAnyPartGroupService, ItemService<AnyPartGroup> {

    private static AnyPartGroupService instance;

    public static AnyPartGroupService getInstance() {
        if (instance == null)
            return new AnyPartGroupService();
        return instance;
    }

    private AnyPartGroupApiInterface api;

    private AnyPartGroupService() {
        BLlinks.partTypeService = this;
        api = RetrofitClient.getInstance().getRetrofit().create(AnyPartGroupApiInterface.class);
    }

    public AnyPartGroupApiInterface getApi() {
        return api;
    }


    @Override
    public AnyPartGroup findById(Long id) {
        try {
            Call<AnyPartGroup> call = api.getById(id);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public AnyPartGroup findByName(String name) {
        try {
            Call<AnyPartGroup> call = api.getByName(name);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<AnyPartGroup> findAll() {
        try {
            Call<List<AnyPartGroup>> call = api.getAll();
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<AnyPartGroup> findAllByText(String text) {
        try {
            Call<List<AnyPartGroup>> call = api.getAllByText(text);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public AnyPartGroup save(AnyPartGroup entity) {
        try {
            Call<AnyPartGroup> call = api.create(entity);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean update(AnyPartGroup entity) {
        try {
            Call<Void> call = api.update(entity);
            return call.execute().isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(AnyPartGroup entity) {
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
