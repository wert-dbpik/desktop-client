package ru.wert.datapik.client.entity.service_interfaces;

import ru.wert.datapik.client.entity.models.AppLog;
import ru.wert.datapik.client.entity.models.User;
import ru.wert.datapik.client.interfaces.ItemService;

import java.time.LocalDateTime;
import java.util.List;


public interface IAppLogService extends ItemService<AppLog> {

    AppLog findByName(String name);
    List<AppLog> findAllByTimeBetween(LocalDateTime startTime, LocalDateTime finishTime);
    List<AppLog> findAllByUser(User user);
    List<AppLog> findAllByApplication(Integer app);

}
