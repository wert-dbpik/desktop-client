package ru.wert.tubus.chogori.statics.validators;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class NameValidatorTest {
    @Test
    void testCreateValidName_StartsWithLowerCase() {
        NameValidator validator = new NameValidator();
        String result = validator.createValidName("новая деталь");
        assertEquals("Новая деталь", result, "Имя должно начинаться с заглавной буквы");
    }

    @Test
    void testCreateValidName_StartsWithUpperCase() {
        NameValidator validator = new NameValidator();
        String result = validator.createValidName("Новая деталь");
        assertEquals("Новая деталь", result, "Имя уже начинается с заглавной буквы, изменений не требуется");
    }

    @Test
    void testCreateValidName_StartsWithNonLetter() {
        NameValidator validator = new NameValidator();
        String result = validator.createValidName("- новая деталь");
        assertEquals("Новая деталь", result, "Первый буквенный символ должен быть заглавным");
    }

    @Test
    void testCreateValidName_OnlySpaces() {
        NameValidator validator = new NameValidator();
        String result = validator.createValidName("   ");
        assertEquals("   ", result, "Если строка состоит только из пробелов, она должна возвращаться без изменений");
    }

    @Test
    void testCreateValidName_EmptyString() {
        NameValidator validator = new NameValidator();
        String result = validator.createValidName("");
        assertEquals("", result, "Пустая строка должна возвращаться без изменений");
    }

    @Test
    void testCreateValidName_NullInput() {
        NameValidator validator = new NameValidator();
        String result = validator.createValidName(null);
        assertNull(result, "Если входная строка null, метод должен вернуть null");
    }

    @Test
    void testCreateValidName_NoLetters() {
        NameValidator validator = new NameValidator();
        String result = validator.createValidName("123!@#");
        assertEquals("123!@#", result, "Если в строке нет букв, она должна возвращаться без изменений");
    }

    @Test
    void testCreateValidName_LeadingSpaces() {
        NameValidator validator = new NameValidator();
        String result = validator.createValidName("   новая деталь");
        assertEquals("Новая деталь", result, "Первый буквенный символ после пробелов должен быть заглавным");
    }
}
