package notzexcavate.notzapi.utils

import org.bukkit.Bukkit
import org.bukkit.entity.Player

object PingU {
    fun getPlayerPing(player: Player?): Int {
        return try {
            val craftPlayer = Class.forName("org.bukkit.craftbukkit.$serverVersion.entity.CraftPlayer")
            val converted = craftPlayer.cast(player)
            val handle = converted.javaClass.getMethod("getHandle")
            val entityPlayer = handle.invoke(converted)
            entityPlayer.javaClass.getField("ping").getInt(entityPlayer)
        } catch (ex: Exception) {
            Bukkit.getConsoleSender().sendMessage(ex.message)
            -1
        }
    }

    val serverVersion: String
        get() = Bukkit.getServer().javaClass.getPackage().name.replace(".", ",").split(",".toRegex())
            .dropLastWhile { it.isEmpty() }
            .toTypedArray()[3]
}
