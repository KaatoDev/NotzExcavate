package notzexcavate.notzapi.apis

import notzexcavate.notzapi.utils.MessageU.c
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

object NotzItems {
    /**
     * @param item ItemStack of the item.
     * @param name Name of the item.
     * @param lore Lore of the item.
     * @param enchanted If the item will be enchanted.
     * @return The final item.
     */
    fun buildItem(item: ItemStack, name: String?, lore: List<String>?, enchanted: Boolean?): ItemStack {
        val meta = item.itemMeta
        if (name != null)
            meta.displayName = c("$name&r")

        if (lore != null)
            meta.lore = lore.map { c(it) }

        if (enchanted != null && enchanted) {
            meta.addEnchant(Enchantment.LUCK, 1, false)
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        }

        item.setItemMeta(meta)

        return item
    }

    /**
     * @param item ItemStack of the item.
     * @param name Name of the item.
     * @param lore Lore of the item.
     * @param enchanted If the item will be enchanted.
     * @return The final item.
     */
    fun buildItem(item: ItemStack, name: String?, lore: List<String>?, enchantmentLevel: Int): ItemStack {
        val meta = item.itemMeta
        if (name != null)
            meta.displayName = c("$name&r")

        if (lore != null)
            meta.lore = lore.map { c(it) }

        meta.addEnchant(Enchantment.LUCK, enchantmentLevel, true)
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)

        item.setItemMeta(meta)

        return item
    }

    /**
     * @param id ID of the item.
     * @param name Name of the item.
     * @param lore Lore of the item.
     * @param enchanted If the item will be enchanted.
     * @return The final item.
     */
    fun buildItem(id: Int, name: String, lore: List<String>, enchanted: Boolean): ItemStack {
        @Suppress("DEPRECATION")
        return buildItem(ItemStack(id), name, lore, enchanted)
    }

    /**
     * @param id ID of the item.
     * @param data Data of the item's ID.
     * @param name Name of the item.
     * @param lore Lore of the item.
     * @param enchanted If the item will be enchanted.
     * @return The final item.
     */
    fun buildItem(id: Int, data: Int, name: String, lore: List<String>, enchanted: Boolean): ItemStack {
        @Suppress("DEPRECATION")
        return if (data > 999)
            buildItem(ItemStack(id, 1, 0, data.toByte()), name, lore, enchanted)
        else buildItem(ItemStack(id, 1, data.toShort()), name, lore, enchanted)
    }

    /**
     * @param material Material of the item.
     * @param name Name of the item.
     * @param lore Lore of the item.
     * @param enchanted If the item will be enchanted.
     * @return The final item.
     */
    fun buildItem(material: Material, name: String, lore: List<String>, enchanted: Boolean): ItemStack {
        return buildItem(ItemStack(material), name, lore, enchanted)
    }

    /**
     * @param id ID of the item.
     * @param name Name of the item.
     * @param lore Lore of the item.
     * @param enchanted If the item will be enchanted.
     * @return The final item.
     */
    fun buildItem(id: Int, name: String, lore: List<String>, enchantmentLevel: Int): ItemStack {
        @Suppress("DEPRECATION")
        return buildItem(ItemStack(id), name, lore, enchantmentLevel)
    }

    /**
     * @param id ID of the item.
     * @param data Data of the item's ID.
     * @param name Name of the item.
     * @param lore Lore of the item.
     * @param enchanted If the item will be enchanted.
     * @return The final item.
     */
    fun buildItem(id: Int, data: Int, name: String, lore: List<String>, enchantmentLevel: Int): ItemStack {
        @Suppress("DEPRECATION")
        return if (data > 999)
            buildItem(ItemStack(id, 1, 0, data.toByte()), name, lore, enchantmentLevel)
        else buildItem(ItemStack(id, 1, data.toShort()), name, lore, enchantmentLevel)
    }

    /**
     * @param material Material of the item.
     * @param name Name of the item.
     * @param lore Lore of the item.
     * @param enchanted If the item will be enchanted.
     * @return The final item.
     */
    fun buildItem(material: Material, name: String, lore: List<String>, enchantmentLevel: Int): ItemStack {
        return buildItem(ItemStack(material), name, lore, enchantmentLevel)
    }

    /**
     * @param player The player that will be pick up the head.
     */
    fun getHead(player: Player): ItemStack {
        val head = ItemStack(Material.SKULL_ITEM)
        head.durability = 3.toShort()

        val meta = head.itemMeta as SkullMeta
        meta.setOwner(player.name)
        head.setItemMeta(meta)

        return head
    }

    /**
     * @param data The data id representingthe color.
     * @return A glass with the color selected.
     */
    @Suppress("DEPRECATION")
    fun glass(data: Int): ItemStack {
        val item = ItemStack(Material.STAINED_GLASS_PANE, 1, 0.toShort(), data.toByte())
        val meta = item.itemMeta

        meta.displayName = c("&r")
        meta.lore = emptyList()
        item.setItemMeta(meta)

        return item
    }

    /**
     * @param data The data id representingthe color.
     * @return The color dye selected.
     */
    @Suppress("DEPRECATION")
    fun dye(data: Int): ItemStack {
        val item = ItemStack(351, 1, 0.toShort(), data.toByte())
        val meta = item.itemMeta

        meta.displayName = c("&r")
        meta.lore = emptyList()
        item.setItemMeta(meta)

        return item
    }
}