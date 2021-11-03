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
//@EqualsAndHashCode(of = {"passport", "draftType", "pageNumber", "status", "statusTime"}, callSuper = false)
public class Draft extends _BaseEntity implements Item, Comparable<Draft> {

    private Passport passport;
    private String extension;
    private String initialDraftName;
    private Folder folder;

    private Integer draftType;
    private Integer pageNumber;

    private Integer status;
    private User statusUser;
    private String statusTime; //LocalDateTime

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

    @Override
    public int compareTo(@NotNull Draft o) {
//        return toUsefulString().toLowerCase().compareTo(o.toUsefulString().toLowerCase());

        //Сравниваем номер чертежа, причем 745 должен быть выше, чем 469
        int result = o.getPassport().getNumber()
                .compareTo(getPassport().getNumber());
        if (result == 0) {
            //Сравниваем тип чертежа
            result = getDraftType() - o.getDraftType();
            if (result == 0) {
                //Сравниваем номер страницы
                result = getPageNumber() - o.getPageNumber();
                if (result == 0) {
                    //Сравниваем статус id
                    result = getStatus().compareTo(o.getStatus());
                    if (result == 0) {
                        //Сравниваем время создания статуса
                        result = getStatusTime().compareTo(o.getStatusTime());
                    }
                }
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "Draft{" +
                "passport=" + passport +
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Draft)) return false;
        Draft draft = (Draft) o;
        //Не должно быть двух чертежей со статусом ДЕЙСТВУЕТ
        if (getStatus().equals(EDraftStatus.LEGAL.getStatusId())) {
            return getPassport().equals(draft.getPassport()) &&
                    getDraftType().equals(draft.getDraftType()) &&
                    getPageNumber().equals(draft.getPageNumber()) &&
                    getStatus().equals(draft.getStatus());
        }
        //Иначе статус чертежа ЗАМЕНЕН или АННУЛИРОВАН, а дата замены или аннулирования
        //исключает их тождественность
        return getPassport().equals(draft.getPassport()) &&
                getDraftType().equals(draft.getDraftType()) &&
                getPageNumber().equals(draft.getPageNumber()) &&
                getStatus().equals(draft.getStatus()) &&
                getStatusTime().equals(draft.getStatusTime());

    }

    @Override
    public int hashCode() {
        return Objects.hash(getPassport(), getDraftType(), getPageNumber(), getStatus(), getStatusTime());
    }
}
