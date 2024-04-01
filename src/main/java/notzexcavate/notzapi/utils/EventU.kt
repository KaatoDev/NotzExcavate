package notzexcavate.notzapi.utils

import notzexcavate.notzapi.NotzAPI.Companion.itemManager
import notzexcavate.notzapi.gui.ChestPages
import notzexcavate.notzapi.utils.MenuU.getLastMenu
import notzexcavate.notzapi.utils.MessageU.c
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

object EventU {
    val tasks = hashMapOf<ItemStack, (Player) -> Unit>(
        itemManager.getItem("back") to { p -> getLastMenu(p)},
        itemManager.getItem("exit") to { p -> p.closeInventory()}
    )

    fun containsTask(item: ItemStack): Boolean {
        return tasks.containsKey(item)
    }

    fun setFunction(item: ItemStack, task: (Player) -> Unit) {
        tasks[item] = task
    }

    fun setFunction(item: String, task: (Player) -> Unit) {
        setFunction(itemManager.getItem(item), task)
    }

    fun getFunction(item: ItemStack, any: Player) {
        if (itemManager.getItem("default") != item)
            tasks[item]!!.invoke(any)
    }

    /**
     * @param event The InventoryClickEvent.
     * @param chestPages The ChestPages to be opened.
     */
    fun switchPage(event: InventoryClickEvent, chestPages: ChestPages): Boolean {
        if (event.slot == 7 && event.currentItem == itemManager.getItem("nexton"))
            event.whoClicked.openInventory(
                chestPages.page(
                    event.clickedInventory.getItem(5).itemMeta.displayName.substring(
                        11,
                        event.clickedInventory.getItem(5).itemMeta.displayName.length - 2
                    ).toInt()
                )
            )

        else if (event.slot == 1 && event.currentItem == itemManager.getItem("previouson"))
            event.whoClicked.openInventory(
                chestPages.page(
                    event.clickedInventory.getItem(3).itemMeta.displayName.substring(
                        11,
                        event.clickedInventory.getItem(3).itemMeta.displayName.length - 2
                    ).toInt()
                )
            )

        else if (event.currentItem.itemMeta.displayName.contains(c("&e&lPágina ")))
            event.whoClicked.openInventory(chestPages.page(event.currentItem.itemMeta.displayName.substring(11, event.currentItem.itemMeta.displayName.length - 2).toInt()))
        else return false
        return true
    }
}