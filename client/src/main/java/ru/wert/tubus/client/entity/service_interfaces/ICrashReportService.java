package ru.wert.tubus.client.entity.service_interfaces;

import ru.wert.tubus.client.entity.models.CrashReport;
import ru.wert.tubus.client.entity.models.User;
import ru.wert.tubus.client.interfaces.ItemService;

import java.util.List;

public interface ICrashReportService extends ItemService<CrashReport> {

    List<CrashReport> findAllByUser(User user);

    List<CrashReport> findAllByDevice(String device);
}
