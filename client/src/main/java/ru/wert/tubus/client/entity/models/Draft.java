package ru.wert.tubus.client.entity.models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.sun.org.apache.xerces.internal.parsers.CachingParserPool;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import ru.wert.tubus.client.interfaces.Item;
import ru.wert.tubus.winform.enums.EDraftStatus;
import ru.wert.tubus.winform.enums.EDraftType;
//import EDraftStatus;

import java.util.Objects;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@EqualsAndHashCode(of = {"passport", "draftType", "pageNumber", "status", "statusTime"}, callSuper = false)
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
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private String statusTime; //LocalDateTime

    //Поля хранения информации о СОЗДАНИИ
    private User creationUser;
    private String creationTime; //LocalDateTime

    private String note;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Draft draft = (Draft) o;

       if (getPassport().equals(draft.getPassport()) &&
                getDraftType().equals(draft.getDraftType()) &&
                getPageNumber().equals(draft.getPageNumber()) &&
                getStatus().equals(draft.getStatus())){
           return getStatus().equals(EDraftStatus.LEGAL.getStatusId());
       }
       return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPassport(), getDraftType(), getPageNumber(), getStatus(), getStatusTime());
    }

    @Override
    public String getName() {
        return passport.getName();
    }

    @Override
    public String toUsefulString() {
        return passport.toUsefulString();
    }

    /**
     * Если чертеж DXF - ПИК.745222.256-02 Имя
     * Если чертеж обычный - ПИК.745222.256 СП Имя
     */
    public String toFullName() {
        StringBuilder sb = new StringBuilder();
        sb.append(passport.getPrefix().getName()).append(".").append(getDecimalNumber());
        if (draftType == EDraftType.IMAGE_DXF.ordinal() && pageNumber > 0)
            sb.append("-").append(pageNumber);
        else
            sb.append(" ").append(EDraftType.getDraftTypeById(draftType).getShortName());
        sb.append(" ").append(getName());

        return sb.toString();
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
