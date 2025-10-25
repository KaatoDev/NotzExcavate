package notzexcavate.znotzapi

import notzexcavate.znotzapi.apis.NotzYAML
import notzexcavate.znotzapi.managers.ItemManager
import notzexcavate.znotzapi.managers.MessageManager
import notzexcavate.znotzapi.managers.PlaceholderManager
import notzexcavate.znotzapi.utils.EventU.setFunction
import notzexcavate.znotzapi.utils.MenuU.getLastMenu
import notzexcavate.znotzapi.utils.MessageU.c
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class NotzAPI(javaPlugin: JavaPlugin) {
    companion object {
        lateinit var itemManager: ItemManager
        lateinit var messageManager: MessageManager
        lateinit var placeholderManager: PlaceholderManager
        lateinit var plugin: JavaPlugin
    }

    init {
        plugin = javaPlugin

        messageManager = MessageManager(NotzYAML(plugin, "messages"))

        itemManager = ItemManager()

        placeholderManager = PlaceholderManager()
        letters()

        val back: (Player) -> Unit = { p -> getLastMenu(p)}
        val exit: (Player) -> Unit = { p -> p.closeInventory()}

        setFunction("back", back)
        setFunction("exit", exit)
    }

    private fun letters() {
        Bukkit.getConsoleSender().sendMessage((placeholderManager.set("{prefix} &2Inicializado com sucesso.)").plus(
                c("\n&f┳┓    &6┏┓┏┓┳"
                + "\n&f┃┃┏┓╋┓&6┣┫┃┃┃"
                + "\n&f┛┗┗┛┗┗&6┛┗┣┛┻"))))
    }
}
