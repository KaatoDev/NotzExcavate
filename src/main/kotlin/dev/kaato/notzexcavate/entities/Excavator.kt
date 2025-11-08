package dev.kaato.notzexcavate.entities

import com.intellectualcrafters.plot.`object`.Plot
import com.intellectualcrafters.plot.`object`.PlotId
import dev.kaato.notzapi.utils.MessageU.Companion.formatDateTime
import dev.kaato.notzapi.utils.MessageU.Companion.join
import dev.kaato.notzexcavate.NotzExcavate.Companion.messageU
import dev.kaato.notzexcavate.NotzExcavate.Companion.papi
import dev.kaato.notzexcavate.NotzExcavate.Companion.plugin
import dev.kaato.notzexcavate.enums.Status
import dev.kaato.notzexcavate.managers.DatabaseManager.getExcavatorIDDB
import dev.kaato.notzexcavate.managers.DatabaseManager.insertExcavatorDB
import dev.kaato.notzexcavate.managers.DatabaseManager.updateExcavatorDB
import dev.kaato.notzexcavate.utils.ExcavateU.all_allowed
import dev.kaato.notzexcavate.utils.ExcavateU.getBlocks
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import java.io.Serializable

class Excavator(var id: Int, val owner: OfflinePlayer, val plotId: String, var time: Long, var timeLeft: Long, var blocks: Int, var status: Status = Status.NOTSTARTED, val allowedBlocks: List<Material>, val blockedBlocks: List<Material>) {
    data class ExcavatorModel(val id: Int, val player: OfflinePlayer, val plotId: String, val time: Long, var timeLeft: Long, val blocks: Int, var status: Status, val allowedBlocks: List<Material>, val blockedBlocks: List<Material>, val serialVersionUID: Long) : Serializable

    constructor(plot: Plot, minutes: Int, allowedBlocks: List<Material>, blockedBlocks: List<Material>) : this(0, Bukkit.getOfflinePlayer(plot.owners.first()), plot.id.toString(), (minutes * 60 * 1000).toLong(), (minutes * 60 * 1000).toLong(), getBlocks(plot, allowedBlocks, blockedBlocks).size, Status.NOTSTARTED, allowedBlocks, blockedBlocks) {
        insertExcavatorDB(this)
        id = getExcavatorIDDB(plotId)
        save()
    }

    private val plot: Plot = papi.allPlots.find { it.id.toString() == plotId } ?: throw IllegalStateException("Plot $plotId not found")
    private lateinit var task: BukkitRunnable

    //    private lateinit var task: Timer
    private var blocksLeft: MutableList<Block>

    init {
        blocksLeft = if (isCompleted()) mutableListOf() else getBlocks(plot, allowedBlocks, blockedBlocks)
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
        if (blocks == 0 || blocksLeft.isEmpty()) return true

        val progress = "❱❱❱❱❱❱❱❱❱❱❱❱❱❱❱"
        val progressIndex = (blocks - blocksLeft.size) / (blocks / 15)

        val finalStatus = progress.substring(0, progressIndex + 1) + if (progressIndex < 15) "&7" + progress.substring(progressIndex + 1) else ""
        val res = (blocks - blocksLeft.size) / (blocks / 100.0)
        val percentage = String.format("%.2f", res)
        val full = all_allowed[0] == "ALL"

//        println("---------------")
//        println("timeMilli: $timeMilli")
//        println("formatDateTime: " + formatDateTime(timeMilli, true))
//        println("time: $time")
//        println("formatDateTime: " + formatDateTime(time, true))
//        println("timeLeft: $timeLeft")
//        println("formatDateTime: " + formatDateTime(timeLeft, true))
//        println("timeLeft2: ${time - (time / 100 * res)}")
//        println("formatDateTime: " + formatDateTime(time - (time / 100.0 * res).toLong(), true))
//        println("res: $res")
//        println("res1: " + formatDateTime((time / res * 100).toLong(), true))
//        println("res2: " + formatDateTime(time - (time / res * 100).toLong(), true))
//        println("---------------")

        messageU.sendHeader(
            p, "status", defaults = listOf(
                plot.id.toString(),
                owner.name,
                formatDateTime(time, eng = true),
                formatDateTime(timeLeft + 1000, eng = true),
                if (full) "All" else if (allowedBlocks.isEmpty()) join(all_allowed) else join(allowedBlocks.map(Material::name)).lowercase(),
                if (blockedBlocks.isEmpty()) "None" else join(blockedBlocks.map(Material::name)).lowercase(),
                status.description,
                percentage,
                finalStatus
            )
        )
        return false
    }

    fun start(p: Player) {
        if (isCompleted() || !start()) {
            messageU.send(p, "plotCompleted")
        } else if (owner.name != p.name) messageU.send(p, "excavationStart", defaults = listOf(plot.id.toString(), owner.name))
    }

    // paste here
    fun start(): Boolean {
        if (isCompleted()) return false

        if (blocksLeft.isEmpty()) {
            blocksLeft = getBlocks(plot, allowedBlocks, blockedBlocks)
        }

        if (blocks == 0) {
            blocks = blocksLeft.size
        }

        if (isCompleted() || blocksLeft.isEmpty()) return false

        status = Status.RUNNING

        if (owner.isOnline) messageU.send(owner.player, "excavationStartOwner", plot.id.toString())

        val ticks = time / 50
        val bpt = blocks / ticks
//        var acumulate = 0.0

//        for (i in 1..10) {
//            val ticks = i * 60 * 20
////            val timee = (i * 60 * 1000)
//            val tick = if (ticks / blocks < 1) 1 else ticks / blocks
//            val bptick = blocks / ticks
//            val tickpb = ticks / blocks
////            println("$i minutos = $tt ms por bloco?")
////            println("total: $total blocos por ms")
//            println("$tick ticks")
//            println("$bptick blocks per tick")
//            println("$tickpb ticks per blocks")
//            println("$tick tick: ${bptick * tick}")
//            println()
//        }

        val removeOneBlock = {
            if (blocksLeft.isNotEmpty()) {
                blocksLeft.first().type = Material.AIR
                blocksLeft.removeFirst()
//                timeLeft -= 50
            }
        }

        val removeBlockTick = {
            for (i in 1..bpt) {
                removeOneBlock()
            }
            timeLeft -= 50
        }

        task = object : BukkitRunnable() {
            override fun run() {
                try {
//                    println(acumulate)
                    removeBlockTick()
//                    acumulate += bpms
//                        if (bpms > 1) {
//                            removeBlock()
//                            acumulate += bpms
//                        }
//                        if (acumulate >= 1) {
//                            println(acumulate)
//                            removeBlock()
//                            acumulate -= 1.0
//                        }

                    if (blocksLeft.isEmpty()) {
                        if (owner.isOnline) messageU.send(owner.player, "plotFinished", formatDateTime(time, eng = true))

                        status = Status.COMPLETED
                        try {
                            task.cancel()
                            this.cancel()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        save()
                    }
                } catch (e: NoSuchElementException) {
                    e.printStackTrace()
                    this.cancel()
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                    this.cancel()
                }
            }
        }
        task.runTaskTimer(plugin, 0L, 1L)

//        task.schedule(0, timeMilli) {
//            try {
//                object : BukkitRunnable() {
//                    override fun run() {
//                        removeBlock()
//                        println(acumulate)
//                        acumulate += bpms
//                        if (bpms > 1) {
//                            removeBlock()
//                            acumulate += bpms
//                        }
//                        if (acumulate >= 1) {
//                            println(acumulate)
//                            removeBlock()
//                            acumulate -= 1.0
//                        }
//                    }
//                }.runTaskLater(plugin, 1)
//
//                if (i++ == finish) {
//                    if (owner.isOnline) messageU.send(owner.player, "&aPlot escavado com sucesso em &2${formatDateTime(time, true)}&a")
//
//                    status = Status.COMPLETED
//                    this@schedule.cancel()
//                    save()
//                }
//            } catch (e: NoSuchElementException) {
//                e.printStackTrace()
//                this.cancel()
//            } catch (e: IllegalArgumentException) {
//                e.printStackTrace()
//                this.cancel()
//            }
//        }
        return true
    }

    fun stop(p: Player) {
        messageU.send(p, "excavatorCancelled1")
        stop()
    }

    fun stop() {
        blocksLeft.clear()
        status = Status.PAUSED
        try {
            task.cancel()
        } catch (e: RuntimeException) {
            e.printStackTrace()
        }
        save()
        messageU.send(Bukkit.getConsoleSender(), "excavatorCancelled2", defaults = listOf(plot.id.toString(), owner.name))
    }

    fun getExcavatorModel(): ExcavatorModel {
        return ExcavatorModel(id, owner, plotId, time, timeLeft, blocks, status, allowedBlocks, blockedBlocks, id.toLong())
    }

    fun save() {
        updateExcavatorDB(this)
    }
}


//    fun start(): Boolean {
//        println("$plotId --- $status")
//        if (status == Status.COMPLETED) return false
//
//        if (blocks == 0) {
//            blocks = getBlocks(plot, allowedBlocks, blockedBlocks).size
//        }
//
//        if (blocksLeft.isEmpty()) {
//            blocksLeft = getBlocks(plot, allowedBlocks, blockedBlocks)
//        }
//        if (blocksLeft.isEmpty() || status == Status.COMPLETED) return false
//
//        status = Status.RUNNING
//
//        if (owner.isOnline) messageU.send(owner.player, excavationStartOwner)
//
//        var timeMilli = if (time < 1 || time / blocks < 1) 1 else time / blocks
//        val bpms = blocks / time.toDouble()
//        var acumulate = 0.0
//
//        for (i in 1..10) {
//            val timee = (i * 60 * 1000)
//            val ms = if (timee / blocks < 1) 1 else timee / blocks
//            val total = (timee) / blocks
//            val bpms = blocks / timee.toDouble()
//            val mspb = timee.toDouble() / blocks
////            println("$i minutos = $tt ms por bloco?")
////            println("total: $total blocos por ms")
//            println("${timee / blocks}")
//            println("$bpms blocks per ms")
//            println("$ms ms: ${bpms * ms}")
//            println()
//        }
//
//        println("---------------")
//        println("timeMilli: $timeMilli")
//        println("formatDateTime: " + formatDateTime(timeMilli, true))
//        println("time: $time")
//        println("formatDateTime: " + formatDateTime(time, true))
//        println("timeLeft: $timeLeft")
//        println("formatDateTime: " + formatDateTime(timeLeft, true))
//        println("---------------")
//
//        val finish = blocksLeft.size
//        var i = 0
//
//        val date1 = LocalDateTime.now()
//        var date2 = LocalDateTime.now()
//        val removeBlock = {
//            if (blocksLeft.isNotEmpty()) {
//                blocksLeft.first().type = Material.AIR
//                blocksLeft.removeFirst()
//                timeLeft -= timeMilli
//            }
//        }
//
//        println(bpms)
//
//        val task = object : BukkitRunnable() {
//            override fun run() {
//                try {
//                    println(acumulate)
//                    timeLeft -= 50
//                    for (i in 1..50) {
//                        removeBlock()
//                        acumulate += bpms
////                        if (bpms > 1) {
////                            removeBlock()
////                            acumulate += bpms
////                        }
////                        if (acumulate >= 1) {
////                            println(acumulate)
////                            removeBlock()
////                            acumulate -= 1.0
////                        }
//                    }
//
//                    if (i++ == finish) {
//                        if (owner.isOnline) messageU.send(owner.player, "&aPlot escavado com sucesso em &2${formatDateTime(time, true)}&a")
//
//                        status = Status.COMPLETED
//                        task.cancel()
//                        save()
//                    }
//                } catch (e: NoSuchElementException) {
//                    e.printStackTrace()
//                    this.cancel()
//                } catch (e: IllegalArgumentException) {
//                    e.printStackTrace()
//                    this.cancel()
//                }
//            }
//        }
//        task.runTaskTimer(plugin, 0L, 1L)
//
////        task.schedule(0, timeMilli) {
////            try {
////                object : BukkitRunnable() {
////                    override fun run() {
////                        removeBlock()
////                        println(acumulate)
////                        acumulate += bpms
////                        if (bpms > 1) {
////                            removeBlock()
////                            acumulate += bpms
////                        }
////                        if (acumulate >= 1) {
////                            println(acumulate)
////                            removeBlock()
////                            acumulate -= 1.0
////                        }
////                    }
////                }.runTaskLater(plugin, 1)
////
////                if (i++ == finish) {
////                    if (owner.isOnline) messageU.send(owner.player, "&aPlot escavado com sucesso em &2${formatDateTime(time, true)}&a")
////
////                    status = Status.COMPLETED
////                    this@schedule.cancel()
////                    save()
////                }
////            } catch (e: NoSuchElementException) {
////                e.printStackTrace()
////                this.cancel()
////            } catch (e: IllegalArgumentException) {
////                e.printStackTrace()
////                this.cancel()
////            }
////        }
//        return true
//    }