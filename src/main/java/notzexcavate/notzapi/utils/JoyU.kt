package notzexcavate.notzapi.utils

import org.bukkit.entity.Player
import org.bukkit.util.Vector

object JoyU {
    fun repulsePlayer(p: Player) {
        val yaw = p.location.yaw
        val pitch = p.location.pitch

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
        p.velocity = p.velocity.add(Vector(x, y, z))
    }
}