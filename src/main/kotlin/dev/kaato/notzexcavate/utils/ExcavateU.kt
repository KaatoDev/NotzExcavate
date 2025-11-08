package dev.kaato.notzexcavate.utils

import com.intellectualcrafters.plot.`object`.Plot
import dev.kaato.notzexcavate.NotzExcavate.Companion.cf
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block

object ExcavateU {
    val all_allowed = cf.config.getStringList("allowed-blocks").map(String::uppercase)

    fun getBlocks(plot: Plot, allowed: List<Material>, blocked: List<Material>): MutableList<Block> {
        val blocks = mutableListOf<Block>()
        val region = plot.regions.first()
        val full = all_allowed[0] == "ALL"
        val allowedBlocks = allowed.map(Material::name).ifEmpty { all_allowed }.toMutableList()
        allowedBlocks.removeAll(blocked.map(Material::name))

        var reverse = false

        for (y in 130 downTo 1) // region.minY
            for (x in if (y % 2 == 0) region.minX..region.maxX else region.maxX downTo region.minX) {
                for (z in if (reverse) region.minZ..region.maxZ else region.maxZ downTo region.minZ) {
                    val block = Location(Bukkit.getWorld(plot.worldName), x.toDouble(), y.toDouble(), z.toDouble()).block
                    if (full || allowedBlocks.contains(block.type.name)) blocks.add(block)
                }
                reverse = !reverse
            }
        return blocks
    }
}