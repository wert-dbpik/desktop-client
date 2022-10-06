package ru.wert.datapik.chogori.remarks.commands;

import javafx.event.ActionEvent;
import javafx.event.Event;
import ru.wert.datapik.client.entity.models.Remark;
import ru.wert.datapik.chogori.common.commands.ICommand;
import ru.wert.datapik.chogori.common.commands.ItemCommands;
import ru.wert.datapik.chogori.remarks.Remark_TableView;
import ru.wert.datapik.chogori.statics.AppStatic;


import java.util.List;

import static ru.wert.datapik.chogori.application.services.ChogoriServices.CH_REMARKS;
import static ru.wert.datapik.chogori.setteings.ChogoriSettings.CH_CURRENT_USER;

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

    public void pushRemarkUp(ActionEvent actionEvent) {
        Remark selectedRemark = tableView.getSelectionModel().getSelectedItem();
        if(selectedRemark == null) return;
        selectedRemark.setUser(CH_CURRENT_USER);
        selectedRemark.setCreationTime(AppStatic.getCurrentTime());
        CH_REMARKS.update(selectedRemark);
        tableView.updateTableView();
    }
}
