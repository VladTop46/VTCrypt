package ru.vladtop46.vtcrypt;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.Random;

public class VTCryptKeyGen {
    private static final int KEY_LENGTH = 256;

    /**
     * Генерирует ключ на основе текущего времени
     * @return 256-символьный ключ
     */
    public String generateKey() {
        return generateKeyFromSeed(System.currentTimeMillis() / 1000L);
    }

    /**
     * Генерирует ключ на основе переданного seed-значения
     * @param seed значение для генерации ключа
     * @return 256-символьный ключ
     */
    public String generateKeyFromSeed(long seed) {
        BigDecimal seedBD = new BigDecimal(seed);

        // Получаем последние 8 цифр seed
        String lastEightDigits = String.valueOf(seed)
                .substring(String.valueOf(seed).length() - 8);

        // Разделяем на пары чисел
        int[] pairs = new int[4];
        for(int i = 0; i < 4; i++) {
            pairs[i] = Integer.parseInt(lastEightDigits.substring(i * 2, (i * 2) + 2));
        }

        // Переворачиваем пары чисел
        for (int i = 0; i < pairs.length; i++) {
            pairs[i] = reverseNumber(pairs[i]);
        }

        // Сортируем пары от большего к меньшему
        java.util.Arrays.sort(pairs);
        reverseArray(pairs);

        // Проверка на нули
        for (int i = 0; i < pairs.length; i++) {
            if (pairs[i] == 0) pairs[i] = 1;
        }

        // Вычисляем логарифмы
        BigDecimal logResult1 = new BigDecimal(
                log(pairs[1], pairs[0]).toPlainString().replace(".", ""));
        BigDecimal logResult2 = new BigDecimal(
                log(pairs[3], pairs[2]).toPlainString().replace(".", ""));

        // Последние три цифры seed для расчёта
        int lastThreeDigits = Integer.parseInt(
                String.valueOf(seed).substring(String.valueOf(seed).length() - 3));

        // Вычисляем части ключа
        BigDecimal part1 = BigDecimal.valueOf(lastThreeDigits)
                .divide(logResult1, MathContext.DECIMAL128);
        BigDecimal part2 = BigDecimal.valueOf(lastThreeDigits)
                .divide(logResult2, MathContext.DECIMAL128);

        // Убираем точки
        BigDecimal part1noDot = new BigDecimal(
                part1.toPlainString().replace(".", ""),
                MathContext.DECIMAL128);
        BigDecimal part2noDot = new BigDecimal(
                part2.toPlainString().replace(".", ""),
                MathContext.DECIMAL128);

        // Генерация финального числа
        String temp = part1noDot.multiply(part2noDot, MathContext.UNLIMITED)
                .multiply(new BigDecimal(
                                part1noDot.add(part2noDot)
                                        .multiply(seedBD.multiply(seedBD))
                                        .toString() + seed),
                        MathContext.UNLIMITED)
                .toPlainString()
                .replace(".", "");

        String finalKey = temp.length() >= KEY_LENGTH ?
                temp.substring(0, KEY_LENGTH) :
                addRandomDigits(temp, KEY_LENGTH, seed);

        return finalKey;
    }

    private static String addRandomDigits(String base, int length, long seed) {
        Random random = new SecureRandom();
        random.setSeed(seed);

        StringBuilder sb = new StringBuilder(base);
        while (sb.length() < length) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private static int reverseNumber(int number) {
        return Integer.parseInt(
                new StringBuilder(String.valueOf(number))
                        .reverse()
                        .toString()
        );
    }

    private static void reverseArray(int[] array) {
        for (int i = 0; i < array.length / 2; i++) {
            int temp = array[i];
            array[i] = array[array.length - 1 - i];
            array[array.length - 1 - i] = temp;
        }
    }

    private static BigDecimal log(int a, int base) {
        MathContext mc = new MathContext(200, RoundingMode.HALF_UP);
        return BigDecimal.valueOf(Math.log(a))
                .divide(BigDecimal.valueOf(Math.log(base)), mc);
    }
}