package ru.vladtop46.vtcrypt

class VTCryptKeyGenBuilder {
    private var seed: Long? = null
    
    fun withSeed(seed: Long) = apply { this.seed = seed }
    
    fun build(): String {
        val keyGen = VTCryptKeyGen()
        return seed?.let { keyGen.generateKeyFromSeed(it) } ?: keyGen.generateKey()
    }

    companion object {
        @JvmStatic
        fun create() = VTCryptKeyGenBuilder()
    }
}