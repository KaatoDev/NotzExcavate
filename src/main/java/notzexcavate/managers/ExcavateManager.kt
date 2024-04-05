package notzexcavate.managers

import com.intellectualcrafters.plot.`object`.Plot
import com.intellectualcrafters.plot.`object`.PlotId
import notzexcavate.entities.Excavator
import notzexcavate.managers.DatabaseManager.dropExcavatorDB
import notzexcavate.managers.DatabaseManager.insertExcavatorDB
import notzexcavate.managers.DatabaseManager.loadExcavatorsDB
import notzexcavate.notzapi.utils.MessageU.send
import org.bukkit.entity.Player

object ExcavateManager {
    val excavators = hashMapOf<PlotId, Excavator>()

    fun containsExcavator(plot: Plot): Boolean {
        return excavators.containsKey(plot.id)
    }

    fun startExcavator(p: Player, plot: Plot, minutes: Int) {
        val excavator = Excavator(plot, minutes)
        insertExcavatorDB(excavator)
        excavators[plot.id] = excavator
        excavator.start(p)
    }

    fun removeExcavator(plot: Plot) { // comando e event
        removeExcavator(excavators[plot.id]!!)
    }

    fun removeExcavator(excavator: Excavator): Boolean {
        return try {
            dropExcavatorDB(excavator)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun getExcavatorStatus(p: Player, plot: Plot) {
        if (excavators[plot.id]!!.status(p))
            send(p, "&eEsta plot já foi escavada!")
    }

    fun loadExcavators() {
        loadExcavatorsDB().forEach { excavators[it.key] = it.value }
        restartExcavators()
    }

    fun restartExcavators() {

    }

    fun saveExcavators() {
        excavators.values.forEach { it.save() }
    }
}