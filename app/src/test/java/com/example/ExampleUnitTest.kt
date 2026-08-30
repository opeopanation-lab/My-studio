package com.example

import com.example.model.CipherAlgorithm
import com.example.model.E2EEncryptionConfig
import com.example.model.KdfAlgorithm
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun defaultE2EEncryptionConfig_isValid() {
        val config = E2EEncryptionConfig()
        assertTrue(config.isE2EEnabled)
        assertEquals(CipherAlgorithm.AES_256_GCM, config.selectedCipher)
        assertEquals(KdfAlgorithm.ARGON2ID, config.selectedKdf)
        assertTrue(config.hardwareKeystoreBacked)
        assertTrue(config.zeroKnowledgeProofEnabled)
        assertTrue(config.encryptCommitHistory)
        assertTrue(config.encryptMultiFileWorkspaces)
        assertEquals(12, config.mnemonicRecoveryPhrase.size)
        assertTrue(config.masterKeyFingerprint.isNotEmpty())
    }

    @Test
    fun cipherAlgorithm_properties_areCorrect() {
        val aes = CipherAlgorithm.AES_256_GCM
        assertTrue(aes.displayName.contains("AES-256-GCM"))
        assertEquals("256-bit NSA Suite B", aes.securityLevel)
        assertFalse(aes.isPostQuantum)

        val chacha = CipherAlgorithm.CHACHA20_POLY1305
        assertEquals("ChaCha20-Poly1305", chacha.displayName)

        val kyber = CipherAlgorithm.POST_QUANTUM_KYBER
        assertTrue(kyber.isPostQuantum)
    }

    @Test
    fun kdfAlgorithm_properties_areCorrect() {
        val argon = KdfAlgorithm.ARGON2ID
        assertTrue(argon.displayName.contains("Argon2id"))
        assertEquals("64 MB RAM", argon.memoryCost)

        val scrypt = KdfAlgorithm.SCRYPT
        assertEquals("scrypt (Memory-Hard)", scrypt.displayName)
    }

    @Test
    fun customConfig_updatesCorrectly() {
        val original = E2EEncryptionConfig()
        val modified = original.copy(
            isE2EEnabled = false,
            selectedCipher = CipherAlgorithm.CHACHA20_POLY1305,
            masterKeyFingerprint = "NW-SEC-TEST-9999",
            mnemonicRecoveryPhrase = listOf("apple", "banana", "cherry")
        )
        assertFalse(modified.isE2EEnabled)
        assertEquals(CipherAlgorithm.CHACHA20_POLY1305, modified.selectedCipher)
        assertEquals("NW-SEC-TEST-9999", modified.masterKeyFingerprint)
        assertEquals(3, modified.mnemonicRecoveryPhrase.size)
    }
}
