package ru.wert.datapik.chogori.excel.table.commands;

public abstract class EditorCommand {

    public abstract String getDescription();

    public abstract void execute();

    public abstract void undo();
}
