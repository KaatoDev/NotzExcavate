package notzexcavate.znotzapi.gui

import notzexcavate.znotzapi.NotzAPI.Companion.itemManager
import notzexcavate.znotzapi.apis.NotzGUI
import notzexcavate.znotzapi.apis.NotzItems.glass
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack

/**
 * Sets the page buttons.
 * @param player Player to be set.
 * @param title The title of the pages.
 * @property contents The items contents in the "inventory".
 */
open class ChestPages(player: InventoryHolder?, menuName: String, title: String, val itemsForPage: Int, val contents: List<ItemStack>, showHead: Boolean) {
    val book: Book
    private val p = player
    var pages = contents.size / itemsForPage

    init {
        if (contents.size % itemsForPage > 0)
            pages++

        val rows = 2 + contents.size / (itemsForPage/4) + if (contents.size % (itemsForPage/4) > 0) 1 else 0
        val lastContentsSize = (contents.size - (pages-1)*itemsForPage)
        val lastRows = 2 + lastContentsSize / (itemsForPage/4) + if (lastContentsSize % (itemsForPage/4) > 0) 1 else 0

        book = if (pages == 1)
            Book(p, rows, null, menuName, title, pages, itemsForPage > 28, showHead)
        else Book(p, 6, lastRows, menuName, title, pages, itemsForPage > 28, showHead)

        if (itemsForPage > 28)
            "8 45 46 47 48 50 51 52 53".split(" ").forEach { setItemPage(it.toInt(), glass(0)) }

        book.pages.keys.forEach { page ->
            var slot = 9
            if (page * itemsForPage - 1 < contents.size)
                contents.slice(page * itemsForPage - itemsForPage until page * itemsForPage).forEach {
                    while (book.pages[page]?.get()!!.firstEmpty() > slot)
                        slot++

                    if (slot < book.pages[page]?.get()!!.size-1)
                        book.pages[page]?.setItem(slot, it)
                    slot++
                }

            else contents.slice(page * itemsForPage - itemsForPage until contents.size).forEach {
                while (book.pages[page]?.get()!!.firstEmpty() > slot)
                    slot++

                if (slot < book.pages[page]?.get()!!.size)
                    book.pages[page]?.setItem(slot++, it)
            }
        }
    }

    /**
     * @param page Page to be opened.
     * @return The Inventory.
     */
    fun page(page: Int): Inventory {
        return book.page(page)
    }

    /**
     * @param page Page to be opened.
     * @return The Inventory.
     */
    fun pageRaw(page: Int): NotzGUI {
        return book.pageRaw(page)
    }

    fun setItemPage(page: Int, slot: Int, item: ItemStack) {
        try {
            book.pageRaw(page).setItem(slot, item)
        } catch (e: ArrayIndexOutOfBoundsException) {
            book.pageRaw(page).setItem(slot - book.pageRaw(page).get().size, item)
        }
    }

    fun setItemPage(slot: Int, item: ItemStack) {
        book.pages.values.forEach {
            try {
                it.setItem(slot, item)
            } catch (e: ArrayIndexOutOfBoundsException) {
                var fslot = slot

                while (fslot > it.get().size)
                    fslot -= 9

                try {
                    it.setItem(fslot, item)
                } catch (e: ArrayIndexOutOfBoundsException) {
                    it.setItem(fslot-1, item)
                }
            }
        }
    }

    fun setItemPage(page: Int, slot: Int, item: String) {
        setItemPage(page, slot, itemManager.getItem(item))
    }

    fun setItemPage(slot: Int, item: String) {
        setItemPage(slot, itemManager.getItem(item))
    }
}