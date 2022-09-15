package ru.wert.datapik.utils.remarks.commands;

import javafx.event.Event;
import ru.wert.datapik.client.entity.models.Remark;
import ru.wert.datapik.utils.common.commands.ICommand;
import ru.wert.datapik.utils.common.commands.ItemCommands;
import ru.wert.datapik.utils.remarks.Remark_TableView;


import java.util.List;

public class _RemarkCommands implements ItemCommands<Remark> {

    private final Remark_TableView tableView;
    private String accWindowRes;

    public _RemarkCommands(Remark_TableView tableView) {
        this.tableView = tableView;
    }

    @Override
    public void add(Event event, Remark newItem){
        ICommand command = new RemarkAddCommand((Remark)newItem, tableView);
        command.execute();
    }

    @Override
    public void copy(Event event){

    }

    @Override
    public void delete(Event event, List<Remark> items){
        ICommand command = new RemarkDeleteCommand(items, tableView);
        command.execute();
    }

    @Override
    public void change(Event event, Remark item){
        ICommand command = new RemarkChangeCommand(item, tableView);
        command.execute();
    }

}
