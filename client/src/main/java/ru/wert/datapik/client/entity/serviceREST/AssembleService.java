package ru.wert.datapik.client.entity.serviceREST;


import retrofit2.Call;
import ru.wert.datapik.client.entity.models.Assemble;
import ru.wert.datapik.client.entity.models.Detail;
import ru.wert.datapik.client.entity.models.Passport;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.client.entity.api_interfaces.AssembleApiInterface;
import ru.wert.datapik.client.entity.service_interfaces.IAssembleService;
import ru.wert.datapik.client.interfaces.ItemService;
import ru.wert.datapik.client.retrofit.RetrofitClient;
import ru.wert.datapik.client.utils.BLlinks;

import java.io.IOException;
import java.util.List;

public class AssembleService implements IAssembleService, ItemService<Assemble> {

    private static AssembleService instance;
    private AssembleApiInterface api;

    private AssembleService() {
        BLlinks.assembleService = this;
        api = RetrofitClient.getInstance().getRetrofit().create(AssembleApiInterface.class);
    }

    public AssembleApiInterface getApi() {
        return api;
    }

    public static AssembleService getInstance() {
        if (instance == null)
            return new AssembleService();
        return instance;
    }

    @Override
    public Assemble findById(Long id) {
        try {
            Call<Assemble> call = api.getById(id);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Assemble findByPassportId(Long id) {
        try {
            Call<Assemble> call = api.getByPassportId(id);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public List<Assemble> findAllByFolderId(Long id) {
        try {
            Call<List<Assemble>> call = api.getAllByFolderId(id);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Assemble> findAll() {
        try {
            Call<List<Assemble>> call = api.getAll();
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Assemble> findAllByText(String text) {
        try {
            Call<List<Assemble>> call = api.getAllByText(text);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Assemble> findAllByDraftId(Long id) {
        try {
            Call<List<Assemble>> call = api.getAllByDraftId(id);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Assemble save(Assemble entity) {
        try {
            Call<Assemble> call = api.create(entity);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean update(Assemble entity) {
        try {
            Call<Void> call = api.update(entity);
            return call.execute().isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(Assemble entity) {
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
    public String getPartName(Item assemble) {
        Passport p = ((Assemble)assemble).getPassport();
        return p.getPrefix().getName() + "." + p.getNumber() + "-" + ((Assemble)assemble).getVariant(); //ПИК.745222.123-02
    }
}
