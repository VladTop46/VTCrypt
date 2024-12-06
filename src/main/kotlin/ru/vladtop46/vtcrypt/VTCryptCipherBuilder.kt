package ru.vladtop46.vtcrypt

class VTCryptCipherBuilder {
    private var key: String? = null
    private var debug: Boolean = false

    fun withKey(key: String) = apply { this.key = key }
    fun enableDebug() = apply { this.debug = true }
    
    fun build(): VTCryptCipher {
        requireNotNull(key) { "Encryption key must be provided" }
        return VTCryptCipher(key).apply {
            // Set debug mode when available
        }
    }

    companion object {
        @JvmStatic
        fun create() = VTCryptCipherBuilder()
    }
}
