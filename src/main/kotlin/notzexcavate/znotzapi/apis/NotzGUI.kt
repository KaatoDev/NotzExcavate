package notzexcavate.znotzapi.apis

import notzexcavate.znotzapi.NotzAPI.Companion.itemManager
import notzexcavate.znotzapi.apis.NotzItems.buildItem
import notzexcavate.znotzapi.apis.NotzItems.glass
import notzexcavate.znotzapi.utils.MenuU.addMenu
import notzexcavate.znotzapi.utils.MessageU.c
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack

/**
 * @param player The player to be set.
 * @param rows The quantity of rows of the GUI.
 * @param title The title of the GUI.
 * @property inventory The inventory that has been set.
 */
class NotzGUI(val player: InventoryHolder?, rows: Int, menuName: String?, title: String) {
    private var slots = rows*9
    private var inventory = Bukkit.createInventory(player, slots, c(title))

    init {
        if (menuName != null ) {
            addMenu(menuName, title)
            addMenu(menuName, this)
        }
    }

    /**
     * @return The Inventory GUI.
     */
    fun get(): Inventory {
        return inventory
    }

    /**
     * Set the basics items (back, exits).
     */
    fun setup() {
        setItem(0, "back")
        setItem(slots-5, "exit")
    }

    /**
     * Set the basic item back.
     */
    fun setupBack() {
        setItem(0, "back")
    }

    /**
     * Set the basic item exit.
     */
    fun setupExit() {
        setItem(slots-5, "exit")
    }

    /**
     * @param data The data id color (for glass).
     * @param slots A string that's separated by spaces and hiphens to mark the slots.
     * @return The new inventory contents.
     */
    fun setPanel(data: Int, slots: String) {
        if (slots == "all")
            inventory.contents = inventory.contents.map{ glass(0) }.toTypedArray()

        else slots.split(' ').forEach { slot ->
            if (slot.contains('-')) {
                for (i in slot.split('-')[0].toInt() ..slot.split('-')[1].toInt())
                    inventory.setItem(i, glass(data))

            } else inventory.setItem(slot.toInt(), glass(data))
        }
    }

    /**
     * @param data The data id representing the color (for glass).
     * @param frame If true, sets up a panel frame; if false, fills the entire panel.
     * @return The new inventory contents.
     */
    fun setPanel(data: Int, frame: Boolean) {
        if (frame) {
            when (inventory.contents.size / 9) {
                3 -> setPanel(data, "0-9 17-26")
                4 -> setPanel(data, "0-9 17-18 26-35")
                5 -> setPanel(data, "0-9 17-18 26-27 35-44")
                6 -> setPanel(data, "0-9 17-18 26-27 35-36 44-53")
                else -> setPanel(data, "all")
            }
        } else inventory.contents = inventory.contents.map{ glass(data) }.toTypedArray()
    }

    /**
     * @param item ItemStack to be set.
     */
    fun addItem(item: ItemStack) {
        inventory.addItem(item)
    }

    /**
     * @param items List of ItemStack to be set.
     */
    fun addItems(items: Set<ItemStack>) {
        items.forEach { inventory!!.addItem(it) }
    }

    /**
     * @param material Material to be set.
     */
    fun addItem(material: Material) {
        inventory.addItem(ItemStack(material))
    }

    /**
     * @param name Name of the item contained in the Items.
     */
    fun addItem(name: String) {
        addItem(itemManager.getItem(name))
    }

    /**
     * @param names List of items name that contained in the Items.
     */
    fun addItems(names: String) {
        addItems(names.split(" ").map { itemManager.getItem(it) }.toSet())
    }

    /**
     * @param slot Slot that will be set the item.
     * @param name Name of the item contained in the Items.
     */
    fun setItem(slot: String, name: String) {
        slot.split(" ").forEach { inventory.setItem(it.toInt(), itemManager.getItem(name)) }
    }

    /**
     * @param slots Slot that will be set the item.
     * @param item ItemStack to be set.
     */
    fun setItem(slots: String, item: ItemStack) {
        slots.split(" ").forEach { setItem(it.toInt(), item) }
    }

    /**
     * @param slot Slot that will be set the item.
     * @param name Name of the item contained in the Items.
     */
    fun setItem(slot: Int, name: String) {
        setItem(slot, itemManager.getItem(name))
    }

    /**
     * @param slot Slot that will be set the item.
     * @param item ItemStack to be set.
     */
    fun setItem(slot: Int, item: ItemStack) {
        try {
            inventory.setItem(slot, item)
        } catch (e: ArrayIndexOutOfBoundsException) {
            try {
                inventory.setItem(slot, item)
            } catch (e: ArrayIndexOutOfBoundsException) {
                var fslot = slot

                while (fslot > inventory.size)
                    fslot -= 9

                try {
                    inventory.setItem(fslot, item)
                } catch (e: ArrayIndexOutOfBoundsException) {
                    inventory.setItem(fslot - 1, item)
                }
            }
        }
    }


    /**
     * @param slot Slot that will be set the item.
     * @param material Material item to be set.
     */
    fun setItem(slot: Int, material: Material) {
        setItem(slot, ItemStack(material))
    }

    /**
     * @param slot Slot that will be set the item.
     * @param material Material type of the item.
     * @param name Name of the item.
     * @param lore Lore of the item.
     * @param enchanted If the item will be enchanted.
     */
    fun setItem(slot: Int, material: Material, name: String, lore: List<String>, enchanted: Boolean) {
        setItem(slot, buildItem(material, name, lore, enchanted))
    }

    /**
     * @param slot Slot that will be set the item.
     * @param item ItemStack predefined of the item.
     * @param name Name of the item.
     * @param lore Lore of the item.
     * @param enchanted If the item will be enchanted.
     */
    fun setItem(slot: Int, item: ItemStack, name: String, lore: List<String>, enchanted: Boolean) {
        setItem(slot, buildItem(item, name, lore, enchanted))
    }

    /**
     * @param items List of ItemStack to be set.
     */
    fun addItemsEach(items: MutableList<ItemStack>) {
        inventory.contents.indices.forEach { if (inventory.getItem(it) == null && items.isNotEmpty()) { inventory.setItem(it, items.first()); items.removeFirst() } }
    }
}