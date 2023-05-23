package ru.wert.datapik.client.entity.serviceREST;

import lombok.extern.slf4j.Slf4j;
import retrofit2.Call;
import ru.wert.datapik.client.entity.api_interfaces.VersionNormicApiInterface;
import ru.wert.datapik.client.entity.models.VersionNormic;
import ru.wert.datapik.client.entity.service_interfaces.IVersionNormicService;
import ru.wert.datapik.client.interfaces.ItemService;
import ru.wert.datapik.client.retrofit.RetrofitClient;
import ru.wert.datapik.client.utils.BLlinks;

import java.io.IOException;
import java.util.List;

@Slf4j
public class VersionNormicService implements IVersionNormicService, ItemService<VersionNormic> {

    private static VersionNormicService instance;
    private VersionNormicApiInterface api;

    private VersionNormicService() {
        BLlinks.versionNormicService = this;
        api = RetrofitClient.getInstance().getRetrofit().create(VersionNormicApiInterface.class);
    }

    public VersionNormicApiInterface getApi() {
        return api;
    }

    public static VersionNormicService getInstance() {
        if (instance == null)
            return new VersionNormicService();
        return instance;
    }

    @Override
    public VersionNormic findById(Long id) {
        try {
            Call<VersionNormic> call = api.getById(id);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public VersionNormic findByName(String name) {
        try {
            Call<VersionNormic> call = api.getByName(name);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public List<VersionNormic> findAll() {
        try {
            Call<List<VersionNormic>> call = api.getAll();
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<VersionNormic> findAllByText(String text) {
        //не используется
        return null;
    }

    @Override
    public VersionNormic save(VersionNormic entity) {
        try {
            Call<VersionNormic> call = api.create(entity);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean update(VersionNormic entity) {
        try {
            Call<Void> call = api.update(entity);
            return call.execute().isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(VersionNormic entity) {
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
