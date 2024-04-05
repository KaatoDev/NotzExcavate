package notzexcavate.entities

import com.intellectualcrafters.plot.api.PlotAPI
import com.intellectualcrafters.plot.`object`.Plot
import com.intellectualcrafters.plot.`object`.PlotArea
import com.intellectualcrafters.plot.`object`.PlotId
import notzexcavate.Main
import notzexcavate.enums.Status
import notzexcavate.managers.DatabaseManager.updateExcavatorDB
import notzexcavate.notzapi.utils.MessageU.c
import notzexcavate.notzapi.utils.MessageU.formatDate
import notzexcavate.notzapi.utils.MessageU.send
import notzexcavate.utils.ExcavateU.getBlocks
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import java.io.Serializable
import java.util.*
import kotlin.concurrent.schedule

class Excavator(plotId: PlotId, plotArea: PlotArea, private val time: Long, private val timeLeft: Long, val blocks: Int, val blocksLeft: MutableList<Block>) {
    data class ExcavatorModel(val plotId: PlotId, val plotArea: PlotArea, val time: Long, var timeLeft: Long, val blocks: Int, val blocksLeft: MutableList<Block>, val serialVersionUID: Long) : Serializable

    constructor(plot: Plot, minutes: Int) : this(plot.id, plot.area, (minutes*60*1000).toLong(), (minutes*60*1000).toLong(), getBlocks(plot).size, getBlocks(plot))

    private val plot: Plot = PlotAPI().plotSquared.getPlot(plotArea, plotId)
    private val owner = Bukkit.getOfflinePlayer(plot.owners.first())
    private val excavationStart = "&aFoi iniciado a escavação da &fplot ${plot.id} (${owner.name}) &a."
    private val excavationFinish = "&aPlot escavado com sucesso em &2{default}&a."
    private val task = Timer()
    private var status = if (blocks == blocksLeft.size) Status.NOTSTARTED else Status.PAUSED

    fun status(p: Player): Boolean {
        if (blocksLeft.isEmpty())
            return true

        val progress = "❱❱❱❱❱❱❱❱❱❱❱❱❱❱❱"
        val progressIndex: Int = 15-15*blocks/blocksLeft.size

        val finalStatus = progress.substring(0, progressIndex+1) + if (progressIndex < 15) "&7"+progress.substring(progressIndex+1) else ""

        send(p, """
            &3PlotID: &f 
            &3Dono: &f
            
            &eStatus: &f${status.description}
            &e$finalStatus
        """.trimIndent())
        println("TESTEEEEEEE STATUS EXCAVATOR STATUS()")
        return false
    }

    fun start(p: Player) {
        if (start()) send(p, excavationStart)
        else send(p, "&cEsta plot já foi escavada!")
    }

    private fun start(): Boolean {
        if (blocksLeft.isEmpty() || status == Status.COMPLETED)
            return false

        status = Status.RUNNING

        if (owner.isOnline)
            send(owner.player, excavationStart)

        val timeMilli = time/blocks

        val finish = blocksLeft.size
        var i = 0

        task.schedule(0, timeMilli) {
            try {
                object : BukkitRunnable() {
                    override fun run() {
                        blocksLeft.first().type = Material.AIR
                        blocksLeft.removeFirst()
                    }
                }.runTaskLater(Main.plugin, 1)

                if (i++ == finish) {
                    if (owner.isOnline)
                        send(owner.player, excavationFinish, formatDate(time))

                    status = Status.COMPLETED
                    this@schedule.cancel()
                }
            } catch (e: NoSuchElementException) {
                e.printStackTrace()
                this.cancel()
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
                this.cancel()
            }
        }
        return true
    }

    fun stop(p: Player) {
        send(p, "&eA limpeza desta plot foi cancelada!")
        stop()
    }

    fun stop() {
        status = Status.PAUSED
        task.cancel()
        Bukkit.getConsoleSender().sendMessage(c("&eA limpeza da plot ${plot.id} (${owner.name}) foi cancelada!"))
    }

    fun getPlotID(): PlotId {
        return plot.id
    }

    fun getExcavatorModel(): ExcavatorModel {
        return ExcavatorModel(plot.id, plot.area, time, timeLeft, blocks, blocksLeft, "${plot.id.x}${plot.id.y}".toLong())
    }

    fun save() {
        updateExcavatorDB(this)
    }
}