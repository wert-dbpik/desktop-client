package ru.wert.tubus.client.entity.serviceREST;

import retrofit2.Call;
import ru.wert.tubus.client.entity.api_interfaces.ProductGroupApiInterface;
import ru.wert.tubus.client.entity.models.ProductGroup;
import ru.wert.tubus.client.entity.service_interfaces.IProductGroupService;
import ru.wert.tubus.client.interfaces.ItemService;
import ru.wert.tubus.client.retrofit.RetrofitClient;
import ru.wert.tubus.client.utils.BLlinks;

import java.io.IOException;
import java.util.List;

public class ProductGroupService implements IProductGroupService, ItemService<ProductGroup> {

    private static ProductGroupService instance;
    private ProductGroupApiInterface api;

    private ProductGroupService() {
        BLlinks.productTreeGroupService = this;
        api = RetrofitClient.getInstance().getRetrofit().create(ProductGroupApiInterface.class);
    }

    public ProductGroupApiInterface getApi() {
        return api;
    }

    public static ProductGroupService getInstance() {
        if (instance == null)
            instance = new ProductGroupService();
        return instance;
    }

    @Override
    public ProductGroup findById(Long id) {
        try {
            Call<ProductGroup> call = api.getById(id);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public ProductGroup findByName(String name) {
        try {
            Call<ProductGroup> call = api.getByName(name);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<ProductGroup> findAll() {
        try {
            Call<List<ProductGroup>> call = api.getAll();
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<ProductGroup> findAllByText(String text) {
        try {
            Call<List<ProductGroup>> call = api.getAllByText(text);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ProductGroup save(ProductGroup entity) {
        try {
            Call<ProductGroup> call = api.create(entity);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean update(ProductGroup entity) {
        try {
            Call<Void> call = api.update(entity);
            return call.execute().isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(ProductGroup entity) {
        Long id = entity.getId();
        try {
            Call<Void> call = api.deleteById(id);
            return call.execute().isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ProductGroup getRootItem(){
        ProductGroup rootItem = new ProductGroup(0L, 555L,"Изделия");
        rootItem.setId(1L);
        return rootItem;
    }
}
