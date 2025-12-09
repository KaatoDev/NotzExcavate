package dev.kaato.notzexcavate.managers

import dev.kaato.notzapi.utils.MessageU.Companion.formatDateTime
import dev.kaato.notzapi.utils.MessageU.Companion.join
import dev.kaato.notzexcavate.NotzExcavate.Companion.messageU
import dev.kaato.notzexcavate.NotzExcavate.Companion.papi
import dev.kaato.notzexcavate.database.DatabaseManager.checkOldDatabase
import dev.kaato.notzexcavate.database.DatabaseManager.convertExcavatorsDatabase
import dev.kaato.notzexcavate.database.DatabaseManager.convertShovelsDatabase
import dev.kaato.notzexcavate.database.DatabaseManager.eraseOldDatabase
import dev.kaato.notzexcavate.managers.ExcavateManager.addConvertedExcavators
import dev.kaato.notzexcavate.managers.ExcavateManager.removeExcavator
import dev.kaato.notzexcavate.managers.ShovelManager.addAllowedBlock
import dev.kaato.notzexcavate.managers.ShovelManager.addBlockedBlock
import dev.kaato.notzexcavate.managers.ShovelManager.addConvertedShovels
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
    fun clearAllowedCMD(player: Player, sh: String): Boolean {
        val shovel = getShovel(sh) ?: return false
        clearAllowedBlocks(player, shovel)
        return true
    }

    fun clearBlockedCMD(player: Player, sh: String): Boolean {
        val shovel = getShovel(sh) ?: return false
        clearBlockedBlocks(player, shovel)
        return true
    }

    fun getShovelCMD(player: Player, target: String? = null, sh: String): Boolean {
        var ptarget: Player? = null
        val player = if (target == null) player else {
            Bukkit.getPlayerExact(target).let {
                if (it != null) {
                    ptarget = it
                    it
                } else {
                    messageU.send(player, "offlinePlayer")
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

    fun updateItemCMD(player: Player, sh: String): Boolean {
        val shovel = getShovel(sh) ?: return false
        updateShovel(player, shovel)
        return true
    }

    fun addAllowedCMD(player: Player, block: String, sh: String): Boolean {
        val shovel = getShovel(sh) ?: return false
        addAllowedBlock(player, block.uppercase(), shovel)
        return true
    }

    fun addBlockedCMD(player: Player, block: String, sh: String): Boolean {
        val shovel = getShovel(sh) ?: return false
        addBlockedBlock(player, block.uppercase(), shovel)
        return true
    }

    fun remAllowedCMD(player: Player, block: String, sh: String): Boolean {
        val shovel = getShovel(sh) ?: return false
        remAllowedBlock(player, block.uppercase(), shovel)
        return true
    }

    fun remBlockedCMD(player: Player, block: String, sh: String): Boolean {
        val shovel = getShovel(sh) ?: return false
        remBlockedBlock(player, block.uppercase(), shovel)
        return true
    }

    fun removeExcavatorCMD(player: Player) {
        when (removeExcavator(papi.getPlot(player))) {
            true -> messageU.send(player, "removeExcavatorCMD1") // deletado
            false -> messageU.send(player, "removeExcavatorCMD2") // nao encontrado
            else -> messageU.send(player, "removeExcavatorCMD3") // erro deletado mais de 1
        }
    }

    fun deleteShovelCMD(player: Player, sh: String): Boolean {
        val shovel = getShovel(sh) ?: return false

        when (deleteShovel(shovel)) {
            true -> messageU.send(player, "deleteShovelCMD1", shovel.name) // deletado
            false -> messageU.send(player, "deleteShovelCMD2", shovel.name) // nao encontrado
            else -> messageU.send(player, "deleteShovelCMD3", shovel.name) // erro deletado mais de 1
        }
        return true
    }

    fun setMaterialCMD(player: Player, sh: String): Boolean {
        val shovel = getShovel(sh) ?: return false
        if (player.itemInHand != null) {
            messageU.send(player, "setMaterialCMD1", defaults = listOf(shovel.name, shovel.getMaterial().name, player.itemInHand.type.name))
            shovel.setMaterial(player.itemInHand.type)
        } else messageU.send(player, "setMaterialCMD2")
        return true
    }

    fun setDisplayCMD(player: Player, display: String, sh: String): Boolean {
        val shovel = getShovel(sh) ?: return false
        messageU.send(player, "setDisplayCMD", defaults = listOf(shovel.name, shovel.getDisplay(), display))
        shovel.setDisplay(display)
        return true
    }

    fun setDurationCMD(player: Player, time: String, sh: String): Boolean {
        val shovel = getShovel(sh) ?: return false
        val duration = try {
            time.toInt()
        } catch (e: ParseException) {
            messageU.send(player, "validNumber1")
            return false
        }

        if (duration < 1) {
            messageU.send(player, "validNumber2")
            return false
        }

        val new = formatDateTime(minutes = duration, eng = true)

        if (shovel.getDuration() > 0) messageU.send(
            player, "setDurationCMD1", defaults = listOf(shovel.name, formatDateTime(minutes = shovel.getDuration(), eng = true), new)
        ) else messageU.send(
            player, "setDurationCMD2", defaults = listOf(shovel.name, new)
        )

        shovel.setDuration(duration)
        return true
    }

    fun viewAllowedCMD(player: Player, sh: String): Boolean {
        val shovel = getShovel(sh) ?: return false
        messageU.send(player, "viewAllowedCMD", join(shovel.getAllowedBlocks().map(Material::name)))
        return true
    }

    fun viewBlockedCMD(player: Player, sh: String): Boolean {
        val shovel = getShovel(sh) ?: return false
        messageU.send(player, "viewBlockedCMD", join(shovel.getBlockedBlocks().map(Material::name)))
        return true
    }

    fun convertDatabaseCMD(player: Player, shovel: String) {
        if (!checkOldDatabase()) return

        val exs = addConvertedExcavators(convertExcavatorsDatabase())
        val shs = addConvertedShovels(convertShovelsDatabase(shovel))

        messageU.send(player, messageU.set("Converted a total of {default0} Excavators and {default1} Shovels successfully!", defaults = listOf(exs.toString(), shs.toString())))
    }

    fun removeOldDatabase(player: Player) {
        if (checkOldDatabase()) {
            eraseOldDatabase()
            messageU.send(player, "The old database was deleted successfully!")
        } else messageU.send(player, "There is no old database to be deleted.")
    }
}