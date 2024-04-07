package notzexcavate.znotzapi.utils

import notzexcavate.znotzapi.NotzAPI.Companion.plugin
import notzexcavate.znotzapi.utils.MessageU.send
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.util.Vector

object OthersU {
    fun hasPermission(player: Player, permission: String): Boolean {
        return player.hasPermission("${plugin.name}.$permission")
    }

    fun isAdmin(player: Player): Boolean {
        return player.hasPermission("${plugin.name}.admin")
    }

    fun isntAdmin(player: Player): Boolean {
        val isntAdmin = !isAdmin(player)
        if (isntAdmin) send(player, "no-perm")
        return isntAdmin
    }

    fun repulsePlayer(player: Player) {
        val yaw = player.location.yaw
        val pitch = player.location.pitch

        var v = yaw.toDouble()
        val x: Double
        val z: Double

        if (yaw <= -270) {
            v = (v+270) / -90
            z = -v
            x = 1-v

        } else if (yaw <= -180) {
            v = (v+180) / -90
            z = 1-v
            x = v

        } else if (yaw < -90) {
            v = (v+90) / -90
            z = v
            x = -1+v

        } else {
            v /= -90
            z = v-1
            x = -v
        }

        val y: Double = if (pitch > 0) pitch / 90.0 else 0.25
        player.velocity = player.velocity.add(Vector(x, y, z))
    }

    fun getPlayerPing(player: Player?): Int {
        val serverVersion = Bukkit.getServer().javaClass.getPackage().name.replace(".", ",").split(",".toRegex())
            .dropLastWhile { it.isEmpty() }.toTypedArray()[3]

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
}