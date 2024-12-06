# VTCrypt v1.0

## Оглавление
- [О проекте](#о-проекте)
- [Установка](#установка)
- [Основные компоненты](#основные-компоненты)
    - [Генератор ключей (VTCryptKeyGen)](#генератор-ключей-vtcryptkeygen)
    - [Шифратор (VTCryptCipher)](#шифратор-vtcryptcipher)
- [Примеры использования](#примеры-использования)
    - [Java](#java)
    - [Kotlin](#kotlin)
- [Техническая документация](#техническая-документация)
- [Безопасность](#безопасность)

## О проекте

VTCrypt — это Java-библиотека для генерации криптографических ключей и шифрования данных. Библиотека предоставляет простой и безопасный способ работы с криптографией в Java/Kotlin проектах.

### Основные возможности:
- Генерация 256-символьных криптографических ключей
- Детерминированная генерация ключей с использованием seed-значений
- Блочное шифрование данных в режиме CBC
- Поддержка шифрования данных произвольной длины
- Kotlin-friendly API с расширениями
- Поддержка Java 8+

### Области применения:
- Защита конфиденциальных данных
- Безопасное хранение информации
- Реализация защищённого обмена данными между приложениями
- Шифрование в Android-приложениях

## Установка

1. Скачайте последнюю версию библиотеки (vtcrypt-1.0.0.jar) из раздела Releases
2. Добавьте JAR-файл в свой проект:

### Для Gradle:
```groovy
dependencies {
    implementation files('libs/vtcrypt-1.0.0.jar')
}
```

### Для Maven:
```xml
<dependency>
    <groupId>ru.vladtop46</groupId>
    <artifactId>vtcrypt</artifactId>
    <version>1.0.0</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/libs/vtcrypt-1.0.0.jar</systemPath>
</dependency>
```

## Основные компоненты

### Генератор ключей (VTCryptKeyGen)

Класс `VTCryptKeyGen` отвечает за генерацию 256-символьных криптографических ключей.

#### Основные методы:
- `generateKey()` - генерация ключа на основе текущего времени
- `generateKeyFromSeed(long seed)` - генерация ключа на основе seed-значения

#### Builder API (рекомендуется):
```java
String key = VTCryptKeyGenBuilder.create()
    .withSeed(12345L)  // опционально
    .build();
```

### Шифратор (VTCryptCipher)

Класс `VTCryptCipher` реализует функции шифрования и дешифрования данных.

#### Основные методы:
- `encrypt(byte[] data)` - шифрование данных
- `decrypt(byte[] encryptedData)` - дешифрование данных

#### Builder API (рекомендуется):
```java
VTCryptCipher cipher = VTCryptCipherBuilder.create()
    .withKey(key)
    .enableDebug()  // опционально
    .build();
```

## Примеры использования

### Java

```java
// Генерация ключа
String key = VTCryptKeyGenBuilder.create()
    .withSeed(System.currentTimeMillis())
    .build();

// Создание шифратора
VTCryptCipher cipher = VTCryptCipherBuilder.create()
    .withKey(key)
    .build();

// Шифрование
String message = "Секретное сообщение";
byte[] encrypted = cipher.encrypt(message.getBytes());

// Дешифрование
byte[] decrypted = cipher.decrypt(encrypted);
String decryptedMessage = new String(decrypted);
```

### Kotlin

```kotlin
// Использование extension функций
val key = VTCryptKeyGenBuilder.create().build()

// Шифрование строки
val encrypted = "Секретное сообщение".encrypt(key)

// Дешифрование
val decrypted = encrypted.decryptWithKey(key)
val message = String(decrypted)

// Работа с бинарными данными
val data: ByteArray = getDataFromSomewhere()
val encryptedData = data.encryptWithKey(key)
val decryptedData = encryptedData.decryptWithKey(key)
```

## Техническая документация

### Алгоритм генерации ключей

1. Получение входных данных (seed или текущее время)
2. Извлечение и обработка последних 8 цифр
3. Разделение на 4 пары чисел
4. Математические преобразования:
    - Переворот пар чисел
    - Сортировка по убыванию
    - Вычисление логарифмов
    - Формирование частей ключа
5. Генерация финального 256-символьного ключа

### Алгоритм шифрования

1. Генерация случайного IV (16 байт)
2. Добавление PKCS7-подобного padding
3. Шифрование блоков в режиме CBC:
    - XOR с предыдущим блоком
    - Модульные операции с ключевыми значениями
4. Формирование выходных данных (IV + зашифрованные данные)

## Безопасность

1. Используйте надёжное хранилище для ключей (например, Android Keystore)
2. Не храните ключи в открытом виде
3. Используйте уникальные ключи для разных данных
4. Регулярно обновляйте ключи
5. Включайте режим отладки только при разработке
