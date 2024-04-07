package notzexcavate.znotzapi.utils

import notzexcavate.znotzapi.NotzAPI.Companion.itemManager
import notzexcavate.znotzapi.apis.NotzGUI
import notzexcavate.znotzapi.gui.Book
import notzexcavate.znotzapi.gui.ChestPages
import notzexcavate.znotzapi.utils.EventU.containsTask
import notzexcavate.znotzapi.utils.EventU.getFunction
import notzexcavate.znotzapi.utils.MessageU.c
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object MenuU {
    var menus = hashMapOf<String, String>()
    var menusGUI = hashMapOf<String, NotzGUI>()
    var itemMenus = hashMapOf<ItemStack, NotzGUI>()
    var lastMenu = hashMapOf<Player, MutableList<NotzGUI>>()

    fun createLastMenu(p: Player) {
        lastMenu[p] = mutableListOf()
    }

    fun removeLastMenu(p: Player) {
        lastMenu.remove(p)
    }

    private fun addLastMenu(p: Player, gui: NotzGUI) {
        if (lastMenu[p] == null)
            lastMenu[p] = mutableListOf()

        if (gui.get().title.contains("#") && !gui.get().title.contains("#1"))
            return

        if (!lastMenu[p]!!.contains(gui))
            lastMenu[p]!!.add(gui)
    }


    fun getLastMenu(p: Player) {
        try {
            if (lastMenu[p]!!.isEmpty())
                return

            val anterior = if (lastMenu[p]!!.filter { it.get().title == p.openInventory.topInventory.title }.size > 1) {
                val ind =
                    lastMenu[p]!!.indices.find { lastMenu[p]!![it].get().title == p.openInventory.topInventory.title }!!
                lastMenu[p]!!.removeAll(lastMenu[p]!!.slice(ind..<lastMenu[p]!!.size))
                lastMenu[p]!![ind - 1].get()

            } else {
                try {
                    var inv = lastMenu[p]!![lastMenu[p]!!.size - 2].get()

                    while (p.openInventory.topInventory.title == inv.title) {
                        lastMenu[p]!!.removeAt(lastMenu[p]!!.size - 2)
                        inv = lastMenu[p]!![lastMenu[p]!!.size - 2].get()
                    }
                    inv

                } catch (e: IndexOutOfBoundsException) {
                    var inv = lastMenu[p]!![lastMenu[p]!!.size - 1].get()

                    while (p.openInventory.topInventory.title == inv.title) {
                        lastMenu[p]!!.removeAt(lastMenu[p]!!.size - 1)
                        inv = lastMenu[p]!![lastMenu[p]!!.size - 1].get()
                    }
                    inv
                }
            }

            p.openInventory(anterior)

        } catch (e: Exception) {
            p.closeInventory()
        }
    }

    fun resetLastMenu(p: Player) {
        lastMenu[p] = mutableListOf()
    }

    fun resetBookPages(menuName: String) {
        val pattern = Regex("$menuName[0-9]+")
        menus.keys.removeIf { pattern.matches(it) }
        menusGUI.keys.removeIf { pattern.matches(it) }
    }

    /**
     * @param title The title of the NotzGUI.
     */
    fun contains(title: String): Boolean {
        return menus.containsValue(title)
    }

    /**
     * @param menuName The name of the NotzGUI for the reference.
     * @param title The title of the NotzGUI.
     */
    fun containsMenu(menuName: String, title: String): Boolean {
        return menus[menuName]!!.contains(title)
    }

    /**
     * @param menuName The name of the NotzGUI for the reference.
     * @param title The title of the NotzGUI.
     */
    fun equalsMenu(menuName: String, title: String): Boolean {
        return menus[menuName].equals(title)
    }

    fun containsItemMenu(item: ItemStack): Boolean {
        return itemMenus.containsKey(item)
    }

    /**
     * @param item The item "button".
     * @param menu The NotzGUI of the button.
     */
    fun addItemMenu(item: ItemStack, menu: NotzGUI) {
        itemMenus[item] = menu
    }

    /**
     * @param item The item "button".
     * @param menu The Book of the button.
     */
    fun addItemMenu(item: ItemStack, menu: Book) {
        itemMenus[item] = menu.pageRaw(1)
    }

    /**
     * @param item The item "button".
     * @param menu The ChestPages of the button.
     */
    fun addItemMenu(item: ItemStack, menu: ChestPages) {
        itemMenus[item] = menu.pageRaw(1)
    }

    fun getMenuByTitle(title: String): String {
        return menus.keys.find { menus[it] == title }!!
    }

    fun getNotzGUI(menuName: String): NotzGUI {
        return menusGUI[menuName]!!
    }

    fun openInv(player: Player, gui: NotzGUI) {
        val inv = gui.get()
        player.openInventory(inv)
    }


    fun openInv(player: Player, menu: String) {
        var tt = menu
        if (!menusGUI.containsKey(menu))
            tt = getMenuByTitle(menu)

        val inv = menusGUI[tt]!!.get()

        player.openInventory(inv)
    }

    /**
     * @param player The player that will receive the menu.
     * @param gui The NotzGUI that will be opened.
     */
    fun openMenu(player: Player, gui: NotzGUI) {
        val inv = gui.get()

        addLastMenu(player, gui)
        player.openInventory(inv)
    }

    /**
     * @param player The player that will receive the menu.
     * @param gui The NotzGUI that will be opened.
     */
    fun openMenu(player: Player, menu: String) {
        var tt = menu
        if (!menusGUI.containsKey(menu))
            tt = getMenuByTitle(menu)

        val inv = menusGUI[tt]!!.get()

        addLastMenu(player, menusGUI[tt]!!)
        player.openInventory(inv)
    }

    /**
     * @param player The player that will receive the menu.
     * @param button The item "button" that was clicked.
     */
    fun openMenu(player: Player, button: ItemStack): Boolean {
        if (containsTask(button)) {

            if (itemManager.getItem("default") != button)
                getFunction(button, player)

            return true
        } else if (containsItemMenu(button)) {
            addLastMenu(player, itemMenus[button]!!)
            player.openInventory(itemMenus[button]!!.get())
        } else return false
        return false
    }

    /**
     * @param menuName The name of the NotzGUI for the reference.
     * @param title The title of the NotzGUI.
     */
    fun addMenu(menuName: String, title: String) {
        menus[menuName] = c(title)
    }

    /**
     * @param menuName The name of the NotzGUI for the reference.
     * @param title The title of the NotzGUI.
     */
    fun addMenu(menuName: String, menu: NotzGUI) {
        menusGUI[menuName] = menu
    }

    /**
     * @param menuList The names and titles of the NotzGUI for the reference.
     */
    fun addMenu(menuList: HashMap<String, String>) {
        menus.putAll(menuList)
    }
}