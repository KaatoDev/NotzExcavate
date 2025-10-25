package notzexcavate.managers

import com.intellectualcrafters.plot.`object`.Plot
import com.intellectualcrafters.plot.`object`.PlotId
import notzexcavate.entities.Excavator
import notzexcavate.managers.DatabaseManager.dropExcavatorDB
import notzexcavate.managers.DatabaseManager.insertExcavatorDB
import notzexcavate.managers.DatabaseManager.loadExcavatorsDB
import notzexcavate.znotzapi.utils.MessageU.send
import notzexcavate.znotzapi.utils.MessageU.sendHeader
import org.bukkit.entity.Player

object ExcavateManager {
    private val excavators = hashMapOf<PlotId, Excavator>()

    fun isRunningOrComplete(plot: Plot): Boolean {
        return excavators[plot.id]!!.isRunning() || excavators[plot.id]!!.isCompleted()
    }

    fun isRunningOrStart(p: Player, plot: Plot) {
        if (!excavators[plot.id]!!.isRunning() && !excavators[plot.id]!!.isCompleted())
            excavators[plot.id]!!.start(p)
    }

    fun getRunningExcavators(p: Player ) {
        val notStarted = excavators.values.filter { it.isntStarted() }.map { "(${it.getPlotID()}, ${it.owner.name})" }
        val running = excavators.values.filter { it.isRunning() }.map { "(${it.getPlotID()}, ${it.owner.name})" }
        val onBreak = excavators.values.filter { it.isOnBreak() }.map { "(${it.getPlotID()}, ${it.owner.name})" }

        sendHeader(p, """
            ${if (notStarted.isNotEmpty()) "&eNão iniciado: &f$notStarted" else ""}
            ${if (running .isNotEmpty()) "&eEm endamento: &f$running" else ""}
            ${if (onBreak .isNotEmpty()) "&ePausado: &f$onBreak" else ""}
            """.trimIndent().ifBlank { "&cNão há Excavators ativos, a ser ativados ou pausados." })
    }

    fun getCompletedExcavators(p: Player ) {
        val completed = excavators.values.filter { it.isCompleted() }.map { "(${it.getPlotID()}, ${it.owner.name})" }

        if (completed.isNotEmpty())
            send(p, "&eExcavators finalizados: &f$completed")
        else send(p, "&cNão há Excavators finalizados.")
    }

    fun getAllExcavators(p: Player ) {
        val all = excavators.values.map { "(${it.getPlotID()}, ${it.owner.name})" }

        if (all.isNotEmpty())
            send(p, "&eTodos os Excavators: &f$all")
        else send(p, "&cNão há Excavators criados.")
    }

    fun stopExcavator(p: Player, plot: Plot) {
        if (excavators.keys.contains(plot.id)) {
            if (excavators[plot.id]!!.isRunning())
                excavators[plot.id]!!.stop(p)

            else send(p, "&cO Excavator desta plot não está ativo.")
        } else send(p, "&cAinda não há Excavator nesta plot!")
    }

    fun containsExcavator(plot: Plot): Boolean {
        return excavators.containsKey(plot.id)
    }

    fun startExcavator(p: Player, plot: Plot, minutes: Int) {
        val excavator = insertExcavatorDB(Excavator(plot, minutes))
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
        if (!excavators.containsKey(plot.id))
            send(p, "&cAinda não foi iniciado uma escavação nesta plot!")

        else if (excavators[plot.id]!!.status(p))
            send(p, "&eEsta plot já foi escavada!")
    }

    fun loadExcavators() {
        if (loadExcavatorsDB().isEmpty())
            return

        loadExcavatorsDB().forEach { excavators[it.key] = it.value }
        restartExcavators()
    }

    fun restartExcavators(p: Player) {
        restartExcavators()
        send(p, "&eExcavators reiniciados com sucesso!\n${excavators.values.filter { it.isntStarted() && it.isRunning()}}\n")
    }

    fun restartExcavators() {
        excavators.values.filter { it.isRunning() }.forEach { it.stop() }
        saveExcavators()
        excavators.values.filter { it.isntStarted() || it.isOnBreak() }.forEach { it.start() }
    }

    fun saveExcavators(p: Player) {
        saveExcavators()
        send(p, "&eExcavators salvos com sucesso!")
    }

    fun saveExcavators() {
        excavators.values.forEach { it.save() }
    }

    fun stopExcavators() {
        excavators.values.forEach { if (it.isRunning()) it.stop() }
    }
}