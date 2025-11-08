package dev.kaato.notzexcavate.managers

import com.intellectualcrafters.plot.`object`.Plot
import dev.kaato.notzexcavate.NotzExcavate.Companion.messageU
import dev.kaato.notzexcavate.NotzExcavate.Companion.othersU
import dev.kaato.notzexcavate.entities.Shovel
import dev.kaato.notzexcavate.managers.DatabaseManager.dropShovelDB
import dev.kaato.notzexcavate.managers.DatabaseManager.insertShovelDB
import dev.kaato.notzexcavate.managers.DatabaseManager.loadShovelsDB
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
    private val shovels = hashMapOf<String, Shovel>()

    fun createShovel(p: Player, name: String, display: String) {
        if (shovels.containsKey(name)) {
            messageU.send(p, "&eA Shovel &f${name}&e já existe! &7(${shovels[name]!!.getDisplay()}&7)")
            return
        }

        val shovel = insertShovelDB(Shovel(name, display))
        shovels[shovel.name] = shovel
        messageU.send(p, "createShovel", shovel.getDisplay())
    }

    fun getShovels(): Array<String> {
        return shovels.keys.toTypedArray()
    }

    fun giveShovel(p: Player, shovel: Shovel) {
        p.inventory.addItem(shovel.getShovel().clone())
    }

    fun giveShovel(p: Player, target: Player, shovel: Shovel) {
        giveShovel(target, shovel)
        messageU.send(target, "giveShovel1", shovel.getDisplay())
        messageU.send(p, "giveShovel2", defaults = listOf(shovel.getDisplay(), target.name))
    }

    fun giveShovel(sender: ConsoleCommandSender, target: Player, shovel: Shovel, qtt: Int) {
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

    fun updateShovel(p: Player, shovel: Shovel) {
        val qtt = shovel.updateShovel()
        messageU.send(p, "updateShovel2", defaults = listOf(shovel.getDisplay(), qtt.toString()))
    }

    fun deleteShovel(name: String): Boolean {
        return deleteShovel(shovels[name]!!)
    }

    fun deleteShovel(shovel: Shovel): Boolean {
        return try {
            dropShovelDB(shovel)
            shovels.remove(shovel.name)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun isShovel(item: ItemStack): Boolean { // manager
        return shovels.values.any { it.getShovel().isSimilar(item) }
    }

    fun getShovel(item: ItemStack): Shovel {
        return shovels.values.find { it.getShovel().isSimilar(item) }!!
    }

    fun getShovel(name: String): Shovel? {
        return shovels[name]
    }

    fun hasShovel(name: String): Boolean {
        return shovels.containsKey(name)
    }

    fun clickShovel(p: Player, plot: Plot, item: ItemStack, click: Action) { // event
        when (click) {
            RIGHT_CLICK_AIR -> startExcavatorFromShovel(p, plot, item)
            LEFT_CLICK_AIR -> getExcavatorStatus(p, plot)
            else -> {
                if (containsExcavator(plot)) getExcavatorStatus(p, plot)
                else messageU.send(p, "clickShovel")
            }
        }
    }

    private fun startExcavatorFromShovel(p: Player, plot: Plot, item: ItemStack) {
        val shovel = getShovel(item)

        if (plot.owners.isEmpty()) {
            messageU.send(p, "emptyPlot")
            return
        }

        if (plot.owners.first() != p.uniqueId && !(othersU.isAdmin(p) || p.hasPermission("notzexcavate.useany"))) {
            messageU.send(p, "noPermissionExcavator")
            return
        }

        if (containsExcavator(plot) && isRunningOrComplete(plot)) {
            getExcavatorStatus(p, plot)
            return
        }

        if (shovel.getDuration() == -1) {
            messageU.send(p, "startExcavatorFromShovel", shovel.getDisplay())
            if (othersU.isAdmin(p)) p.performCommand("nex ${shovel.name} setduration")
        } else if (!startExcavation(p, plot, shovel)) getExcavatorStatus(p, plot)
    }

    private fun startExcavation(p: Player, plot: Plot, shovel: Shovel): Boolean { //
        return try {
            startExcavator(p, plot, shovel)
            p.inventory.removeItem(shovel.getShovel().clone())
            true
        } catch (e: Exception) {
//            e.printStackTrace()
            false
        }
    }

    fun addAllowedBlock(p: Player, block: String, shovel: Shovel) {
        validMaterial(p, block).let {
            if (it != null) messageU.send(p, "addAllowedBlock", defaults = listOf((if (shovel.addAllowedBlock(it)) 1 else 2).toString(), it.name))
        }
    }

    fun addBlockedBlock(p: Player, block: String, shovel: Shovel) {
        validMaterial(p, block).let {
            if (it != null) messageU.send(p, "addBlockedBlock", defaults = listOf((if (shovel.addBlockedBlock(it)) 1 else 2).toString(), it.name))
        }
    }

    fun remAllowedBlock(p: Player, block: String, shovel: Shovel) {
        validMaterial(p, block).let {
            if (it != null) messageU.send(p, "remAllowedBlock", defaults = listOf((if (shovel.remAllowedBlock(it)) 1 else 2).toString(), it.name))
        }
    }

    fun remBlockedBlock(p: Player, block: String, shovel: Shovel) {
        validMaterial(p, block).let {
            if (it != null) messageU.send(p, "remBlockedBlock", defaults = listOf((if (shovel.remBlockedBlock(it)) 1 else 2).toString(), it.name))
        }
    }

    fun clearAllowedBlocks(p: Player, shovel: Shovel) {
        messageU.send(p, "clearAllowedBlocks", (if (shovel.clearAllowedBlocks()) 1 else 2).toString())
    }

    fun clearBlockedBlocks(p: Player, shovel: Shovel) {
        messageU.send(p, "clearBlockedBlocks", (if (shovel.clearBlockedBlocks()) 1 else 2).toString())
    }

    fun validMaterial(p: Player, material: String): Material? {
        Material.getMaterial(material).let {
            if (it == null) messageU.send(p, "invalidBlock")
            return it
        }
    }

    fun loadShovels() { // manager
        loadShovelsDB().forEach { shovels[it.key] = it.value }
    }

    fun saveShovels() { // manager
        shovels.values.forEach { it.save() }
    }
}