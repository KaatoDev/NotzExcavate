package notzexcavate.notzapi.gui

import notzexcavate.notzapi.NotzAPI.Companion.itemManager
import notzexcavate.notzapi.apis.NotzGUI
import notzexcavate.notzapi.apis.NotzItems.buildItem
import notzexcavate.notzapi.apis.NotzItems.getHead
import notzexcavate.notzapi.utils.EventU.setFunction
import notzexcavate.notzapi.utils.MenuU.openMenu
import notzexcavate.notzapi.utils.MenuU.resetBookPages
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

/**
 * Sets the page buttons.
 * @property player Player to be set.
 * @param rows Quantity of rows (recommended 3 or 4 at least).
 * @param title The title of the pages.
 * @property pageCount Quantity of pages.
 * @property setRaw If it will be set only the page buttons or not.
 * @property showHead If it will be the player head on the top right of the page.
 */
open class Book(private val player: InventoryHolder?, rows: Int, lastPageRows: Int?, private val menuName: String, title: String, private val pageCount: Int, private val setRaw: Boolean, private val showHead: Boolean) {
    val pages = HashMap<Int, NotzGUI>()

    init {
        var prox = false
        val ti = title.map { if (it == '&') { prox = true; "" } else if (prox) {prox = false; ""} else it}.joinToString(separator = "")

        itemManager.resetBookItems("${menuName}pageon")
        itemManager.resetBookItems("${menuName}pageoff")
        resetBookPages("menuName")

        itemManager.addItem("${menuName}previouson", buildItem(Material.EYE_OF_ENDER, "&a&lPágina anterior", listOf("&7&oClique para retroceder à", "&7&opágina anterior de", "&7&o$ti."), false))
        itemManager.addItem("${menuName}nexton", buildItem(Material.EYE_OF_ENDER, "&a&lPágina posterior", listOf("&7&oClique para avançar à", "&7&opágina posteior de", "&7&o$ti."), false))

        for (i in 1..pageCount) {
            itemManager.addItem("${menuName}pageon$i", buildItem(Material.NETHER_STAR, "&e&lPágina $i", listOf("&7&oVocê está nesta página!"), true))
            itemManager.addItem("${menuName}pageoff$i", buildItem(Material.PAPER, "&e&lPágina $i", listOf("&7&oClique para alterar para", "&7&oa página $i de "), false))
        }

        for (i in 1..pageCount) {
            if (i != pageCount || lastPageRows == null)
                pages[i] = NotzGUI(player, rows, "$menuName$i", "$title #$i")
            else pages[i] = NotzGUI(player, lastPageRows, "$menuName$i", "$title #$i")


            val pageoff: (Player) -> Unit = {
                val tt = it.openInventory.title

                openMenu(it, tt.replace(tt.substring(tt.lastIndexOf("#")), "#$i"))
            }

            setFunction("${menuName}pageoff$i", pageoff)

            if (setRaw)
                setPageRaw(i)
            else setPage(i)
        }

        val previouson: (Player) -> Unit = {
            val tt = it.openInventory.title

            val to = if (tt[tt.length-1] == '#')
                tt.last().digitToInt()
            else tt.substring(tt.length-1, tt.length).toInt() -1

            openMenu(it, tt.replace(tt.substring(tt.lastIndexOf("#")), "#$to"))
        }

        val nexton: (Player) -> Unit = {
            val tt = it.openInventory.title

            val to: Int = if (tt[tt.length-1] == '#')
                tt.last().digitToInt()
            else tt.substring(tt.length-1, tt.length).toInt() +1

            openMenu(it, tt.replace(tt.substring(tt.lastIndexOf("#")), "#$to"))
        }

        setFunction("${menuName}previouson", previouson)
        setFunction("${menuName}nexton", nexton)
    }

    /**
     * @param page The page to be opened.
     * @return The Inventory page.
     */
    fun page(page: Int): Inventory {
        return pages[page]!!.get()
    }

    /**
     * @param page The page to be opened.
     * @return The NotzGUI page.
     */
    fun pageRaw(page: Int): NotzGUI {
        return pages[page]!!
    }

    /**
     * Sets the entire page with the buttons.
     * @param i Page to be set.
     */
    private fun setPage(i: Int) {
        pages[i]!!.setup()
        pages[i]!!.setPanel(0, true)
        setPageRaw(i)
    }

    /**
     * Sets the page buttons.
     * @param i Page to be set.
     */
    private fun setPageRaw(i: Int) {
        pages[i]!!.setup()

        if (showHead && player != null)
            pages[i]!!.setItem(8, getHead(player as Player))

        pages[i]!!.setItem(1, if (itemManager.cointainsItem("${menuName}pageoff${i-1}")) "${menuName}previouson" else "previousoff")
        pages[i]!!.setItem(2, if (itemManager.cointainsItem("${menuName}pageoff${i-2}")) "${menuName}pageoff${i-2}" else "emptypage")
        pages[i]!!.setItem(3, if (itemManager.cointainsItem("${menuName}pageoff${i-1}")) "${menuName}pageoff${i-1}" else "emptypage")
        pages[i]!!.setItem(4, "${menuName}pageon$i")

        pages[i]!!.setItem(5, if (itemManager.cointainsItem("${menuName}pageoff${i+1}")) "${menuName}pageoff${i+1}" else "emptypage")
        pages[i]!!.setItem(6, if (itemManager.cointainsItem("${menuName}pageoff${i+2}")) "${menuName}pageoff${i+2}" else "emptypage")
        pages[i]!!.setItem(7, if (itemManager.cointainsItem("${menuName}pageoff${i+1}")) "${menuName}nexton" else "nextoff")
    }
}