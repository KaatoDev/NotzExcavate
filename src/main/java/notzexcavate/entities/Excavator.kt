package notzexcavate.entities

import com.intellectualcrafters.plot.`object`.Plot
import com.intellectualcrafters.plot.`object`.PlotId
import notzexcavate.Main
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import java.util.*
import kotlin.concurrent.schedule

class Excavator(val plotId: PlotId, val time: Long, var timeLeft: Long, val blocks: Int, val blocksLeft: MutableList<Block>) {
    data class ExacavatorModel(val plotId: PlotId, val time: Long, var timeLeft: Long, val blocks: Int, val blocksLeft: MutableList<Block>)

    fun detectPlot(p: Player, plot: Plot, args: Array<out String>) {
        p.sendMessage(plot.allCorners.size.toString())
        p.sendMessage("max x: ${plot.regions.first().maxX}")
        p.sendMessage("max y: ${plot.regions.first().maxY}")
        p.sendMessage("max z: ${plot.regions.first().maxZ}")
        p.sendMessage("min x: ${plot.regions.first().minX}")
        p.sendMessage("min y: ${plot.regions.first().minY}")
        p.sendMessage("min z: ${plot.regions.first().minZ}")
        plot.regions.first().getCorners("PlotWorld").forEach { p.sendMessage("x: ${it.x} - y: ${it.y} - z: ${it.z}") }

        val region = plot.regions.first()


        val blocks = mutableListOf<Block>()

        var reverse = false

        for (y in 130 downTo 1) // region.minY
            for (x in if (y%2==0) region.minX..region.maxX else region.maxX downTo region.minX) {
                for (z in if (reverse) region.minZ..region.maxZ else region.maxZ downTo region.minZ) {
                    val block = Location(Bukkit.getWorld(plot.worldName), x.toDouble(), y.toDouble(), z.toDouble()).block
                    if (block.type == Material.DIRT || block.type == Material.GRASS) blocks.add(block)
                }
                reverse = !reverse
            }

        println(blocks.size)

        if (blocks.isEmpty()) {
            p.sendMessage("A plot já está escavada!")
            return
        }

        if (args.isEmpty()) {
            p.sendMessage("A plot contém ${blocks.size} blocos.")
            return
        }

//        val timeMilli = 6000L / blocks.size
        val timeMilli = if (args[0].toInt()*1000 < blocks.size) (blocks.size*1.1/blocks.size).toLong() else args[0].toInt()*1000L/blocks.size

        val finish = blocks.size
        var i = 0
        p.sendMessage(blocks.size.toString())
        p.sendMessage(timeMilli.toString())

        Timer().schedule(0, timeMilli) {
            try {
                object : BukkitRunnable() {
                    override fun run() {
                        blocks.first().type = Material.AIR
                        blocks.removeFirst()
                    }
                }.runTaskLater(Main.plugin, 1)

                if (i++ == finish) {
                    p.sendMessage("Plot escavado com sucesso em ${timeMilli * blocks.size / 10} segundos.")
                    p.sendMessage(i.toString())
                    this@schedule.cancel()
                }
            } catch (e: NoSuchElementException) {
                p.sendMessage("$i erro 79") // erro 85
                this.cancel()
            } catch (e: IllegalArgumentException) {
                p.sendMessage("$i erro 82") // erro 85
                this.cancel()
            }
        }

        var seconds = 0

        Timer().schedule(100, 100) {
            seconds++
            if (blocks.isEmpty()) {
                p.sendMessage("${seconds / 10.0} segundos.")
                this.cancel()
            }
        }
    }
}