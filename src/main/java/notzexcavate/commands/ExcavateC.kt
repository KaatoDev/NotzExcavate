package notzexcavate.commands

import com.intellectualcrafters.plot.api.PlotAPI
import com.intellectualcrafters.plot.`object`.Plot
import notzexcavate.Main
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import java.util.*
import kotlin.concurrent.schedule

class ExcavateC : CommandExecutor {
    val papi = PlotAPI()

    override fun onCommand(p: CommandSender?, command: Command?, label: String?, args: Array<out String>?): Boolean {
        if (p !is Player)
            return false

        if (papi.isInPlot(p))
            detectPlot(p, papi.getPlot(p), args!!)
        else p.sendMessage("Você não está em uma plot!")

        return true
    }

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
        p.sendMessage("""
            ---------
            ${(args[0].toInt()*1000)}
            ${blocks.size}
            ${((args[0].toInt()*1000.0)/blocks.size)}
            ${((args[0].toInt()*1000.0)/blocks.size).toLong()}
            ---------
        """.trimIndent())

        val timeMilli = 1L

        val finish = blocks.size
        var i = 0
        p.sendMessage(blocks.size.toString())
        p.sendMessage(timeMilli.toString())

        Timer().schedule(0, timeMilli) {
            try {
                object : BukkitRunnable() {
                    override fun run() {
                        if (blocks.isNotEmpty()) {
                            blocks.first().type = Material.AIR
                            blocks.removeFirst()
                        }
                        else {
                            p.sendMessage("Plot escavado com sucesso em ${timeMilli * blocks.size / 10} segundos.")
                            p.sendMessage(i.toString())
                        }
                    }
                }.runTaskLater(Main.plugin, 1)

                if (i++ == finish) {
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