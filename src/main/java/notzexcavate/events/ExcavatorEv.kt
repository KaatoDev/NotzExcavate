package notzexcavate.events

import com.plotsquared.bukkit.events.PlotClearEvent
import com.plotsquared.bukkit.events.PlotDeleteEvent
import notzexcavate.Main.Companion.papi
import notzexcavate.managers.ExcavateManager.removeExcavator
import notzexcavate.managers.ShovelManager.clickShovel
import notzexcavate.managers.ShovelManager.isShovel
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

class ExcavatorEv : Listener {
    @EventHandler
    fun useShovel(e: PlayerInteractEvent) {
        if (e.item != null && pass(e.item, e.player))
            clickShovel(e.player, papi.getPlot(e.player), e.item, e.action)
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
        return isShovel(item) && papi.isInPlot(p)
    }
}