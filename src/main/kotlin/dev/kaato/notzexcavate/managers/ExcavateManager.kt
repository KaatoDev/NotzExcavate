package dev.kaato.notzexcavate.managers

import com.intellectualcrafters.plot.`object`.Plot
import com.intellectualcrafters.plot.`object`.PlotId
import dev.kaato.notzapi.utils.MessageU.Companion.join
import dev.kaato.notzexcavate.NotzExcavate.Companion.messageU
import dev.kaato.notzexcavate.database.DatabaseManager.containExcavatorsDB
import dev.kaato.notzexcavate.database.DatabaseManager.loadExcavatorsDB
import dev.kaato.notzexcavate.entities.ExcavatorV2
import dev.kaato.notzexcavate.entities.ShovelV2
import dev.kaato.notzexcavate.enums.Status
import org.bukkit.entity.Player

object ExcavateManager {
    private val excavators = hashMapOf<PlotId, ExcavatorV2>()

    fun isRunningOrComplete(plot: Plot): Boolean {
        return excavators[plot.id]!!.isRunning() || excavators[plot.id]!!.isCompleted()
    }

    fun getRunningExcavators(player: Player) {
        val notStarted = excavators.values.filter { it.isntStarted() }.map { "(${it.plotId}, ${it.player})" }
        val running = excavators.values.filter { it.isRunning() }.map { "(${it.plotId}, ${it.player})" }
        val onBreak = excavators.values.filter { it.isOnBreak() }.map { "(${it.plotId}, ${it.player})" }
        val listed = listOf(
            Status.NOTSTARTED.description,
            if (notStarted.isNotEmpty()) join(notStarted) else "Empty",
            Status.RUNNING.description,
            if (running.isNotEmpty()) join(running) else "Empty",
            Status.PAUSED.description,
            if (onBreak.isNotEmpty()) join(onBreak) else "Empty"
        )


        messageU.sendHeader(player, if (notStarted.isNotEmpty() || running.isNotEmpty() || onBreak.isNotEmpty()) "getRunningExcavators1" else "getRunningExcavators2", defaults = listed)
    }

    fun getCompletedExcavators(player: Player) {
        val completed = excavators.values.filter { it.isCompleted() }.map { "(${it.plotId}, ${it.player})" }

        if (completed.isNotEmpty())
            messageU.send(player, "getCompletedExcavators1", join(completed))
        else messageU.send(player, "getCompletedExcavators2")
    }

    fun getAllExcavators(player: Player) {
        val all = excavators.values.map { "(${it.plotId}, ${it.player})" }

        if (all.isNotEmpty())
            messageU.send(player, "getAllExcavators1", join(all))
        else messageU.send(player, "getAllExcavators2")
    }

    fun stopExcavator(player: Player, plot: Plot) {
        if (excavators.keys.contains(plot.id)) {
            if (excavators[plot.id]!!.isRunning())
                excavators[plot.id]!!.stop(player)
            else messageU.send(player, "stopExcavator1")
        } else messageU.send(player, "stopExcavator2")
    }

    fun containsExcavator(plot: Plot): Boolean {
        return excavators.containsKey(plot.id)
    }

    fun startExcavator(player: Player, plot: Plot, shovel: ShovelV2): Boolean {
        val excavator = ExcavatorV2(plot, shovel)
        excavators[plot.id] = excavator
        return excavator.start(player)
    }

    fun removeExcavator(plot: Plot): Boolean? {
        return removeExcavator(excavators[plot.id] ?: return false)
    }

    fun removeExcavator(excavator: ExcavatorV2): Boolean? {
        return try {
            println(excavator.plot.id)
            val res = excavator.deleteForever()
            if (res == true) {
                excavators.remove(excavator.plot.id)
                true
            } else res
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun getExcavatorStatus(player: Player, plot: Plot) {
        if (!containsExcavator(plot))
            messageU.send(player, "getExcavatorStatus")
        else if (excavators[plot.id]!!.status(player))
            messageU.send(player, "plotCompleted")
    }

    fun loadExcavators() {
        if (containExcavatorsDB())
            loadExcavatorsDB().forEach { excavators[it.plot.id] = it }
    }

    fun checkExcavators() {
        excavators.values.filter {
            it.isntStarted() || it.isRunning()
        }.forEach { it.start() }
    }

    fun restartExcavators(player: Player) {
        restartExcavators()
        messageU.send(player, "restartExcavators", join(excavators.values.filter { it.isntStarted() && it.isRunning() }.map { "(${it.plotId}, ${it.player})" }))
    }

    fun restartExcavators() {
        if (excavators.isNotEmpty()) {
            excavators.values.filter { it.isRunning() }.forEach { it.stop(false) }
        }

        excavators.values.filter {
            it.isntStarted() || it.isOnBreak()
        }.forEach { it.start() }
    }

    fun saveExcavators(player: Player) {
        saveExcavators()
        messageU.send(player, "saveExcavators")
    }

    fun saveExcavators() {
        excavators.values.forEach { it.save() }
    }

    fun stopExcavators() {
        excavators.values.forEach { if (it.isRunning()) it.stop(true) }
    }

    fun addConvertedExcavators(cExcavators: List<ExcavatorV2>): Int {
        cExcavators.forEach {
            if (!excavators.containsKey(it.plot.id)) excavators[it.plot.id] = it
        }

        return cExcavators.size
    }
}