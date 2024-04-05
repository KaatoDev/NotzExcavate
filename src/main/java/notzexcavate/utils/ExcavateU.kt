package notzexcavate.utils

import com.intellectualcrafters.plot.`object`.Plot
import com.intellectualcrafters.plot.`object`.PlotId
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block

object ExcavateU {
    private val plotBlocksList = hashMapOf<PlotId, MutableList<Block>>()

    fun getBlocks(plot: Plot): MutableList<Block> {
        if (plotBlocksList.containsKey(plot.id))
            return plotBlocksList[plot.id]!!

        val blocks = mutableListOf<Block>()
        val region = plot.regions.first()

        var reverse = false

        for (y in 130 downTo 1) // region.minY
            for (x in if (y%2==0) region.minX..region.maxX else region.maxX downTo region.minX) {
                for (z in if (reverse) region.minZ..region.maxZ else region.maxZ downTo region.minZ) {
                    val block = Location(Bukkit.getWorld(plot.worldName), x.toDouble(), y.toDouble(), z.toDouble()).block
                    if (block.type == Material.DIRT || block.type == Material.GRASS) blocks.add(block)
                }
                reverse = !reverse
            }

        plotBlocksList[plot.id] = blocks
        return blocks
    }
}