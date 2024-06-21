package ru.wert.tubus.chogori.search;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;

public class SearchHistoryFile {

    final String homeDir = System.getProperty("user.home") + File.separator + "AppData" + File.separator + "Local" + File.separator + "Tubus";
    final String searchHistoryFilePath = homeDir + File.separator + "search-history.txt";
    File searchHistoryFile = new File(searchHistoryFilePath);

    static SearchHistoryFile instance;

    public static SearchHistoryFile getInstance() {
        if (instance == null)
            instance = new SearchHistoryFile();
        return instance;
    }

    /**
     * Сохраняет историю поиска в файл
     */
    public void save(){
        saveToFile(createStringToSave());
    }

    /**
     * Очищает историю поиска
     */
    public void clear(){
        saveToFile("");
        SearchHistoryListView.getInstance().getItems().clear();
    }

    //================================================================================================

    /**
     * Создает строку для последующего сохранения в файле
     * @return String
     */
    private String createStringToSave(){
        StringBuilder sb = new StringBuilder();
        ObservableList<String> history = SearchHistoryListView.getInstance().getItems();
        for(String str : history)
            sb.append(str).append("\n");
        return sb.toString();
    }

    /**
     * Загружает данные с элементами поиска в файл
     * @param searchHistoryItems String , элементы истории каждый в отдельной строке
     */
    public void saveToFile(String searchHistoryItems){
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(searchHistoryFilePath));
            writer.write(searchHistoryItems);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Читает файл и формирует из него список из элементов истории поиска
     * @return ObservableList<String> список элементов поиска
     */
    public ObservableList<String> read(){
        ObservableList<String> history = FXCollections.observableArrayList();
        try {
            BufferedReader br = new BufferedReader(new FileReader(searchHistoryFile));
            String st;
            while ((st = br.readLine()) != null)
                history.add(st);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return history;
    }



}
