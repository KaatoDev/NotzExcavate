package notzexcavate.managers

import com.intellectualcrafters.plot.`object`.Plot
import notzexcavate.entities.Shovel
import notzexcavate.managers.DatabaseManager.dropShovelDB
import notzexcavate.managers.DatabaseManager.insertShovelDB
import notzexcavate.managers.DatabaseManager.loadShovelsDB
import notzexcavate.managers.ExcavateManager.getExcavatorStatus
import notzexcavate.notzapi.utils.MessageU.send
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.block.Action.LEFT_CLICK_AIR
import org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK
import org.bukkit.inventory.ItemStack

object ShovelManager {
    private val shovels = hashMapOf<String, Shovel>()

    fun createShovel(name: String, display: String): Shovel {
        return insertShovelDB(Shovel(name, display))
    }

    fun deleteShovel(name: String): Boolean {
        return deleteShovel(shovels[name]!!)
    }

    fun deleteShovel(shovel: Shovel): Boolean {
        return try {
            dropShovelDB(shovel)
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

    fun clickShovel(p: Player, plot: Plot, item: ItemStack, click: Action) { // event
        when (click) {
            RIGHT_CLICK_BLOCK -> startExcavatorFromShovel(p, plot, item)
            LEFT_CLICK_AIR -> getExcavatorStatus(p, plot)
            else -> send(p, "&cClique com o item no ar.")
        }
    }

    private fun startExcavatorFromShovel(p: Player, plot: Plot, item: ItemStack) {
        val shovel = getShovel(item)

        if (plot.owners.first() != p.uniqueId && !p.hasPermission("notzexcavator.useany"))


        if (ExcavateManager.containsExcavator(plot)) {
            getExcavatorStatus(p, plot)
            return
        }

        if (shovel.getDuration() == 0)
            send(p, "&eA duração do limpador ${shovel.getDisplay()}&e ainda não foi setado!")
        else startExcavation(p, plot, shovel)
    }

    private fun startExcavation(p: Player, plot: Plot, shovel: Shovel): Boolean { //
        return try {
            ExcavateManager.startExcavator(p, plot, shovel.getDuration())
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun loadShovels() { // manager
        loadShovelsDB().forEach { shovels[it.key] = it.value }
    }

    fun saveShovels() { // manager
        shovels.values.forEach { it.save() }
    }
}