package notzexcavate.managers

import com.intellectualcrafters.plot.`object`.Plot
import notzexcavate.entities.Shovel
import notzexcavate.managers.DatabaseManager.dropShovelDB
import notzexcavate.managers.DatabaseManager.insertShovelDB
import notzexcavate.managers.DatabaseManager.loadShovelsDB
import notzexcavate.managers.ExcavateManager.containsExcavator
import notzexcavate.managers.ExcavateManager.getExcavatorStatus
import notzexcavate.managers.ExcavateManager.isRunningOrComplete
import notzexcavate.managers.ExcavateManager.startExcavator
import notzexcavate.znotzapi.utils.MessageU.send
import notzexcavate.znotzapi.utils.OthersU.isAdmin
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.block.Action.LEFT_CLICK_AIR
import org.bukkit.event.block.Action.RIGHT_CLICK_AIR
import org.bukkit.inventory.ItemStack

object ShovelManager {
    private val shovels = hashMapOf<String, Shovel>()

    fun createShovel(p: Player, name: String, display: String) {
        if (shovels.containsKey(name)) {
            send(p, "&eA Shovel &f${name}&e já existe! &7(${shovels[name]!!.getDisplay()}&7)")
            return
        }

        val shovel = insertShovelDB(Shovel(name, display))
        shovels[shovel.name] = shovel
        send(p, "&aA &f&lShovel ${shovel.getDisplay()}&a foi criada com sucesso!")
    }

    fun getShovels(): List<String> {
        return shovels.keys.toList()
    }

    fun giveShovel(p: Player, shovel: Shovel) {
        p.inventory.addItem(shovel.getShovel().clone())
        send(p, "&aVocê recebeu um escavador ${shovel.getDisplay()}&a!")
    }

    fun giveShovel(p: Player, target: Player, shovel: Shovel) {
        target.inventory.addItem(shovel.getShovel().clone())
        send(p, "&eVocê deu um escavador ${shovel.getDisplay()}&e para o player ${target.name}&e!")
        send(target, "&aVocê recebeu um escavador ${shovel.getDisplay()}&a!")
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

    fun getShovel(name: String): Shovel? {
        return shovels[name]
    }

    fun clickShovel(p: Player, plot: Plot, item: ItemStack, click: Action) { // event
        when (click) {
            RIGHT_CLICK_AIR -> startExcavatorFromShovel(p, plot, item)
            LEFT_CLICK_AIR -> getExcavatorStatus(p, plot)
            else -> {
                if (containsExcavator(plot))
                    getExcavatorStatus(p, plot)
                else send(p, "&eClique com o item no ar.")
            }
        }
    }

    private fun startExcavatorFromShovel(p: Player, plot: Plot, item: ItemStack) {
        val shovel = getShovel(item)

        if (plot.owners.isEmpty()) {
            send(p, "&eEsta plot não tem dono.")
            return
        }

        if (plot.owners.first() != p.uniqueId && !p.hasPermission("notzexcavator.useany")) {
            send(p, "&eVocê só pode usar escavadores na sua plot!")
            return
        }

        if (containsExcavator(plot) && isRunningOrComplete(plot)) {
            getExcavatorStatus(p, plot)
            return
        }

        if (shovel.getDuration() == -1) {
            send(p, "&eA duração do limpador ${shovel.getDisplay()}&e ainda &cnão foi &esetada.")
            if (isAdmin(p)) p.performCommand("nex ${shovel.name} setduration")

        } else startExcavation(p, plot, shovel)
    }

    private fun startExcavation(p: Player, plot: Plot, shovel: Shovel): Boolean { //
        return try {
            startExcavator(p, plot, shovel.getDuration())
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun replaceShovelsInInventory(old: ItemStack, new: ItemStack) {
        Bukkit.getOnlinePlayers().forEach {
            if (it.inventory.contains(old)) {
                it.inventory.contents.filter { item -> item.isSimilar(old) || item.itemMeta == new.itemMeta }.forEach { item -> item.setItemMeta(new.itemMeta); item.type = new.type }
            }
        }
    }

    fun loadShovels() { // manager
        loadShovelsDB().forEach { shovels[it.key] = it.value }
    }

    fun saveShovels() { // manager
        shovels.values.forEach { it.save() }
    }
}