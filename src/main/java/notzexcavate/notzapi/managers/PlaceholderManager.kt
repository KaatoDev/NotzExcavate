package notzexcavate.notzapi.managers

import me.clip.placeholderapi.PlaceholderAPI
import notzexcavate.notzapi.NotzAPI.Companion.messageManager
import notzexcavate.notzapi.utils.MessageU.c
import org.bukkit.entity.Player
import java.util.regex.Pattern

/**
 * @property placeholders The HashMap with all the placeholders.
 */
open class PlaceholderManager {
    private val placeholders: HashMap<String, String> = HashMap()
    private val pattern = Pattern.compile("\\{default[0-9]\\}")

    init {
        if (messageManager.messageFile.config.contains("prefix"))
            placeholders["{prefix}"] = c(messageManager.messageFile.config.getString("prefix"))
    }

    /**
     * Adds a placeholder to the Map.
     * @param placeholder The char sequence to be replaced.
     * @param result The replacement string for the placeholder.
     */
    fun addPlaceholder(placeholder: String, result: String) {
        placeholders[placeholder] = result
    }

    /**
     * @return The Placeholders HashMap.
     */
    fun getPlaceholders(): HashMap<String, String> {
        return placeholders
    }

    /**
     * @return If the placeholder exists.
     */
    fun contains(placeholder: String): Boolean {
        return placeholders.containsKey(placeholder)
    }

    /**
     * @param text The text to be replaced by placeholders.
     * @return The text replaced with the placeholders.
     */
    fun set(text: String): String {
        if (!text.contains("{") && !text.contains("}"))
            return c(text)

        val txt = StringBuilder()

        text.split(" ").forEach {
            if (text.contains("{") && text.contains("}"))
                txt.append(replace(it)).append(" ")
        }

        return c(txt.toString())
    }

    /**
     * @param text The text to be replaced by placeholders.
     * @param default The text that will be replaced in the {default} placeholder.
     * @return The text replaced with the placeholders and a {default} placeholder.
     */
    fun set(text: String, default: String): String {
        if (!(text.contains("{") && text.contains("}")))
            return c(text)

        val txt = StringBuilder()

       text.split(" ").forEach {
            if (text.contains("{") && text.contains("}"))
                txt.append(replace(it, default)).append(" ")

        }

        return c(txt.toString())
    }

    /**
     * @param player The player that will be sent the message and replaced in the placeholder.
     * @param text The text to be replaced by placeholders.
     * @return The text replaced with the plugin placeholders and PlaceHolderAPI.
     */
    fun set(player: Player, text: String): String {
        if (!text.contains("%")) {
            return set(text)
        }
        val txt = StringBuilder()

        set(text).split(" ").forEach {
            if (text.contains("%") && text.indexOf("%") < text.lastIndexOf("%"))
                txt.append(PlaceholderAPI.setPlaceholders(player, text)).append(" ")
        }

        return c(txt.toString())
    }

    /**
     * @param player The player that will be sent the message and replaced in the placeholder.
     * @param text The text to be replaced by placeholders.
     * @param default The text that will be replaced in the {default} placeholder.
     * @return The text replaced with the plugin placeholders, {default} placeholder and PlaceHolderAPI.
     */
    fun set(player: Player, text: String, default: String): String {
        if (!text.contains("%"))
            return c(text)

        val txt = StringBuilder()

        set(text, default).split(" ").forEach {
            if (text.contains("%") && text.indexOf("%") < text.lastIndexOf("%"))
                txt.append(it, PlaceholderAPI.setPlaceholders(player, text)).append(" ")
        }

        return c(txt.toString())
    }

    private fun replace(text: String): String {
        return if (placeholders.containsKey(text))
            text.replace(text, placeholders[text]!!)
        else text
    }

    /**
     * @param text The text to be replaced by placeholders.
     * @param default The text that will be replaced in the {default} placeholder.
     * @return The text replaced with the plugin placeholders, {default} placeholder and PlaceHolderAPI.
     */
    private fun replace(text: String, default: String): String {
        return if (placeholders.containsKey(text))
            text.replace(text, placeholders[text]!!)

        else if (text == "{default}")
            text.replace(text, default)

        else if (text.matches(pattern.toRegex()))
            text.replace(text, default.split(" ")[text[8].code].replace("_", " "))

        else text
    }
}