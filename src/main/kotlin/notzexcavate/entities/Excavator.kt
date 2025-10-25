package notzexcavate.entities

import com.intellectualcrafters.plot.`object`.Plot
import com.intellectualcrafters.plot.`object`.PlotId
import notzexcavate.Main.Companion.papi
import notzexcavate.enums.Status
import notzexcavate.managers.DatabaseManager.updateExcavatorDB
import notzexcavate.utils.ExcavateU.getBlocks
import notzexcavate.znotzapi.NotzAPI.Companion.plugin
import notzexcavate.znotzapi.utils.MessageU.c
import notzexcavate.znotzapi.utils.MessageU.formatDate
import notzexcavate.znotzapi.utils.MessageU.send
import notzexcavate.znotzapi.utils.MessageU.sendHeader
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import java.io.Serializable
import java.util.*
import kotlin.concurrent.schedule

class Excavator(val id: Int, val owner: OfflinePlayer, val plotId: String, var time: Long, val timeLeft: Long, var blocks: Int) {
    data class ExcavatorModel(val id: Int, val player: OfflinePlayer, val plotId: String, val time: Long, var timeLeft: Long, val blocks: Int, val serialVersionUID: Long) : Serializable

    constructor(plot: Plot, minutes: Int) : this(0, Bukkit.getOfflinePlayer(plot.owners.first()), plot.id.toString(), (minutes*60*1000).toLong(), (minutes*60*1000).toLong(), getBlocks(plot).size)

    private val plot: Plot = papi.allPlots.find { it.id.toString() == plotId }!!
    private lateinit var task: Timer
    private var blocksLeft: MutableList<Block>
    private var excavationStart: String
    private var excavationStartOwner: String
    private var excavationFinish: String
    private var status: Status

    init {
        blocksLeft = getBlocks(plot)
        excavationStart = "&aFoi iniciado a escavação da &fplot ${plot.id} (${owner.name})&a."
        excavationStartOwner = "&aFoi iniciado a escavação da sua &fplot ${plot.id}&a."
        excavationFinish = "&aPlot escavado com sucesso em &2{default}&a."
        status = if (blocks == blocksLeft.size) Status.NOTSTARTED else if (blocksLeft.isEmpty()) Status.COMPLETED else Status.PAUSED
    }

    fun getPlotID(): PlotId {
        return plot.id
    }

    fun isntStarted(): Boolean {
        return blocks == blocksLeft.size
    }

    fun isRunning(): Boolean {
        return status == Status.RUNNING
    }

    fun isOnBreak(): Boolean {
        return status == Status.PAUSED
    }

    fun isCompleted(): Boolean {
        return status == Status.COMPLETED
    }

    fun status(p: Player): Boolean {
        if (blocks == 0 || blocksLeft.isEmpty())
            return true

        val progress = "❱❱❱❱❱❱❱❱❱❱❱❱❱❱❱"
        val progressIndex = (blocks-blocksLeft.size)/(blocks/15)

        val finalStatus = progress.substring(0, progressIndex+1) + if (progressIndex < 15) "&7"+progress.substring(progressIndex+1) else ""
        val res = (blocks-blocksLeft.size)/(blocks/100.0)
        val percentage = String.format("%.2f", res)

        sendHeader(p, """${"\n"}
            &3PlotID: &f${plot.id}
            &3Dono: &f${owner.name}
            &eStatus: &f${status.description}
            &a$percentage&e% - $finalStatus
          &r
        """.trimIndent())
        return false
    }

    fun start(p: Player) {
        if (!start()){
            send(p, "&cEsta plot já foi escavada!")
        }
        else if (owner.name != p.name) send(p, excavationStart)
    }

    fun start(): Boolean {
        if (blocks == 0) {
            blocks = getBlocks(plot).size
        }

        if (blocksLeft.isEmpty()) {
            blocksLeft = getBlocks(plot)
        }
        if (blocksLeft.isEmpty() || status == Status.COMPLETED)
            return false

        status = Status.RUNNING

        if (owner.isOnline)
            send(owner.player, excavationStartOwner)

        val timeMilli = if (time < 1 || time/blocks < 1) 1 else time/blocks

        val finish = blocksLeft.size
        var i = 0

        task = Timer()
        task.schedule(0, timeMilli) {
            try {
                object : BukkitRunnable() {
                    override fun run() {
                        if (blocksLeft.isNotEmpty()) {
                            blocksLeft.first().type = Material.AIR
                            blocksLeft.removeFirst()
                        }
                    }
                }.runTaskLater(plugin, 1)

                if (i++ == finish) {
                    if (owner.isOnline)
                        send(owner.player, excavationFinish, formatDate(time, true))

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
        blocksLeft.clear()
        status = Status.PAUSED
        task.cancel()
        Bukkit.getConsoleSender().sendMessage(c("&eA limpeza da plot ${plot.id} (${owner.name}) foi cancelada!"))
    }

    fun getExcavatorModel(): ExcavatorModel {
        return ExcavatorModel(id, owner, plotId, time, timeLeft, blocks, id.toLong())
    }

    fun save() {
        updateExcavatorDB(this)
    }
}