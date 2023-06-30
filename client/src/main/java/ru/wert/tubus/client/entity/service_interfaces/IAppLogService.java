package ru.wert.tubus.client.entity.service_interfaces;

import ru.wert.tubus.client.entity.models.AppLog;
import ru.wert.tubus.client.entity.models.User;
import ru.wert.tubus.client.interfaces.ItemService;

import java.time.LocalDateTime;
import java.util.List;


public interface IAppLogService extends ItemService<AppLog> {

    AppLog findByName(String name);
    List<AppLog> findAllByTimeBetween(LocalDateTime startTime, LocalDateTime finishTime);
    List<AppLog> findAllByTimeBetweenAndAdminOnlyFalse(LocalDateTime startTime, LocalDateTime finishTime);
    List<AppLog> findAllByUser(User user);
    List<AppLog> findAllByUserAndAdminOnlyFalse(User user);
    List<AppLog> findAllByApplication(Integer app);
    List<AppLog> findAllByApplicationAndAdminOnlyFalse(Integer app);
    List<AppLog> findAllByAdminOnlyFalse();

}
