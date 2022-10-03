package ru.wert.datapik.client.entity.models;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.winform.enums.EDraftStatus;
//import ru.wert.datapik.winform.enums.EDraftStatus;

import java.util.Objects;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"passport", "draftType", "pageNumber", "status", "statusTime"}, callSuper = false)
public class Draft extends _BaseEntity implements Item, Comparable<Draft> {

    private Passport passport; //ОСНОВНОЕ 1
    private String extension;
    private String initialDraftName;
    private Folder folder;


    private Integer draftType; //ОСНОВНОЕ 2
    private Integer pageNumber; //ОСНОВНОЕ 3

    //Поля хранения информации о текущем статусе и когда он вступил в силу
    private Integer status;
    private User statusUser;
    private String statusTime; //LocalDateTime

    //Поля хранения информации о СОЗДАНИИ
    private User creationUser;
    private String creationTime; //LocalDateTime

    private String note;



    @Override
    public String getName() {
        return passport.getName();
    }

    @Override
    public String toUsefulString() {
        return passport.toUsefulString();
    }

    public String getDecimalNumber(){
        String prefix = passport.getPrefix().getName().equals("-") ? "" : passport.getPrefix().getName() + ".";
        return prefix + passport.getNumber();
    }

    @Override
    public int compareTo(@NotNull Draft o) {
//        return toUsefulString().toLowerCase().compareTo(o.toUsefulString().toLowerCase());

        //Сравниваем номер чертежа, причем 745 должен быть выше, чем 469
        int result = o.getPassport().getNumber()
                .compareTo(getPassport().getNumber()); //745 вверху
        if (result == 0) {
            //Сравниваем тип чертежа
            result = getDraftType() - o.getDraftType(); //Деталь вверху
            if (result == 0) {
                //Сравниваем номер страницы
                result = getPageNumber() - o.getPageNumber(); //1 вверху
                if (result == 0) {
                    //Сравниваем статус id
                    result = getStatus().compareTo(o.getStatus()); // Действует вверху
                    if (result == 0) {
                        //Сравниваем время создания статуса
                        result = o.getStatusTime().compareTo(getStatusTime()); //поздние даты вверху
                    }
                }
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "Draft{" +
                " id=" + id +
                " passport=" + passport +
                ", extension='" + extension + '\'' +
                ", initialDraftName='" + initialDraftName + '\'' +
                ", folder=" + folder +
                ", draftType=" + draftType +
                ", pageNumber=" + pageNumber +
                ", status=" + status +
                ", statusUser=" + statusUser +
                ", statusTime='" + statusTime + '\'' +
                ", creationUser=" + creationUser +
                ", creationTime='" + creationTime + '\'' +
                ", note='" + note + '\'' +
                '}';
    }

}
