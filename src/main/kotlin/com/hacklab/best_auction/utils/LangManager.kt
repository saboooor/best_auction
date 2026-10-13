package com.hacklab.best_auction.utils

import com.hacklab.best_auction.Main
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File
import java.io.InputStreamReader
import java.text.MessageFormat

class LangManager(private val plugin: Main) {
    
    private val languages = mutableMapOf<String, YamlConfiguration>()
    private var defaultLanguage = "en"
    
    init {
        loadLanguages()
    }
    
    private fun loadLanguages() {
        val langDir = File(plugin.dataFolder, "lang")
        if (!langDir.exists()) {
            langDir.mkdirs()
        }
        
        // Load default languages from resources
        val defaultLangs = listOf("en")
        for (lang in defaultLangs) {
            val langFile = File(langDir, "$lang.yml")
            if (!langFile.exists()) {
                plugin.saveResource("lang/$lang.yml", false)
            }
            
            try {
                val config = YamlConfiguration.loadConfiguration(langFile)
                // Load defaults from resources
                val defaultStream = plugin.getResource("lang/$lang.yml")
                if (defaultStream != null) {
                    val defaultConfig = YamlConfiguration.loadConfiguration(InputStreamReader(defaultStream, "UTF-8"))
                    config.setDefaults(defaultConfig)
                }
                languages[lang] = config
                plugin.logger.info("Loaded language: $lang")
            } catch (e: Exception) {
                plugin.logger.warning("Failed to load language file: $lang.yml - ${e.message}")
            }
        }
        
        // Set default language
        val configLang = plugin.config.getString("language", "en") ?: "en"
        if (languages.containsKey(configLang)) {
            defaultLanguage = configLang
        }
        
        plugin.logger.info("Default language set to: $defaultLanguage")
    }
    
    fun getMessage(key: String, vararg args: Any): String {
        return getMessage(defaultLanguage, key, *args)
    }
    
    fun getMessage(player: Player, key: String, vararg args: Any): String {
        val playerLang = getPlayerLanguage(player)
        return getMessage(playerLang, key, *args)
    }
    
    fun getMessage(lang: String, key: String, vararg args: Any): String {
        val config = languages[lang] ?: languages[defaultLanguage]
        
        val message = config?.getString(key) ?: run {
            // Fallback to default language if key not found
            if (lang != defaultLanguage) {
                languages[defaultLanguage]?.getString(key)
            } else {
                null
            }
        } ?: run {
            plugin.logger.warning("Missing translation key: $key in language: $lang (available languages: ${languages.keys})")
            plugin.logger.warning("Config keys for $lang: ${config?.getKeys(true)?.take(10)}")
            "§c[Missing: $key]"
        }
        
        return if (args.isNotEmpty()) {
            try {
                MessageFormat.format(message, *args)
            } catch (e: Exception) {
                plugin.logger.warning("Failed to format message: $key - ${e.message}")
                message
            }
        } else {
            message
        }
    }
    
    fun getPlayerLanguage(player: Player): String {
        val language = player.locale().language.lowercase(java.util.Locale.ROOT)
        return if (languages.containsKey(language)) language else defaultLanguage
    }

    fun getAvailableLanguages(): Set<String> {
        return languages.keys
    }
    
    fun reloadLanguages() {
        languages.clear()
        loadLanguages()
    }
    
    fun setDefaultLanguage(lang: String) {
        if (languages.containsKey(lang)) {
            defaultLanguage = lang
            plugin.config.set("language", lang)
            plugin.saveConfig()
        }
    }
    
    // Utility methods for common message types
    fun sendMessage(player: Player, key: String, vararg args: Any) {
        player.sendMessage(getMessage(player, key, *args))
    }
    
    fun sendErrorMessage(player: Player, key: String, vararg args: Any) {
        player.sendMessage("§c" + getMessage(player, key, *args))
    }
    
    fun sendSuccessMessage(player: Player, key: String, vararg args: Any) {
        player.sendMessage("§a" + getMessage(player, key, *args))
    }
    
    fun sendInfoMessage(player: Player, key: String, vararg args: Any) {
        player.sendMessage("§e" + getMessage(player, key, *args))
    }
}