package ru.wert.tubus.chogori.components;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class BlinkingImageView extends ImageView {
    private FadeTransition fadeTransition;

    public BlinkingImageView(Image image) {
        super(image);
        setupBlinkingAnimation();
    }

    public BlinkingImageView(Image image, double durationSeconds) {
        super(image);
        setupBlinkingAnimation(durationSeconds);
    }

    private void setupBlinkingAnimation() {
        setupBlinkingAnimation(1.0); // По умолчанию 1 секунда
    }

    private void setupBlinkingAnimation(double durationSeconds) {
        fadeTransition = new FadeTransition(Duration.seconds(durationSeconds), this);
        fadeTransition.setFromValue(1.0); // Полная видимость
        fadeTransition.setToValue(0.0);   // Полная прозрачность
        fadeTransition.setCycleCount(Animation.INDEFINITE); // Бесконечное повторение
        fadeTransition.setAutoReverse(true); // Возврат к начальному состоянию
    }

    public void startBlinking() {
        fadeTransition.play();
    }

    public void stopBlinking() {
        fadeTransition.stop();
        this.setOpacity(1.0); // Возвращаем полную видимость при остановке
    }

    public boolean isBlinking() {
        return fadeTransition.getStatus() == Animation.Status.RUNNING;
    }
}
