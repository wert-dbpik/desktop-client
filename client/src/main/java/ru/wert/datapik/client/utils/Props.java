package ru.wert.datapik.client.utils;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

@Getter
@Setter
public class Props {

    // -- Сохраняемые параметры --
    private Long currentUserId;

    //----------------------------
    private static Props instance;
    private FileInputStream fis;
    private FileOutputStream fos;
    private Properties props = new Properties();
    private JDialog owner;
    private static final String  appConfigPath = "client/src/main/resources/props/config.properties";



    // -------------------    Конструктор     ----------------------
    private Props(){
    }

    public static Props getInstance(){
        if(instance == null){
            instance = new Props();
        }
        return instance;
    }

    //--------------------    Чтение конфигурации   -------------------
    public void getParams(){
        try {
            fis = new FileInputStream(appConfigPath);
            props.load(fis);

            //Get String parameter from  the file .properties
            String currentUserId = props.getProperty("CURRENT_USER_ID");
            fis.close();

            //Pars string parameter we got above to the proper type
            this.currentUserId = (Long.parseLong(currentUserId));

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Не могу найти файл свойств \n"
                    + appConfigPath, "", JOptionPane.ERROR_MESSAGE);
        }
    }

    //-------------------   Сохраниение конфигурации     ------------
    public void setParams(){
        try{
            fos = new FileOutputStream(appConfigPath);

            props.setProperty("CURRENT_USER_ID", Long.toString(currentUserId));

            props.store(fos, null);

            fos.close();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Не могу найти файл свойств "
                    + appConfigPath, "", JOptionPane.ERROR_MESSAGE);
        }
    }


}
