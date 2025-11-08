package dev.kaato.notzexcavate.managers

import com.intellectualcrafters.plot.`object`.Plot
import com.intellectualcrafters.plot.`object`.PlotId
import dev.kaato.notzapi.utils.MessageU.Companion.join
import dev.kaato.notzexcavate.NotzExcavate.Companion.messageU
import dev.kaato.notzexcavate.NotzExcavate.Companion.placeholderManager
import dev.kaato.notzexcavate.entities.Excavator
import dev.kaato.notzexcavate.entities.Shovel
import dev.kaato.notzexcavate.enums.Status
import dev.kaato.notzexcavate.managers.DatabaseManager.dropExcavatorDB
import dev.kaato.notzexcavate.managers.DatabaseManager.loadExcavatorsDB
import org.bukkit.entity.Player

object ExcavateManager {
    private val excavators = hashMapOf<PlotId, Excavator>()

    fun isRunningOrComplete(plot: Plot): Boolean {
        return excavators[plot.id]!!.isRunning() || excavators[plot.id]!!.isCompleted()
    }

    fun getRunningExcavators(p: Player) {
        val notStarted = excavators.values.filter { it.isntStarted() }.map { "(${it.getPlotID()}, ${it.owner.name})" }
        val running = excavators.values.filter { it.isRunning() }.map { "(${it.getPlotID()}, ${it.owner.name})" }
        val onBreak = excavators.values.filter { it.isOnBreak() }.map { "(${it.getPlotID()}, ${it.owner.name})" }

        messageU.sendHeader(
            p, """
            ${if (notStarted.isNotEmpty()) placeholderManager.set("getRunningExcavators1", defaults = listOf(Status.NOTSTARTED.description, join(notStarted))) else ""}
            ${if (running.isNotEmpty()) placeholderManager.set("getRunningExcavators1", defaults = listOf(Status.RUNNING.description, join(running))) else ""}
            ${if (onBreak.isNotEmpty()) placeholderManager.set("getRunningExcavators1", defaults = listOf(Status.PAUSED.description, join(onBreak))) else ""}
            """.trimIndent().ifBlank { placeholderManager.set("getRunningExcavators2") })
    }

    fun getCompletedExcavators(p: Player) {
        val completed = excavators.values.filter { it.isCompleted() }.map { "(${it.getPlotID()}, ${it.owner.name})" }

        if (completed.isNotEmpty())
            messageU.send(p, "getCompletedExcavators1", join(completed))
        else messageU.send(p, "getCompletedExcavators2")
    }

    fun getAllExcavators(p: Player) {
        val all = excavators.values.map { "(${it.getPlotID()}, ${it.owner.name})" }

        if (all.isNotEmpty())
            messageU.send(p, "getAllExcavators1", join(all))
        else messageU.send(p, "getAllExcavators2")
    }

    fun stopExcavator(p: Player, plot: Plot) {
        if (excavators.keys.contains(plot.id)) {
            if (excavators[plot.id]!!.isRunning())
                excavators[plot.id]!!.stop(p)
            else messageU.send(p, "stopExcavator1")
        } else messageU.send(p, "stopExcavator2")
    }

    fun containsExcavator(plot: Plot): Boolean {
        return excavators.containsKey(plot.id)
    }

    fun startExcavator(p: Player, plot: Plot, shovel: Shovel) {
        val excavator = Excavator(plot, shovel.getDuration(), shovel.getAllowedBlocks(), shovel.getBlockedBlocks())
        excavators[plot.id] = excavator
        excavator.start(p)
    }

    fun removeExcavator(plot: Plot): Boolean {
        return removeExcavator(excavators[plot.id])
    }

    fun removeExcavator(excavator: Excavator?): Boolean {
        return try {
            if (excavator != null && excavators.containsValue(excavator)) {
                if (excavator.isRunning())
                    excavator.stop()

                excavators.remove(excavator.getPlotID())
                dropExcavatorDB(excavator)

            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun getExcavatorStatus(p: Player, plot: Plot) {
        if (!containsExcavator(plot))
            messageU.send(p, "getExcavatorStatus")
        else if (excavators[plot.id]!!.status(p))
            messageU.send(p, "plotCompleted")
    }

    fun loadExcavators() {
        val exs = loadExcavatorsDB()
        if (exs.isEmpty())
            return

        exs.forEach { excavators[it.key] = it.value }
        restartExcavators()
    }

    fun restartExcavators(p: Player) {
        restartExcavators()
        messageU.send(p, "restartExcavators", join(excavators.values.filter { it.isntStarted() && it.isRunning() }.map { "(${it.getPlotID()}, ${it.owner.name})" }))
    }

    fun restartExcavators() {
        if (excavators.isNotEmpty()) {
            excavators.values.filter { it.isRunning() }.forEach { it.stop() }
            saveExcavators()
        }

        excavators.values.filter {
            it.isntStarted() || it.isOnBreak()
        }.forEach { it.start() }
    }

    fun saveExcavators(p: Player) {
        saveExcavators()
        messageU.send(p, "saveExcavators")
    }

    fun saveExcavators() {
        excavators.values.forEach { it.save() }
    }

    fun stopExcavators() {
        excavators.values.forEach { if (it.isRunning()) it.stop() }
    }
}