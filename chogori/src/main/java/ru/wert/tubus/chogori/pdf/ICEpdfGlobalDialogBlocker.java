package ru.wert.tubus.chogori.pdf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.*;

    public class ICEpdfGlobalDialogBlocker {
        static {
            blockAllDialogs();
        }

        public static void blockAllDialogs() {
            try {
                // Способ 1: Подмена JOptionPane
                replaceJOptionPane();

                // Способ 2: Блокировка на уровне Window
                blockWindowCreation();

                // Способ 3: Перехват событий на более низком уровне
                interceptAWT();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private static void replaceJOptionPane() {
            // Используем рефлексию для подмены JOptionPane
            try {
                Method showMethod = JOptionPane.class.getDeclaredMethod(
                        "showOptionDialog", Component.class, Object.class,
                        String.class, int.class, int.class, Icon.class,
                        Object[].class, Object.class);

                Method showMessageMethod = JOptionPane.class.getDeclaredMethod(
                        "showMessageDialog", Component.class, Object.class,
                        String.class, int.class);

                // Устанавливаем новые обработчики
                setAccessible(showMethod);
                setAccessible(showMessageMethod);

            } catch (Exception e) {
                // Если рефлексия не работает, используем другой подход
            }
        }

        private static void blockWindowCreation() {
            // Блокируем создание любых диалогов
            Dialog.ModalityType[] types = Dialog.ModalityType.values();
            for (Dialog.ModalityType type : types) {
                try {
                    Field field = Dialog.class.getDeclaredField("DEFAULT_MODALITY_TYPE");
                    setAccessible(field);
                    field.set(null, Dialog.ModalityType.MODELESS);
                } catch (Exception e) {
                    // Игнорируем
                }
            }
        }

        private static void interceptAWT() {
            Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
                public void eventDispatched(AWTEvent event) {
                    if (event instanceof WindowEvent) {
                        WindowEvent we = (WindowEvent) event;

                        // Перехватываем ДО открытия окна
                        if (we.getID() == WindowEvent.WINDOW_OPENED) {
                            Window window = we.getWindow();
                            if (shouldBlock(window)) {
                                System.out.println("Blocking window: " + window.getClass().getName());
                                window.dispose();

                                // Дополнительно прерываем поток
                                if (window.isVisible()) {
                                    window.setVisible(false);
                                }
                            }
                        }

                        // Перехватываем попытки сделать видимым
                        if (we.getID() == WindowEvent.WINDOW_GAINED_FOCUS) {
                            Window window = we.getWindow();
                            if (shouldBlock(window)) {
                                window.setVisible(false);
                            }
                        }
                    }
                }
            }, AWTEvent.WINDOW_EVENT_MASK | AWTEvent.WINDOW_FOCUS_EVENT_MASK);
        }

        private static boolean shouldBlock(Window window) {
            if (window == null) return false;

            String name = window.getClass().getName();
            String title = window instanceof Dialog ? ((Dialog)window).getTitle() : "";

            // Блокируем все диалоги и окна связанные с ICEpdf
            return name.contains("icepdf") ||
                    name.contains("ICEpdf") ||
                    title.contains("ICEpdf") ||
                    title.contains("Error") ||
                    title.contains("Exception") ||
                    window instanceof JDialog;
        }

        private static void setAccessible(AccessibleObject object) {
            object.setAccessible(true);
        }
    }