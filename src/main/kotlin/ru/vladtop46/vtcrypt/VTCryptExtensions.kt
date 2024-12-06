package ru.vladtop46.vtcrypt

fun String.encrypt(key: String): ByteArray {
    val cipher = VTCryptCipher(key)
    return cipher.encrypt(this.toByteArray())
}

fun String.decrypt(key: String): String {
    val cipher = VTCryptCipher(key)
    return String(cipher.decrypt(this.toByteArray()))
}

fun ByteArray.encryptWithKey(key: String): ByteArray {
    val cipher = VTCryptCipher(key)
    return cipher.encrypt(this)
}

fun ByteArray.decryptWithKey(key: String): ByteArray {
    val cipher = VTCryptCipher(key)
    return cipher.decrypt(this)
}