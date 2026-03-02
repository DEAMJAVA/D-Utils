package net.deamjava.d_utils.config

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.fabricmc.loader.api.FabricLoader
import java.io.File

/**
 * Manages D Utils configuration, persisted to disk as JSON.
 */
data class DUtilsConfig(
    var protectionEnabled: Boolean = true,
    var signProtection: Boolean = true,
    var anvilProtection: Boolean = true,
    var bookProtection: Boolean = true
) {
    companion object {
        private val GSON: Gson = GsonBuilder().setPrettyPrinting().create()
        private val CONFIG_FILE: File by lazy {
            FabricLoader.getInstance().configDir.resolve("d_utils.json").toFile()
        }

        private var _instance: DUtilsConfig? = null

        val instance: DUtilsConfig
            get() = _instance ?: load()

        fun load(): DUtilsConfig {
            return if (CONFIG_FILE.exists()) {
                try {
                    GSON.fromJson(CONFIG_FILE.readText(), DUtilsConfig::class.java) ?: DUtilsConfig()
                } catch (e: Exception) {
                    DUtilsConfig()
                }
            } else {
                DUtilsConfig()
            }.also {
                _instance = it
                it.save()
            }
        }

        fun save() {
            instance.save()
        }
    }

    fun save() {
        CONFIG_FILE.parentFile?.mkdirs()
        CONFIG_FILE.writeText(GSON.toJson(this))
    }

    /**
     * Returns true if a specific screen type should have protection active.
     */
    fun isSignProtectionActive(): Boolean = protectionEnabled && signProtection
    fun isAnvilProtectionActive(): Boolean = protectionEnabled && anvilProtection
    fun isBookProtectionActive(): Boolean = protectionEnabled && bookProtection
}
