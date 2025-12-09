package dev.kaato.notzexcavate.managers

import com.intellectualcrafters.plot.`object`.Plot
import dev.kaato.notzexcavate.NotzExcavate.Companion.messageU
import dev.kaato.notzexcavate.NotzExcavate.Companion.othersU
import dev.kaato.notzexcavate.database.DatabaseManager.containShovelsDB
import dev.kaato.notzexcavate.database.DatabaseManager.loadShovelsDB
import dev.kaato.notzexcavate.entities.ShovelV2
import dev.kaato.notzexcavate.managers.ExcavateManager.containsExcavator
import dev.kaato.notzexcavate.managers.ExcavateManager.getExcavatorStatus
import dev.kaato.notzexcavate.managers.ExcavateManager.isRunningOrComplete
import dev.kaato.notzexcavate.managers.ExcavateManager.startExcavator
import org.bukkit.Material
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.block.Action.LEFT_CLICK_AIR
import org.bukkit.event.block.Action.RIGHT_CLICK_AIR
import org.bukkit.inventory.ItemStack

object ShovelManager {
    private val shovels = hashMapOf<String, ShovelV2>()

    fun createShovel(player: Player, name: String, display: String) {
        if (shovels.containsKey(name)) {
            messageU.send(player, "&eA Shovel &f${name}&e já existe! &7(${shovels[name]!!.getDisplay()}&7)")
            return
        }

        val shovel = ShovelV2(name, display)
        shovels[shovel.name] = shovel
        messageU.send(player, "createShovel", shovel.getDisplay())
    }

    fun getShovels(): Array<String> {
        return shovels.keys.toTypedArray()
    }

    fun giveShovel(player: Player, shovel: ShovelV2) {
        player.inventory.addItem(shovel.getShovel().clone())
    }

    fun giveShovel(player: Player, target: Player, shovel: ShovelV2) {
        giveShovel(target, shovel)
        messageU.send(target, "giveShovel1", shovel.getDisplay())
        messageU.send(player, "giveShovel2", defaults = listOf(shovel.getDisplay(), target.name))
    }

    fun giveShovel(sender: ConsoleCommandSender, target: Player, shovel: ShovelV2, qtt: Int) {
        if (qtt == 1) {
            giveShovel(target, shovel)
            messageU.send(target, "giveShovel1", shovel.getDisplay())
            messageU.send(sender, "giveShovel4", defaults = listOf(qtt.toString(), shovel.getDisplay(), target.name))
        } else {
            for (i in 1..qtt) giveShovel(target, shovel)
            messageU.send(target, "giveShovel3", defaults = listOf(qtt.toString(), shovel.getDisplay()))
            messageU.send(sender, "giveShovel4", defaults = listOf(qtt.toString(), shovel.getDisplay(), target.name))
        }
    }

    fun updateShovel(player: Player, shovel: ShovelV2) {
        val qtt = shovel.updateShovel()
        messageU.send(player, "updateShovel2", defaults = listOf(shovel.getDisplay(), qtt.toString()))
    }

//    fun deleteShovel(name: String): Boolean {
//        return deleteShovel(shovels[name]!!)
//    }

    fun deleteShovel(shovel: ShovelV2): Boolean? {
        return try {
            val res = shovel.deleteForever()
            if (res == true) {
                shovels.remove(shovel.name)
                true
            } else res
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun isShovel(item: ItemStack): Boolean { // manager
        return shovels.values.any { it.getShovel().isSimilar(item) }
    }

    fun getShovel(item: ItemStack): ShovelV2 {
        return shovels.values.find { it.getShovel().isSimilar(item) }!!
    }

    fun getShovel(name: String): ShovelV2? {
        return shovels[name]
    }

    fun hasShovel(name: String): Boolean {
        return shovels.containsKey(name)
    }

    fun clickShovel(player: Player, plot: Plot, item: ItemStack, click: Action) { // event
        when (click) {
            RIGHT_CLICK_AIR -> startExcavatorFromShovel(player, plot, item)
            LEFT_CLICK_AIR -> getExcavatorStatus(player, plot)
            else -> {
                if (containsExcavator(plot)) getExcavatorStatus(player, plot)
                else messageU.send(player, "clickShovel")
            }
        }
    }

    private fun startExcavatorFromShovel(player: Player, plot: Plot, item: ItemStack) {
        val shovel = getShovel(item)

        if (plot.owners.isEmpty()) {
            messageU.send(player, "emptyPlot")
            return
        }

        if (plot.owners.first() != player.uniqueId && !(othersU.isAdmin(player) || player.hasPermission("notzexcavate.useany"))) {
            messageU.send(player, "noPermissionExcavator")
            return
        }

        if (containsExcavator(plot) && isRunningOrComplete(plot)) {
            getExcavatorStatus(player, plot)
            return
        }

        if (shovel.getDuration() == 0) {
            messageU.send(player, "startExcavatorFromShovel", shovel.getDisplay())
            if (othersU.isAdmin(player)) player.performCommand("nex ${shovel.name} setduration")
        } else if (!startExcavation(player, plot, shovel)) getExcavatorStatus(player, plot)
    }

    private fun startExcavation(player: Player, plot: Plot, shovel: ShovelV2): Boolean { //
        return try {
            if (startExcavator(player, plot, shovel))
                player.inventory.removeItem(shovel.getShovel().clone())
            true
        } catch (e: Exception) {
//            e.printStackTrace()
            false
        }
    }

    fun addAllowedBlock(player: Player, block: String, shovel: ShovelV2) {
        validMaterial(player, block).let {
            if (it != null) messageU.send(player, "addAllowedBlock", defaults = listOf((if (shovel.addAllowedBlock(it)) 1 else 2).toString(), it.name))
        }
    }

    fun addBlockedBlock(player: Player, block: String, shovel: ShovelV2) {
        validMaterial(player, block).let {
            if (it != null) messageU.send(player, "addBlockedBlock", defaults = listOf((if (shovel.addBlockedBlock(it)) 1 else 2).toString(), it.name))
        }
    }

    fun remAllowedBlock(player: Player, block: String, shovel: ShovelV2) {
        validMaterial(player, block).let {
            if (it != null) messageU.send(player, "remAllowedBlock", defaults = listOf((if (shovel.remAllowedBlock(it)) 1 else 2).toString(), it.name))
        }
    }

    fun remBlockedBlock(player: Player, block: String, shovel: ShovelV2) {
        validMaterial(player, block).let {
            if (it != null) messageU.send(player, "remBlockedBlock", defaults = listOf((if (shovel.remBlockedBlock(it)) 1 else 2).toString(), it.name))
        }
    }

    fun clearAllowedBlocks(player: Player, shovel: ShovelV2) {
        messageU.send(player, "clearAllowedBlocks", (if (shovel.clearAllowedBlocks()) 1 else 2).toString())
    }

    fun clearBlockedBlocks(player: Player, shovel: ShovelV2) {
        messageU.send(player, "clearBlockedBlocks", (if (shovel.clearBlockedBlocks()) 1 else 2).toString())
    }

    fun validMaterial(player: Player, material: String): Material? {
        Material.getMaterial(material).let {
            if (it == null) messageU.send(player, "invalidBlock")
            return it
        }
    }

    fun loadShovels() {
        if (containShovelsDB())
            loadShovelsDB().forEach { shovels[it.name] = it }
    }

    fun saveShovels() {
        shovels.values.forEach { it.save() }
    }

    fun addConvertedShovels(cShovels: List<ShovelV2>): Int {
        cShovels.forEach {
            if (!shovels.containsKey(it.name)) shovels[it.name] = it
        }
        
        return cShovels.size
    }
}