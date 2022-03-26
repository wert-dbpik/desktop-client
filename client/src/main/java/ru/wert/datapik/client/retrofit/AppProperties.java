package ru.wert.datapik.client.retrofit;

import javafx.scene.control.Dialog;
import ru.wert.datapik.winform.warnings.Warning1;

import java.io.*;
import java.util.Properties;

public class AppProperties {

    static AppProperties instance;

    public static AppProperties getInstance(){
        if(instance == null)
            return new AppProperties();
        else
            return instance;
    }

    private int attempt = 0;
    private Properties connectionProps;
    private String appConfigPath = "connectionSettings.properties";

    /**
     * Конструктор
     * Загружаем имеющиеся настройки доступа к серверу из файла connectionSettings.properties
     * если файла не существует, то он создается и в файл записываются данные по умолчанию
     */
    private AppProperties() {
//        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
//        String rootPath = this.getClass().getClassLoader().getResource("").getPath();

        File propsFile = new File(appConfigPath);
        if (!propsFile.exists())
            createFileOfConnectionSettings(appConfigPath);
        else {
            try {
                connectionProps = new Properties();
                connectionProps.load(new FileInputStream(appConfigPath));
            } catch (IOException e) {
                Warning1.create("Ошибка!",
                        "Не удалось загрузить настройки доступа к серверу",
                        "Возможно, файл настроек поврежден");
                e.printStackTrace();
            }
        }
    }

    /**
     * Создаем файл connectionSettings.properties, если он отсутствует каталоге программы
     * @param appConfigPath String, путь к файлу connectionSettings.properties
     */
    private void createFileOfConnectionSettings(String appConfigPath) {
        try {
            File props = new File(appConfigPath);
            props.createNewFile();
            FileWriter writer = new FileWriter (props);
            writer.write("IP_ADDRESS=192.168.1.83\n");
            writer.write("PORT = 8080");
            writer.write("MONITOR=0");
            writer.close();
        } catch (IOException e) {
            if(++attempt < 3) new AppProperties();
            else{

                Warning1.create("Ошибка!",
                        "Не удалось создать файл настроек доступа к серверу",
                        "Возможно, стоит защита от создания файлов,\n обратитесь к разработчику");
                e.printStackTrace();
            }

        }
    }

    public String getIpAddress(){
        return connectionProps.getProperty("IP_ADDRESS");
    }

    public String getPort(){
        return connectionProps.getProperty("PORT");
    }

    public int getMonitor(){
        return Integer.parseInt(connectionProps.getProperty("MONITOR"));
    }

    public void setIpAddress(final String ipAddress){
        try {
            FileOutputStream fos = new FileOutputStream(appConfigPath);
            connectionProps.setProperty("IP_ADDRESS", ipAddress);
            connectionProps.store(fos, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setPort(final String port){
        try {
            FileOutputStream fos = new FileOutputStream(appConfigPath);
            connectionProps.setProperty("PORT", port);
            connectionProps.store(fos, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setMonitor(final int monitor){
        try {
            FileOutputStream fos = new FileOutputStream(appConfigPath);
            connectionProps.setProperty("MONITOR", String.valueOf(monitor));
            connectionProps.store(fos, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
