package ru.vladtop46.vtcrypt;

import java.security.SecureRandom;
import java.util.Arrays;

public class VTCryptCipher {
    private static final int BLOCK_SIZE = 16;
    private static final int IV_SIZE = 16;
    private final int[] keyValues;
    private boolean debug = false;

    public VTCryptCipher(String key) {
        if (key.length() != 256) {
            throw new IllegalArgumentException("Key must be 256 characters long");
        }

        // Преобразуем ключ в массив значений от 1 до 15
        keyValues = new int[16];
        for (int i = 0; i < 16; i++) {
            keyValues[i] = (sumDigits(key.substring(i * 16, (i + 1) * 16)) % 15) + 1;
        }
    }

    public byte[] encrypt(byte[] data) {
        if (debug) System.out.println("Encrypting data of length: " + data.length);

        // Вычисляем padding
        int paddingSize = BLOCK_SIZE - (data.length % BLOCK_SIZE);
        int totalSize = IV_SIZE + data.length + paddingSize;

        if (debug) {
            System.out.println("Padding size: " + paddingSize);
            System.out.println("Total size with IV and padding: " + totalSize);
        }

        // Подготавливаем результат
        byte[] result = new byte[totalSize];
        byte[] iv = generateIV();
        System.arraycopy(iv, 0, result, 0, IV_SIZE);
        System.arraycopy(data, 0, result, IV_SIZE, data.length);

        // Добавляем padding
        Arrays.fill(result, IV_SIZE + data.length, result.length, (byte)paddingSize);

        if (debug) {
            System.out.println("Before encryption:");
            System.out.println("IV: " + Arrays.toString(iv));
            System.out.println("Data with padding: " +
                    Arrays.toString(Arrays.copyOfRange(result, IV_SIZE, result.length)));
        }

        // Шифруем блоки
        byte[] previousBlock = iv;
        for (int i = IV_SIZE; i < result.length; i += BLOCK_SIZE) {
            byte[] block = Arrays.copyOfRange(result, i, i + BLOCK_SIZE);
            byte[] encryptedBlock = encryptBlock(block, previousBlock);
            System.arraycopy(encryptedBlock, 0, result, i, BLOCK_SIZE);
            previousBlock = encryptedBlock;

            if (debug) {
                System.out.println("Encrypted block " + ((i - IV_SIZE) / BLOCK_SIZE) + ": " +
                        Arrays.toString(encryptedBlock));
            }
        }

        return result;
    }

    public byte[] decrypt(byte[] encryptedData) {
        if (debug) System.out.println("Decrypting data of length: " + encryptedData.length);

        if (encryptedData.length < IV_SIZE + BLOCK_SIZE || encryptedData.length % BLOCK_SIZE != 0) {
            throw new IllegalArgumentException("Invalid encrypted data length");
        }

        // Извлекаем IV
        byte[] iv = Arrays.copyOfRange(encryptedData, 0, IV_SIZE);
        byte[] result = new byte[encryptedData.length - IV_SIZE];
        byte[] previousBlock = iv;

        if (debug) System.out.println("IV: " + Arrays.toString(iv));

        // Расшифровываем блоки
        for (int i = 0; i < result.length; i += BLOCK_SIZE) {
            byte[] block = Arrays.copyOfRange(encryptedData, i + IV_SIZE, i + IV_SIZE + BLOCK_SIZE);
            byte[] decryptedBlock = decryptBlock(block, previousBlock);
            System.arraycopy(decryptedBlock, 0, result, i, BLOCK_SIZE);
            previousBlock = block;

            if (debug) {
                System.out.println("Decrypted block " + (i / BLOCK_SIZE) + ": " +
                        Arrays.toString(decryptedBlock));
            }
        }

        // Получаем последний байт (размер padding)
        int paddingSize = result[result.length - 1] & 0xFF;

        if (debug) {
            System.out.println("Detected padding size: " + paddingSize);
            System.out.println("Last block: " +
                    Arrays.toString(Arrays.copyOfRange(result, result.length - BLOCK_SIZE, result.length)));
        }

        // Проверяем padding
        if (paddingSize <= 0 || paddingSize > BLOCK_SIZE) {
            throw new IllegalArgumentException("Invalid padding size: " + paddingSize);
        }

        // Проверяем все байты padding
        for (int i = result.length - paddingSize; i < result.length; i++) {
            if ((result[i] & 0xFF) != paddingSize) {
                if (debug) {
                    System.out.println("Invalid padding at position " + i +
                            ", expected " + paddingSize + ", got " + (result[i] & 0xFF));
                }
                throw new IllegalArgumentException("Invalid padding values");
            }
        }

        // Удаляем padding
        return Arrays.copyOfRange(result, 0, result.length - paddingSize);
    }

    private byte[] encryptBlock(byte[] block, byte[] previousBlock) {
        byte[] result = new byte[block.length];

        // XOR с предыдущим блоком (CBC mode)
        for (int i = 0; i < block.length; i++) {
            result[i] = (byte)(block[i] ^ previousBlock[i]);
        }

        // Простое обратимое преобразование
        for (int i = 0; i < result.length; i++) {
            int value = result[i] & 0xFF;
            // Сложение по модулю
            value = (value + keyValues[i]) & 0xFF;
            result[i] = (byte)value;
        }

        return result;
    }

    private byte[] decryptBlock(byte[] block, byte[] previousBlock) {
        byte[] result = new byte[block.length];
        System.arraycopy(block, 0, result, 0, block.length);

        // Обратное преобразование
        for (int i = 0; i < result.length; i++) {
            int value = result[i] & 0xFF;
            // Вычитание по модулю
            value = (value - keyValues[i]) & 0xFF;
            result[i] = (byte)value;
        }

        // XOR с предыдущим блоком
        for (int i = 0; i < result.length; i++) {
            result[i] = (byte)(result[i] ^ previousBlock[i]);
        }

        return result;
    }

    private byte[] generateIV() {
        byte[] iv = new byte[IV_SIZE];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    private int sumDigits(String str) {
        return str.chars().map(ch -> ch - '0').sum();
    }
}