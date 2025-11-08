package dev.kaato.notzexcavate.events

import com.plotsquared.bukkit.events.PlotClearEvent
import com.plotsquared.bukkit.events.PlotDeleteEvent
import dev.kaato.notzexcavate.NotzExcavate.Companion.papi
import dev.kaato.notzexcavate.managers.ExcavateManager.removeExcavator
import dev.kaato.notzexcavate.managers.ShovelManager.clickShovel
import dev.kaato.notzexcavate.managers.ShovelManager.isShovel
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

class ExcavatorEv : Listener {
    @EventHandler
    fun useShovel(e: PlayerInteractEvent) {
        if (e.item != null && pass(e.item, e.player)) {
            e.isCancelled = true
            if (e.action == Action.RIGHT_CLICK_AIR || e.action == Action.LEFT_CLICK_AIR)
                clickShovel(e.player, papi.getPlot(e.player), e.item, e.action)
        }
    }

    @EventHandler
    fun useShovel(e: BlockBreakEvent) {
        if (e.player.itemInHand != null && isShovel(e.player.itemInHand)) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun plotClear(e: PlotClearEvent) {
        removeExcavator(e.plot)
    }

    @EventHandler
    fun plotDelete(e: PlotDeleteEvent) {
        removeExcavator(e.plot)
    }

    private fun pass(item: ItemStack, p: Player): Boolean {
        val isInPlot = try {
            papi.isInPlot(p)
        } catch (e: Exception) {
            false
        }
        return isShovel(item) && isInPlot
    }
}