package ru.wert.datapik.utils.common.tableView;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.client.interfaces.ItemService;
import ru.wert.datapik.utils.common.commands.ItemCommands;
import ru.wert.datapik.utils.common.contextMenuACC.FormView_ContextMenu;

import java.util.List;

public abstract class RoutineTableView<P extends Item> extends ItemTableView<P> {

    protected FormView_ContextMenu<P> contextMenu;

    public abstract void setTableColumns();
    public abstract ItemCommands<P> getCommands();
    public abstract String getAccWindowRes();
    public abstract void setModifyingItem(Object item);

    public RoutineTableView(String promptText) {
        super(promptText);
    }

    /**
     * Обновляет данные формы
     */
    public void updateTableView() {
        updateRoutineTableView();
    }

    /**
     * Обновляет данные формы
     */
    public void updateRoutineTableView() {
        TaskUpdateItemsInRoutineTableView<P> taskUpdate = new TaskUpdateItemsInRoutineTableView<>(this);
        Thread t = new Thread(taskUpdate);
        t.setDaemon(true);
        t.start();
    }
}
