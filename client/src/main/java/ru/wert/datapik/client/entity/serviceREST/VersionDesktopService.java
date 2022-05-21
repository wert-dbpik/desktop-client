package ru.wert.datapik.client.entity.serviceREST;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.extern.slf4j.Slf4j;
import retrofit2.Call;
import ru.wert.datapik.client.entity.api_interfaces.VersionDesktopApiInterface;
import ru.wert.datapik.client.entity.models.VersionDesktop;
import ru.wert.datapik.client.entity.service_interfaces.IVersionDesktopService;
import ru.wert.datapik.client.interfaces.ItemService;
import ru.wert.datapik.client.retrofit.RetrofitClient;
import ru.wert.datapik.client.utils.BLlinks;

import java.io.IOException;
import java.util.List;

@Slf4j
public class VersionDesktopService implements IVersionDesktopService, ItemService<VersionDesktop> {

    private static VersionDesktopService instance;
    private VersionDesktopApiInterface api;

    private VersionDesktopService() {
        BLlinks.versionDesktopService = this;
        api = RetrofitClient.getInstance().getRetrofit().create(VersionDesktopApiInterface.class);
    }

    public VersionDesktopApiInterface getApi() {
        return api;
    }

    public static VersionDesktopService getInstance() {
        if (instance == null)
            return new VersionDesktopService();
        return instance;
    }

    @Override
    public VersionDesktop findById(Long id) {
        try {
            Call<VersionDesktop> call = api.getById(id);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public VersionDesktop findByName(String name) {
        try {
            Call<VersionDesktop> call = api.getByName(name);
            VersionDesktop res = call.execute().body();
                if(res != null) return res;
        } catch (IOException e) {
            return null;
        }
        return null;
    }
    
    @Override
    public List<VersionDesktop> findAll() {
        try {
            Call<List<VersionDesktop>> call = api.getAll();
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<VersionDesktop> findAllByText(String text) {
        //не используется
        return null;
    }

    @Override
    public VersionDesktop save(VersionDesktop entity) {
        try {
            Call<VersionDesktop> call = api.create(entity);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean update(VersionDesktop entity) {
        try {
            Call<Void> call = api.update(entity);
            return call.execute().isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(VersionDesktop entity) {
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
