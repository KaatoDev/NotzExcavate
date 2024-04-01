package notzexcavate.notzapi.utils

import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import notzexcavate.notzapi.NotzAPI.Companion.messageManager
import notzexcavate.notzapi.NotzAPI.Companion.placeholderManager
import org.bukkit.entity.Player
import java.util.*

object MessageU {
    /**
     * Translates a message with color code.
     * @param message Message that will be translated.
     */
    fun c(message: String): String {
        return ChatColor.translateAlternateColorCodes('&', message)
    }

    /**
     * Sends a translated message with color to a player.
     * @param player Player who will receive the message.
     * @param message The message that will be sent.
     */
    fun send(player: Player, message: String) {
        messageManager.send(player, message)
    }

    /**
     * Sends a translated message with color to a player.
     * @param player Player who will receive the message.
     * @param message The message that will be sent.
     */
    fun send(player: Player, message: String?, default: String) {
        messageManager.send(player, message, default)
    }

    /**
     * Sends a translated message with color to a player.
     * @param player Player who will receive the message.
     * @param message The message that will be sent.
     */
    fun send(player: Player, message: String?, defaults: List<String>) {
        messageManager.send(player, message, defaults)
    }

    /**
     * @param player Player who will receive the header.
     */
    fun sendHeader(player: Player) {
        player.sendMessage(" ")
        player.sendMessage(placeholderManager.set("&f-=-=-=-&b= {prefix} &b=&f-=-=-=-"))
    }

    /**
     * @param msg Message that will be hovered.
     * @param hover Hover to be set to.
     * @return The TextComponent with hover.
     */
    fun createHover(msg: String, hover: Array<String>): TextComponent {
        val tc = TextComponent(c(msg))

        tc.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, hover.map { TextComponent(c(it)) }.toTypedArray())
        return tc
    }

    /**
     * @param msg Message that will be hovered.
     * @param cmdMsg Hover to be set to.
     * @param cmd Command to be set to in the hover.
     * @param run Run or just suggest the command.
     * @return The TextComponent with hover.
     */
    fun createHoverCMD(msg: String, cmdMsg: Array<String>, cmd: String, run: Boolean): TextComponent {
        val tc = createHover(msg, cmdMsg)

        if (run)
            tc.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd)
        else tc.clickEvent = ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, cmd)

        return tc
    }

    /**
     * @param msg Message that will be hovered.
     * @param urlMsg Hover to be set to.
     * @param url Link to be set to in the hover.
     * @return The TextComponent with hover.
     */
    fun createHoverURL(msg: String, urlMsg: Array<String>, url: String): TextComponent {
        val tc = createHover(msg, urlMsg)

        tc.clickEvent = ClickEvent(ClickEvent.Action.OPEN_URL, url)
        return tc
    }

    /**
     * Sends a hover message to a Player.
     * @param p Player that will receive the message.
     * @param msg Message that will be hovered.
     * @param hover Hover to be set to.
     */
    fun sendHover(p: Player, msg: String, hover: Array<String>) {
        p.spigot().sendMessage(createHover(c(msg), hover.map { c(it) }.toTypedArray()))
    }

    /**
     * Sends a hover message to a Player.
     * @param p Player that will receive the message.
     * @param msg Message that will be hovered.
     * @param cmdMsg Hover to be set to.
     * @param cmd Command to be set to in the hover.
     * @param run Run or just suggest the command.
     */
    fun sendHoverCMD(p: Player, msg: String, cmdMsg: Array<String>, cmd: String, run: Boolean) {
        p.spigot().sendMessage(createHoverCMD(c(msg), cmdMsg.map { c(it) }.toTypedArray(), cmd, run))
    }

    /**
     * Sends a hover message to a Player.
     * @param p Player that will receive the message.
     * @param msg Message that will be hovered.
     * @param urlMsg Hover to be set to.
     * @param url Link to be set to in the hover.
     */
    fun sendHoverURL(p: Player, msg: String, urlMsg: Array<String>, url: String) {
        p.spigot().sendMessage(createHoverURL(c(msg), urlMsg.map { c(it) }.toTypedArray(), url))
    }

    fun formatMoney(money: Double): String {
        val mm = money.toInt().toString()

        if (money < 1000)
            return mm + String.format("%.2f", money - money.toInt()).substring(1)

        val m = formatMoneyFull(money)
        val pr = m.toCharArray().count { it == '.' }-1

        val ext = "K M B T q Q s S O N D Ud Dd Td qd Qd sd Sd Od Nd V"

        return if (m.contains(".0"))
            m.substring(0, 4).replace(if (m.contains(".00")) ".00" else ".0", "") + ext.split(" ")[pr]
        else m.substring(0, if (m[3] == '0') 3 else 4) + ext.split(" ")[pr]
    }

    fun formatMoneyFull(money: Double): String {
        var mm = money.toInt().toString()

        if (money < 1000)
            return mm + String.format("%.2f", money - money.toInt()).substring(1)

        var m = when (mm.length % 3) {
            1 -> {val rr = mm.first().toString(); mm = mm.substring(1); "$rr."}
            2 -> {val rr = mm.substring(0, 2); mm = mm.substring(2); "$rr."}
            else -> ""
        }

        for (i in 0 .. mm.length step 3) if (i < mm.length)
            m += mm.slice(i until i + if (i < mm.length - 3) 3 else 3) + if (mm.length - 3 != i) "." else ""

        return m
    }

    fun formatDate(timeMillis: Long): String {
        val date = Date(timeMillis)

        val days = 1 + if (date.date > 1 && date.month > 0)  "0 31 48 79 109 140 170 201 232 262 293 323".split(" ")[date.month].toInt() else 0

        val years = date.year+1900
        while (days > 364) {
            years.inc()
            days.minus(365)
        }

        val times = listOf(years, days/30, days%30, date.hours, date.minutes, date.seconds)
        val dateFormatted = ""
        val count = 0
        times.indices.forEach {
            if (times[it] > 0) {
                count.inc()

                if (dateFormatted.isNotBlank())
                    dateFormatted.plus(" ")

                dateFormatted.plus(when (it) {
                    0 -> "ano"
                    1 -> if (times[it] == 1) "mês" else "meses"
                    2 -> "dia"
                    3 -> "hora"
                    4 -> "minuto"
                    5 -> "segundo"
                    else -> ""
                }).plus(if (it != 1 && times[it] > 1) "s" else "")

            }
        }
        return dateFormatted
    }
}