package ru.wert.tubus.chogori.pdf;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Класс глобально блокирует любые диалоги, исходящие из библиотеки ICEpdf
 */
public class ICEpdfGlobalDialogBlocker {
    static {
        blockAllDialogs();
    }

    public static void blockAllDialogs() {
        // Блокируем все модальные диалоги
        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
            @Override
            public void eventDispatched(AWTEvent event) {
                if (event instanceof WindowEvent) {
                    WindowEvent we = (WindowEvent) event;
                    if (we.getID() == WindowEvent.WINDOW_OPENED) {
                        Window window = we.getWindow();
                        if (window instanceof JDialog) {
                            JDialog dialog = (JDialog) window;
                            // Закрываем все диалоги от ICEpdf
                            if (isIcePdfDialog(dialog)) {
                                SwingUtilities.invokeLater(() -> dialog.dispose());
                            }
                        }
                    }
                }
            }

            private boolean isIcePdfDialog(JDialog dialog) {
                String title = dialog.getTitle();
                Component[] components = dialog.getContentPane().getComponents();

                // Проверяем различные признаки ICEpdf диалогов
                return (title != null && title.contains("ICEpdf")) ||
                        containsIcePdfComponents(components);
            }

            private boolean containsIcePdfComponents(Component[] components) {
                for (Component comp : components) {
                    if (comp instanceof JLabel) {
                        String text = ((JLabel) comp).getText();
                        if (text != null && text.contains("ICEpdf")) {
                            return true;
                        }
                    }
                }
                return false;
            }
        }, AWTEvent.WINDOW_EVENT_MASK);
    }
}
