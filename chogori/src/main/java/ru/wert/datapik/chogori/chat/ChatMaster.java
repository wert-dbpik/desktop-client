package ru.wert.datapik.chogori.chat;

import ru.wert.datapik.client.entity.models.User;

import java.util.Arrays;

import static ru.wert.datapik.chogori.application.services.ChogoriServices.CH_USERS;
import static ru.wert.datapik.chogori.setteings.ChogoriSettings.CH_CURRENT_USER;

public class ChatMaster {

    public static String getRoomName(String roomNameDB){
        String finalName = "";
        if(roomNameDB.startsWith("one-to-one:")){
            roomNameDB = roomNameDB.replace("one-to-one:#", "");
            String[] usersId = roomNameDB.split("#", -1);
            for(String id : usersId){
                User user = CH_USERS.findById(Long.parseLong(id));
                if(!user.getId().equals(CH_CURRENT_USER.getId())) {
                    finalName = user.getName();
                    break;
                }
            }
        } else
            finalName = roomNameDB;

        return finalName;

    }
}
