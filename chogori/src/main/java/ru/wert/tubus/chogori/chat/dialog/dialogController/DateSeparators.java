package ru.wert.tubus.chogori.chat.dialog.dialogController;

import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.client.entity.models.Message;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
public class DateSeparators {

    /**
     * Перебирает список сообщений и вставляет сепараторы при смене даты в сообщениях.
     * Если сегодняшняя дата больше, чем дата последнего сообщения, добавляет сепаратор с текущей датой в конец списка.
     * Если список пуст, добавляет только сепаратор с текущей датой.
     *
     * @param messages исходная коллекция сообщений
     * @return список сообщений с добавленными сепараторами
     */
    public List<Message> insertDateSeparators(List<Message> messages) {
        // Если список сообщений пуст, добавляем только сепаратор с текущей датой
        if (messages == null || messages.isEmpty()) {
            List<Message> result = new ArrayList<>();
            result.add(getSeparatorMessage(LocalDateTime.now())); // Добавляем сепаратор с текущей датой
            log.debug("Добавлен сепаратор с текущей датой, так как список сообщений пуст");
            return result;
        }

        // Сортируем сообщения по времени создания
        messages.sort(Comparator.comparing(Message::getCreationTime));

        // Создаем временный список для хранения сообщений с разделителями
        List<Message> messagesWithSeparators = new ArrayList<>();

        // Переменная для хранения последней даты
        LocalDateTime lastDate = null;
        for (Message message : messages) {
            LocalDateTime currentDate = message.getCreationTime().toLocalDate().atStartOfDay();

            // Если дата сообщения отличается от последней даты, добавляем разделитель
            if (lastDate == null || !currentDate.isEqual(lastDate)) {
                // Создаем разделитель с датой
                Message separator = getSeparatorMessage(currentDate);

                // Добавляем разделитель в список
                messagesWithSeparators.add(separator);
                lastDate = currentDate; // Обновляем последнюю дату
                log.debug("Добавлен сепаратор с датой: {}", lastDate);
            }

            // Добавляем текущее сообщение в список
            messagesWithSeparators.add(message);
        }

        // Получаем текущую дату
        LocalDateTime today = LocalDateTime.now().toLocalDate().atStartOfDay();

        // Получаем дату последнего сообщения
        LocalDateTime lastMessageDate = messages.get(messages.size() - 1).getCreationTime().toLocalDate().atStartOfDay();

        // Если сегодняшняя дата больше, чем дата последнего сообщения, добавляем сепаратор с текущей датой
        if (today.isAfter(lastMessageDate)) {
            Message separator = getSeparatorMessage(LocalDateTime.now());
            messagesWithSeparators.add(separator);
            log.debug("Добавлен сепаратор с текущей датой: {}", today);
        }

        return messagesWithSeparators;
    }

    /**
     * Создает сообщение-сепаратор с указанной датой.
     * Дата форматируется в формате "dd.MM.yyyy".
     *
     * @param dateTime дата и время для сепаратора
     * @return сообщение-сепаратор
     */
    private Message getSeparatorMessage(LocalDateTime dateTime) {
        Message separator = new Message();
        separator.setType(Message.MessageType.CHAT_SEPARATOR);
        separator.setCreationTime(dateTime);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String formattedDate = dateTime.format(formatter);

        separator.setText(formattedDate); // Устанавливаем текст сепаратора как строку с датой
        return separator;
    }
}
