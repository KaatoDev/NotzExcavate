package dev.kaato.notzexcavate.entities

import com.intellectualcrafters.plot.`object`.Plot
import dev.kaato.notzapi.utils.MessageU.Companion.formatDateTime
import dev.kaato.notzapi.utils.MessageU.Companion.join
import dev.kaato.notzexcavate.NotzExcavate.Companion.messageU
import dev.kaato.notzexcavate.NotzExcavate.Companion.papi
import dev.kaato.notzexcavate.NotzExcavate.Companion.plugin
import dev.kaato.notzexcavate.database.DatabaseManager.deleteExcavatorDB
import dev.kaato.notzexcavate.database.DatabaseManager.getExcavatorDB
import dev.kaato.notzexcavate.database.DatabaseManager.insertExcavatorDB
import dev.kaato.notzexcavate.database.DatabaseManager.updateExcavatorDB
import dev.kaato.notzexcavate.enums.Status
import dev.kaato.notzexcavate.utils.ExcavateU.all_allowed
import dev.kaato.notzexcavate.utils.ExcavateU.getBlocks
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import java.time.LocalDateTime
import java.util.*

class ExcavatorV2(val id: Int) {
    data class ExcavatorModel(
        val id: Int,
        val player: String,
        val playerUuid: UUID,
        val plotId: String,
        val time: Long,
        val timeLeft: Long,
        val blocks: Int,
        val status: Status,
        val allowedBlocks: List<Material>,
        val blockedBlocks: List<Material>,
        val created: LocalDateTime,
        val updated: LocalDateTime?
    )

    constructor(plot: Plot, shovel: ShovelV2) : this(insertExcavatorDB(plot, shovel.getDuration() * 60000L, shovel.getAllowedBlocks(), shovel.getBlockedBlocks()))

    val player: String
    val playerUuid: UUID
    val plotId: String
    val time: Long
    private var timeLeft: Long
    private var blocks: Int
    private var status: Status
    private val allowedBlocks = mutableListOf<Material>()
    private val blockedBlocks = mutableListOf<Material>()
    val created: LocalDateTime
    private var updated: LocalDateTime?

    init {
        val ex = getExcavatorDB(id)
        player = ex.player
        playerUuid = ex.playerUuid
        plotId = ex.plotId
        time = ex.time
        timeLeft = ex.timeLeft
        blocks = ex.blocks
        status = ex.status
        allowedBlocks.addAll(ex.allowedBlocks)
        blockedBlocks.addAll(ex.blockedBlocks)
        created = ex.created
        updated = ex.updated

    }

    private var owner = Bukkit.getPlayer(playerUuid)
    val plot: Plot = papi.allPlots.find { it.id.toString() == plotId } ?: throw IllegalStateException("Plot $plotId not found")
    private lateinit var task: BukkitRunnable

    //    private lateinit var task: Timer
    private var blocksLeft: MutableList<Block>

    init {
        blocksLeft = if (isCompleted()) mutableListOf() else getBlocks(plot, allowedBlocks, blockedBlocks)
    }
    
    fun deleteForever(): Boolean? {
        return deleteExcavatorDB(id)
    }
    
    fun getAllowedBlocks(): List<Material> {
        return allowedBlocks
    }

    fun getBlockedBlocks(): List<Material> {
        return blockedBlocks
    }
    
    fun getTimeLeft() = timeLeft
    fun getBlocks() = blocks
    fun getStatus() = status
    fun getUpdated() = updated

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

    fun status(player: Player): Boolean {
        if (blocks == 0 || blocksLeft.isEmpty()) {
            status =  Status.COMPLETED
            save()
            return true
        }

        val progress = "❱❱❱❱❱❱❱❱❱❱❱❱❱❱❱"
        val progressIndex = (blocks - blocksLeft.size) / (blocks / 15)

        val finalStatus = progress.substring(0, progressIndex + 1) + if (progressIndex < 15) "&7" + progress.substring(progressIndex + 1) else ""
        val res = (blocks - blocksLeft.size) / (blocks / 100.0)
        val percentage = String.format("%.2f", res)
        val full = all_allowed[0] == "ALL"

        messageU.sendHeader(
            player, "status", defaults = listOf(
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

    fun start(player: Player): Boolean {
        if (isCompleted() || !start()) {
            messageU.send(player, "plotCompleted")
            return false
        } else if (playerUuid != player.uniqueId) messageU.send(player, "excavationStart", defaults = listOf(plot.id.toString(), owner.name))
        return true
    }

    fun start(): Boolean {
        if (isCompleted()) return false

        if (blocksLeft.isEmpty()) {
            blocksLeft = getBlocks(plot, allowedBlocks, blockedBlocks)
        }

        if (blocks == 0) {
            blocks = blocksLeft.size
        }

        if (isCompleted() || blocksLeft.isEmpty()) {
            status = Status.COMPLETED
            save()
            return false
        }

        status = Status.RUNNING

        if (owner.isOnline) messageU.send(owner.player, "excavationStartOwner", plot.id.toString())

        val ticks = time / 50
        val bpt = blocks / ticks

        val removeOneBlock = {
            if (blocksLeft.isNotEmpty()) {
                blocksLeft.first().type = Material.AIR
                blocksLeft.removeFirst()
            }
        }

        val removeBlockTick = {
            for (i in 1..bpt) {
                removeOneBlock()
            }
            timeLeft -= 50
        }
        save()

        task = object : BukkitRunnable() {
            override fun run() {
                try {
                    removeBlockTick()

                    if (blocksLeft.isEmpty()) {
                        if (owner.isOnline) messageU.send(owner.player, "plotFinished", formatDateTime(time, eng = true))

                        status = Status.COMPLETED
                        save()
                        try {
                            task.cancel()
                            this.cancel()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                } catch (e: NoSuchElementException) {
                    e.printStackTrace()
                    status = Status.PAUSED
                    save()
                    this.cancel()
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                    status = Status.PAUSED
                    save()
                    this.cancel()
                }
            }
        }
        task.runTaskTimer(plugin, 0L, 1L)

        return true
    }

    fun stop(player: Player) {
        messageU.send(player, "excavatorCancelled1")
        stop(false)
    }

    fun stop(toBeContinue: Boolean) {
        blocksLeft.clear()
        if (!toBeContinue)
            status = Status.PAUSED
        try {
            task.cancel()
        } catch (e: RuntimeException) {
            e.printStackTrace()
        }
        save()
        messageU.send(Bukkit.getConsoleSender(), "excavatorCancelled2", defaults = listOf(plot.id.toString(), owner.name))
    }

    fun save() {
        updateExcavatorDB(this)
    }
}