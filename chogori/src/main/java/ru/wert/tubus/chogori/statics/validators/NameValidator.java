package ru.wert.tubus.chogori.statics.validators;

public class NameValidator {
    /**
     * Метод получает строковое значение поля с именем и проверяет его на соответствие нормальному имени.
     * Имя должно начинаться с заглавной буквы. Если имя не начинается с заглавной буквы, оно корректируется.
     * Если имя пустое или не содержит букв, возвращается исходная строка.
     *
     * @param initName Исходное имя.
     * @return Корректное имя, начинающееся с заглавной буквы, или исходная строка, если имя не содержит букв.
     */
    public static String createValidName(String initName) {
        if (initName == null || initName.isEmpty()) {
            return initName; // Возвращаем null или пустую строку, если имя пустое
        }

        if (Character.isUpperCase(initName.charAt(0))) {
            return initName; // Если имя уже начинается с заглавной буквы, возвращаем его
        }

        int position = findFirstLetterPosition(initName);
        if (position == -1) {
            return initName; // Если буквенный символ не найден, возвращаем исходную строку
        }

        // Проверяем, начинается ли имя с заглавной буквы
        char firstLetter = initName.charAt(position);

        // Если имя начинается с маленькой буквы, делаем первую букву заглавной
        char capitalizedChar = Character.toUpperCase(firstLetter);
        return capitalizedChar // Заглавный символ
                + initName.substring(position + 1); // Часть строки после позиции
    }

    /**
     * Метод находит позицию первого буквенного символа в строке.
     *
     * @param str Строка для поиска.
     * @return Позиция первого буквенного символа или -1, если символ не найден.
     */
    private static int findFirstLetterPosition(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (Character.isLetter(str.charAt(i))) {
                return i;
            }
        }
        return -1; // Если буквенный символ не найден
    }
}
