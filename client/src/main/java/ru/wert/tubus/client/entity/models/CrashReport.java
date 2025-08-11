package ru.wert.tubus.client.entity.models;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.wert.tubus.client.interfaces.Item;
import ru.wert.tubus.client.entity.models._BaseEntity;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CrashReport extends _BaseEntity implements Item {

    private LocalDateTime date;
    private User user;
    private String device; // "mobile" или "desktop"
    private String version;
    private String stackTrace;
    private String report;

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String toUsefulString() {
        return user + ": " + device + " v" + version + " - " +
                (stackTrace != null ? stackTrace.substring(0, Math.min(stackTrace.length(), 50)) + "..." : "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CrashReport)) return false;
        CrashReport that = (CrashReport) o;
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}