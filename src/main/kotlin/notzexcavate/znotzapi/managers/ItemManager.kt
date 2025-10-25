package notzexcavate.znotzapi.managers

import notzexcavate.znotzapi.NotzAPI.Companion.messageManager
import notzexcavate.znotzapi.NotzAPI.Companion.placeholderManager
import notzexcavate.znotzapi.apis.NotzItems.buildItem
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.*

open class ItemManager {
    private val items = HashMap<String, ItemStack>()

    init {
        addItems(hashMapOf(
            "default" to buildItem(Material.PAPER, "&c&lDefault", listOf("&c&oO nome do item atribuído não existe."), true),
            "back" to buildItem(Material.ARROW, "&e&lVoltar", listOf("&7&oClique para voltar."), false),
            "exit" to buildItem(Material.BARRIER, "&4&lSair", listOf("&c&oClique aqui para", "&c&ofechar o menu."), false),
            "save" to buildItem(Material.SLIME_BALL, "&a&lSalvar", listOf("&f&oClique para salvar", "&f&oas modificações."), false),
            "emptypage" to buildItem(Material.GHAST_TEAR, "&f&lPágina vazia", Collections.emptyList(), false),
            "previousoff" to buildItem(Material.ENDER_PEARL, "&c&lComeço da linha", listOf("&7&oNão há mais páginas", "&7&opara retroceder."), false),
            "nextoff" to buildItem(Material.ENDER_PEARL, "&c&lFim da linha", listOf("&7&oNão há mais páginas", "&7&opara avançar."), false)
        ))
    }

    private fun loadDefaultItem(name: String): ItemStack {
        return if (messageManager.messageFile.config.contains("items.$name.material")) {
            buildItemFromFile(name)
        } else getItem("default")
    }

    /**
     * @param name The name of the item to be added.
     */
    fun buildItemFromFile(name: String): ItemStack {
        return getItemFile(name)
    }

    /**
     * @param names The name of the items to be added.
     */
    fun buildItemsFromFile(names: String): Set<ItemStack> {
        val list = mutableListOf<ItemStack>()

        names.split(" ").forEach {
            list.add(getItemFile(it))
        }

        return list.toSet()
    }

    /**
     * @param names The name of the items to be added.
     */
    fun buildItemsFromFile(names: Set<String>): Set<ItemStack> {
        val list = mutableListOf<ItemStack>()

        names.forEach {
            list.add(getItemFile(it))
        }

        return list.toSet()
    }

    /**
     * @param name The name of the item to be added.
     */
    fun buildItemFromFileID(name: String): ItemStack {
        return getItemFileID(name)
    }

    /**
     * @param names The name of the items to be added.
     */
    fun buildItemsFromFileID(names: String): Set<ItemStack> {
        return names.split(" ").map { getItemFileID(it) }.toSet()
    }

    /**
     * @param name The item's alias to be created.
     * @param item The item's ItemStack to be created.
     */
    fun createItem(name: String, item: ItemStack) {
        messageManager.messageFile.config.set("items.$name.material", item.type.name)
        messageManager.messageFile.config.set("items.$name.enchanted", item.itemMeta.hasEnchants())
        messageManager.messageFile.config.set("items.$name.name", item.itemMeta.displayName)
        messageManager.messageFile.config.set("items.$name.lore", item.itemMeta.lore)
        messageManager.messageFile.saveConfig()

        items[name] = getItemFile(name)
    }

    /**
     * @param name The item's alias to be added.
     */
    fun addItem(name: String) {
        items[name] = getItemFile(name)
    }

    /**
     * @param name The item's alias to be added.
     * @param item The item's ItemStack to be added.
     */
    fun addItem(name: String, item: ItemStack) {
        items[name] = item
    }

    /**
     * @param name The item's alias to be added.
     * @param item The item's ItemStack to be added.
     */
    fun addItem(name: String, item: Material) {
        items[name] = ItemStack(item)
    }

    /**
     * @param newItems The items ItemStack to be added.
     */
    fun addItems(newItems: HashMap<String, ItemStack>) {
        items.putAll(newItems)
    }

    /**
     * @return If contains a custom item.
     */
    fun containsItem(item: ItemStack): Boolean {
        return items.containsValue(item)
    }

    /**
     * @return If equals a custom item.
     */
    fun equalsItem(item: ItemStack, name: String): Boolean {
        return items.containsValue(item) && item == getItem(name)
    }

    /**
     * @return The Items HashMap.
     */
    fun getItems(): HashMap<String, ItemStack> {
        return items
    }

    /**
     * @param name Name of the item to be chosen.
     */
    fun getItem(name: String): ItemStack {
        return if (items.containsKey(name))
            items[name]!!
        else if (messageManager.messageFile.config.getString("items.$name.material") != null)
            getItemFile(name)
        else items["default"]!!
    }

    fun cointainsItem(name: String): Boolean {
        return items.containsKey(name)
    }

    fun existsItemInFile(name: String): Boolean {
        return messageManager.messageFile.config.getString("items.$name.material") != null
    }

    private fun getItemFile(name: String): ItemStack {
        val item = if (existsItemInFile(name)) {
            if (Material.entries.map { it.name }.contains(messageManager.messageFile.config.getString("items.$name.material").uppercase())) {
                buildItem(
                    Material.valueOf(messageManager.messageFile.config.getString("items.$name.material")),
                    placeholderManager.set(messageManager.messageFile.config.getString("items.$name.name")),
                    messageManager.messageFile.config.getStringList("items.$name.lore")
                        .map { placeholderManager.set(it) },
                    messageManager.messageFile.config.getBoolean("items.$name.enchanted")
                )

            } else if (messageManager.messageFile.config.getString("items.$name.material").contains(":")) {
                buildItem(
                    messageManager.messageFile.config.getString("items.$name.material").split(":")[0].toInt(),
                    messageManager.messageFile.config.getString("items.$name.material").split(":")[1].toInt(),
                    placeholderManager.set(messageManager.messageFile.config.getString("items.$name.name")),
                    messageManager.messageFile.config.getStringList("items.$name.lore")
                        .map { placeholderManager.set(it) },
                    messageManager.messageFile.config.getBoolean("items.$name.enchanted")
                )

            } else {
                buildItem(
                    messageManager.messageFile.config.getString("items.$name.material").toInt(),
                    placeholderManager.set(messageManager.messageFile.config.getString("items.$name.name")),
                    messageManager.messageFile.config.getStringList("items.$name.lore")
                        .map { placeholderManager.set(it) },
                    messageManager.messageFile.config.getBoolean("items.$name.enchanted")
                )
            }
        } else getItem("default")

        //items[name] = item
        return item
    }

    private fun getItemFileID(name: String): ItemStack {
        return if (existsItemInFile(name)) {
            val id = messageManager.messageFile.config.getString("items.$name.material")
            if (id.contains(":"))
                buildItem(
                    id.split(":")[0].toInt(), id.split(":")[1].toInt(),
                    messageManager.messageFile.config.getString("items.$name.name"),
                    messageManager.messageFile.config.getStringList("items.$name.lore"),
                    messageManager.messageFile.config.getBoolean("items.$name.enchanted")
                )
            else buildItem(
                id.toInt(),
                messageManager.messageFile.config.getString("items.$name.name"),
                messageManager.messageFile.config.getStringList("items.$name.lore"),
                messageManager.messageFile.config.getBoolean("items.$name.enchanted")
            )
        } else getItem("default")
    }

    fun resetBookItems(menuName: String) {
        val pattern = Regex("$menuName[0-9]+")
        items.keys.removeIf { pattern.matches(it) }
    }
}