package ru.wert.datapik.client.entity.service_interfaces;

import retrofit2.Call;
import retrofit2.http.Path;
import ru.wert.datapik.client.entity.models.Passport;
import ru.wert.datapik.client.entity.models.Pic;
import ru.wert.datapik.client.entity.models.Remark;
import ru.wert.datapik.client.interfaces.ItemService;

import java.util.List;
import java.util.Set;


public interface IRemarkService extends ItemService<Remark> {

    Remark findByName(String name);

    List<Remark> findAllByPassport(Passport passport);

    List<Pic> getPics(Remark remark);

}
