package dev.kaato.notzexcavate.managers

import dev.kaato.notzapi.utils.MessageU.Companion.formatDateTime
import dev.kaato.notzapi.utils.MessageU.Companion.join
import dev.kaato.notzexcavate.NotzExcavate.Companion.messageU
import dev.kaato.notzexcavate.NotzExcavate.Companion.papi
import dev.kaato.notzexcavate.managers.ExcavateManager.removeExcavator
import dev.kaato.notzexcavate.managers.ShovelManager.addAllowedBlock
import dev.kaato.notzexcavate.managers.ShovelManager.addBlockedBlock
import dev.kaato.notzexcavate.managers.ShovelManager.clearAllowedBlocks
import dev.kaato.notzexcavate.managers.ShovelManager.clearBlockedBlocks
import dev.kaato.notzexcavate.managers.ShovelManager.deleteShovel
import dev.kaato.notzexcavate.managers.ShovelManager.getShovel
import dev.kaato.notzexcavate.managers.ShovelManager.giveShovel
import dev.kaato.notzexcavate.managers.ShovelManager.remAllowedBlock
import dev.kaato.notzexcavate.managers.ShovelManager.remBlockedBlock
import dev.kaato.notzexcavate.managers.ShovelManager.updateShovel
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import java.text.ParseException

object CommandManager {
    fun clearAllowedCMD(p: Player, sh: String): Boolean {
        val shovel = getShovel(sh) ?: return false
        clearAllowedBlocks(p, shovel)
        return true
    }

    fun clearBlockedCMD(p: Player, sh: String): Boolean {
        val shovel = getShovel(sh) ?: return false
        clearBlockedBlocks(p, shovel)
        return true
    }

    fun getShovelCMD(p: Player, target: String? = null, sh: String): Boolean {
        var ptarget: Player? = null
        val player = if (target == null) p else {
            Bukkit.getPlayerExact(target).let {
                if (it != null) {
                    ptarget = it
                    it
                } else {
                    messageU.send(p, "offlinePlayer")
                    return@getShovelCMD false
                }
            }
        }

        val shovel = getShovel(sh) ?: return false
        if (ptarget != null) giveShovel(player, ptarget, shovel)
        else giveShovel(player, shovel)
        return true
    }

    fun getShovelCMD(sender: ConsoleCommandSender, target: String? = null, sh: String, quantity: String): Boolean {
        val qtt = quantity.toIntOrNull() ?: 0
        if (qtt < 1 || qtt > 36) {
            messageU.send(sender, "validNumber3", "36")
            return false
        }

        val player: Player = Bukkit.getPlayerExact(target).let {
            if (it != null) {
                it
            } else {
                messageU.send(sender, "offlinePlayer")
                return@getShovelCMD false
            }
        }

        val shovel = getShovel(sh) ?: return false
        giveShovel(sender, player, shovel, qtt)
        return true
    }

    fun updateItemCMD(p: Player, sh: String): Boolean {
        val shovel = getShovel(sh) ?: return false
        updateShovel(p, shovel)
        return true
    }

    fun addAllowedCMD(p: Player, block: String, sh: String): Boolean {
        val shovel = getShovel(sh) ?: return false
        addAllowedBlock(p, block.uppercase(), shovel)
        return true
    }

    fun addBlockedCMD(p: Player, block: String, sh: String): Boolean {
        val shovel = getShovel(sh) ?: return false
        addBlockedBlock(p, block.uppercase(), shovel)
        return true
    }

    fun remAllowedCMD(p: Player, block: String, sh: String): Boolean {
        val shovel = getShovel(sh) ?: return false
        remAllowedBlock(p, block.uppercase(), shovel)
        return true
    }

    fun remBlockedCMD(p: Player, block: String, sh: String): Boolean {
        val shovel = getShovel(sh) ?: return false
        remBlockedBlock(p, block.uppercase(), shovel)
        return true
    }

    fun removeExcavatorCMD(p: Player) {
        if (removeExcavator(papi.getPlot(p))) messageU.send(p, "removeExcavatorCMD1")
        else messageU.send(p, "removeExcavatorCMD2")
    }

    fun deleteShovelCMD(p: Player, sh: String): Boolean {
        val shovel = getShovel(sh) ?: return false
        if (deleteShovel(shovel)) messageU.send(p, "deleteShovelCMD", shovel.getDisplay())
        else messageU.send(p, "deleteShovelCMD", shovel.getDisplay())
        return true
    }

    fun setMaterialCMD(p: Player, sh: String): Boolean {
        val shovel = getShovel(sh) ?: return false
        if (p.itemInHand != null) {
            messageU.send(p, "setMaterialCMD1", defaults = listOf(shovel.name, shovel.getMaterial().name, p.itemInHand.type.name))
            shovel.setMaterial(p.itemInHand.type)
        } else messageU.send(p, "setMaterialCMD2")
        return true
    }

    fun setDisplayCMD(p: Player, display: String, sh: String): Boolean {
        val shovel = getShovel(sh) ?: return false
        messageU.send(p, "setDisplayCMD", defaults = listOf(shovel.name, shovel.getDisplay(), display))
        shovel.setDisplay(display)
        return true
    }

    fun setDurationCMD(p: Player, time: String, sh: String): Boolean {
        val shovel = getShovel(sh) ?: return false
        val duration = try {
            time.toInt()
        } catch (e: ParseException) {
            messageU.send(p, "validNumber1")
            return false
        }

        if (duration < 1) {
            messageU.send(p, "validNumber2")
            return false
        }

        val new = formatDateTime(minutes = duration, eng = true)

        if (shovel.getDuration() > 0) messageU.send(
            p, "setDurationCMD1", defaults = listOf(shovel.name, formatDateTime(minutes = shovel.getDuration(), eng = true), new)
        ) else messageU.send(
            p, "setDurationCMD2", defaults = listOf(shovel.name, new)
        )

        shovel.setDuration(duration)
        return true
    }

    fun viewAllowedCMD(p: Player, sh: String): Boolean {
        val shovel = getShovel(sh) ?: return false
        messageU.send(p, "viewAllowedCMD", join(shovel.getAllowedBlocks().map(Material::name)))
        return true
    }

    fun viewBlockedCMD(p: Player, sh: String): Boolean {
        val shovel = getShovel(sh) ?: return false
        messageU.send(p, "viewBlockedCMD", join(shovel.getBlockedBlocks().map(Material::name)))
        return true
    }
}