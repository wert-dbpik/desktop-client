package ru.wert.datapik.client.entity.serviceREST;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import retrofit2.Call;
import ru.wert.datapik.client.entity.api_interfaces.ProductApiInterface;
import ru.wert.datapik.client.entity.models.*;
import ru.wert.datapik.client.entity.service_interfaces.IProductService;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.client.utils.BLlinks;
import ru.wert.datapik.client.interfaces.ItemService;
import ru.wert.datapik.client.interfaces.PartItem;
import ru.wert.datapik.client.retrofit.RetrofitClient;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class ProductService implements IProductService, PartItem {

    private static ProductService instance;
    private ProductApiInterface api;

    private ProductService() {
        BLlinks.productService = this;
        api = RetrofitClient.getInstance().getRetrofit().create(ProductApiInterface.class);
    }

    public ProductApiInterface getApi() {
        return api;
    }

    public static ProductService getInstance() {
        if (instance == null)
            return new ProductService();
        return instance;
    }

    @Override
    public Product findById(Long id) {
        try {
            Call<Product> call = api.getById(id);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public Product findByPassportId(Long id) {
        try {
            Call<Product> call = api.getByPassportId(id);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Product> findAllByFolderId(Long id) {
        try {
            Call<List<Product>> call = api.getAllByFolderId(id);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Product> findAllByGroupId(Long id) {
        try {
            Call<List<Product>> call = api.getAllByProductGroupId(id);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Product> findAllByProductGroup(ProductGroup group) {
        return null;
    }


    @Override
    public ObservableList<Product> findAll() {
        try {
            Call<List<Product>> call = api.getAll();
            return FXCollections.observableArrayList(call.execute().body());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ObservableList<Product> findAllByText(String text) {
        try {
            Call<List<Product>> call = api.getAllByText(text);
            return FXCollections.observableArrayList(call.execute().body());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Product save(Product entity) {
        try {
            Call<Product> call = api.create(entity);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean update(Product entity) {
        try {
            Call<Void> call = api.update(entity);
            return call.execute().isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(Product entity) {
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
    public String getPartName(Item product) {
        Passport p = ((Product)product).getPassport();
        return p.getPrefix().getName() + "." + p.getNumber() + "-" + ((Product)product).getVariant(); //ПИК.745222.123-02
    }

    // ЧЕРТЕЖИ


    @Override
    public List<Draft> findDrafts(Product product) {
        try {
            Call<List<Draft>> call = api.getDrafts(product.getId());
            List<Draft> res = call.execute().body();
            if(res != null)
                return res;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Draft> addDrafts(Product product, Draft draft) {
        try {
            Call<List<Draft>> call = api.addDraft(product.getId(), draft.getId());
            List<Draft> res = call.execute().body();
            if(res != null)
                return res;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Draft> removeDrafts(Product product, Draft draft) {
        try {
            Call<List<Draft>> call = api.removeDraft(product.getId(), draft.getId());
            List<Draft> res = call.execute().body();
            if(res != null)
                return res;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
