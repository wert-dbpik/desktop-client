package ru.wert.datapik.chogori.calculator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OpMechanicalTest {

    static OpMechanical test;

    @BeforeAll
    static void beforeAll() {
        test = new OpMechanical(1.5, 200, 300, CalcConstants.TimeMeasure.MIN);
    }

    @Test
    void countMass() {
        double res = test.countMass();
        assertEquals(0.7065, res, 0.01);
    }

    @Test
    void countCuttingTime() {
        double res = test.countCuttingTime(0, 5, 0, true);
        assertEquals(0.8103, res, 0.01);
    }

    @Test
    void countBendingTime() {
        double res = test.countBendingTime(3, 2, 1);
        assertEquals(1.125, res, 0.01);
    }

    @Test
    void countPaintingTime() {
        double res = test.countPaintingTime(100, 100, 20, 1.4);
        assertEquals(1.8006, res, 0.01);
    }
}