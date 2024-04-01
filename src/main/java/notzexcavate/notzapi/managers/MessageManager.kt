package notzexcavate.notzapi.managers

import notzexcavate.notzapi.NotzAPI.Companion.placeholderManager
import notzexcavate.notzapi.apis.NotzYAML
import org.bukkit.entity.Player

open class MessageManager(val messageFile: NotzYAML) {
    /**
     * Sends a translated message with color and placeholders to a player.
     * @param player Player who will receive the message.
     * @param message The message that will be sent or the path of the message.
     */
    fun send(player: Player, message: String) {
        if (message != " " && messageFile.config.contains("messages.$message"))
            player.sendMessage(placeholderManager.set(player, "{prefix} ${messageFile.config.getString("messages.$message")}"))

        else player.sendMessage(placeholderManager.set(player, "{prefix} $message"))
    }

    /**
     * Sends a translated message with color and placeholders to a player.
     * @param player Player who will receive the message.
     * @param message The message that will be sent or the path of the message.
     * @param default The text that will be replaced in the {default} placeholder.
     */
    fun send(player: Player, message: String?, default: String) {
        if (!message!!.contains(" ") && messageFile.config.contains("messages.$message"))
            player.sendMessage(placeholderManager.set(player, "{prefix} ${messageFile.config.getString("messages.$message")}", default))
    }

    /**
     * Sends a translated message with color and placeholders to a player.
     * @param player Player who will receive the message.
     * @param message The message that will be sent or the path of the message.
     * @param defaults The texts that will be replaced in the {default[0-9]} placeholder.
     */
    fun send(player: Player, message: String?, defaults: List<String>) {
        if (!message!!.contains(" ") && messageFile.config.contains("messages.$message"))
            player.sendMessage(placeholderManager.set(player, "{prefix} ${messageFile.config.getString("messages.$message")}", defaults.joinToString(separator = " ", prefix = "", postfix = "")))

        else player.sendMessage(placeholderManager.set(player, "{prefix} $message", defaults.joinToString(separator = " ", prefix = "", postfix = "")))
    }
}