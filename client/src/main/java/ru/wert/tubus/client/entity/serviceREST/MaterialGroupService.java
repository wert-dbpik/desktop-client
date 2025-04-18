package ru.wert.tubus.client.entity.serviceREST;

import retrofit2.Call;
import ru.wert.tubus.client.entity.api_interfaces.MaterialGroupApiInterface;
import ru.wert.tubus.client.entity.models.MaterialGroup;
import ru.wert.tubus.client.entity.service_interfaces.IMaterialGroupService;
import ru.wert.tubus.client.interfaces.ItemService;
import ru.wert.tubus.client.retrofit.RetrofitClient;
import ru.wert.tubus.client.utils.BLlinks;

import java.io.IOException;
import java.util.List;

public class MaterialGroupService implements IMaterialGroupService, ItemService<MaterialGroup> {

    private static MaterialGroupService instance;
    private MaterialGroupApiInterface api;

    private MaterialGroupService() {
        BLlinks.materialTreeGroupService = this;
        api = RetrofitClient.getInstance().getRetrofit().create(MaterialGroupApiInterface.class);
    }

    public MaterialGroupApiInterface getApi() {
        return api;
    }

    public static MaterialGroupService getInstance() {
        if (instance == null)
            instance = new MaterialGroupService();
        return instance;
    }

    @Override
    public MaterialGroup findById(Long id) {
        try {
            Call<MaterialGroup> call = api.getById(id);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public MaterialGroup findByName(String name) {
        try {
            Call<MaterialGroup> call = api.getByName(name);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<MaterialGroup> findAll() {
        try {
            Call<List<MaterialGroup>> call = api.getAll();
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<MaterialGroup> findAllByText(String text) {
        try {
            Call<List<MaterialGroup>> call = api.getAllByText(text);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public MaterialGroup save(MaterialGroup entity) {
        try {
            Call<MaterialGroup> call = api.create(entity);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean update(MaterialGroup entity) {
        try {
            Call<Void> call = api.update(entity);
            return call.execute().isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(MaterialGroup entity) {
        Long id = entity.getId();
        try {
            Call<Void> call = api.deleteById(id);
            return call.execute().isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    //Получаем строку из модели Класса, там ее удобнее написать
    @Override
    public MaterialGroup getRootItem(){
        MaterialGroup rootItem = new MaterialGroup(0L, 555L,"Материалы");
        rootItem.setId(1L);
        return rootItem;
    }


}
