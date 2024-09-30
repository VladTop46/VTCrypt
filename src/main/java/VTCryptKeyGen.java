import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.Random;

public class VTCryptKeyGen {

    public static void main(String[] args) {
        long unixTime = System.currentTimeMillis() / 1000L; // Получаем текущее время в секундах (Unix Time)
        BigDecimal unixTimeBD = new BigDecimal(unixTime);
        System.out.println("Unix Time: " + unixTime);

        // Получаем последние 8 цифр Unix-времени
        String lastEightDigits = String
                .valueOf(unixTime)
                .substring(
                        String.valueOf(unixTime).length() - 8
                );
        System.out.println("Последние 8 цифр: " + lastEightDigits);

        // Разделяем на пары чисел
        int[] pairs = new int[4];
        pairs[0] = Integer.parseInt(
                lastEightDigits.substring(0, 2)
        ); // Первая пара
        pairs[1] = Integer.parseInt(
                lastEightDigits.substring(2, 4)
        ); // Вторая пара
        pairs[2] = Integer.parseInt(
                lastEightDigits.substring(4, 6)
        ); // Третья пара
        pairs[3] = Integer.parseInt(
                lastEightDigits.substring(6, 8)
        ); // Четвёртая пара

        // Переворачиваем пары чисел
        for (int i = 0; i < pairs.length; i++) {
            pairs[i] = reverseNumber(pairs[i]);
        }

        // Сортируем пары от большего к меньшему
        java.util.Arrays.sort(pairs);
        reverseArray(pairs); // Сортируем в порядке убывания

        // Проверка на нули в парах, заменяем на 1 при необходимости
        for (int i = 0; i < pairs.length; i++) {
            if (pairs[i] == 0) {
                pairs[i] = 1; // Замена нуля на 1
            }
        }

        // Считаем логарифмы с максимальной точностью
        BigDecimal logResult1 = new BigDecimal(
                log(pairs[1], pairs[0])
                        .toPlainString()
                        .replace(".", "")
        );
        BigDecimal logResult2 = new BigDecimal(
                log(pairs[3], pairs[2])
                        .toPlainString()
                        .replace(".", "")
        );

        // Получаем последние три цифры Unix-времени для расчёта ключа
        int lastThreeDigits = Integer.parseInt(
                String.valueOf(unixTime).substring(
                        String.valueOf(unixTime).length() - 3
                )
        );

        // Первая и вторая части ключа
        BigDecimal part1 = BigDecimal.valueOf(
                lastThreeDigits)
                .divide(
                        logResult1,
                        MathContext.DECIMAL128
                );
        BigDecimal part2 = BigDecimal.valueOf(
                lastThreeDigits)
                .divide(
                        logResult2,
                        MathContext.DECIMAL128
                );

        // Убираем точки и получаем числа без точки
        BigDecimal part1noDot = new BigDecimal(
                part1
                        .toPlainString()
                        .replace(".", ""),
                MathContext.DECIMAL128
        );
        BigDecimal part2noDot = new BigDecimal(
                part2
                        .toPlainString()
                        .replace(".", ""),
                MathContext.DECIMAL128
        );

        // Генерация числа для ключа
        String temp = part1noDot.multiply(part2noDot, MathContext.UNLIMITED)
                .multiply(new BigDecimal(part1noDot.add(part2noDot)
                                .multiply(unixTimeBD.multiply(unixTimeBD)).toString() + unixTime),
                        MathContext.UNLIMITED)
                .toPlainString();

        // Убираем точки и перемешиваем строку для случайности
        temp = temp.replace(".", "");
        String finalKey = temp.length() >= 256 ? temp.substring(0, 256) : temp;

        // Если длина меньше 256 символов, добавляем случайные цифры, основанные на Unix-времени
        if (finalKey.length() < 256) {
            finalKey = addRandomDigits(finalKey, 256, unixTime);
        }

        System.out.println("Итоговый 256-символьный ключ: " + finalKey);
    }

    // Метод для добавления случайных цифр
    private static String addRandomDigits(String base, int length, long seed) {
        Random random = new SecureRandom();
        random.setSeed(seed); // Используем Unix-время как источник случайности

        StringBuilder sb = new StringBuilder(base);
        while (sb.length() < length) {
            sb.append(random.nextInt(10)); // Добавляем случайную цифру (от 0 до 9)
        }
        return sb.toString();
    }

    // Метод для переворота числа
    private static int reverseNumber(int number) {
        return Integer.parseInt(
                new StringBuilder(
                        String.valueOf(number)
                )
                        .reverse()
                        .toString()
        );
    }

    // Метод для сортировки массива в обратном порядке
    private static void reverseArray(int[] array) {
        for (int i = 0; i < array.length / 2; i++) {
            int temp = array[i];
            array[i] = array[array.length - 1 - i];
            array[array.length - 1 - i] = temp;
        }
    }

    // Метод для вычисления логарифма числа по нестандартному основанию с максимальной точностью
    private static BigDecimal log(int a, int base) {
        MathContext mc = new MathContext(
                200,
                RoundingMode.HALF_UP
        );
        return BigDecimal
                .valueOf(
                        Math.log(a)
                )
                .divide(
                        BigDecimal.valueOf(
                                Math.log(base)
                        ), mc
                );
    }
}
