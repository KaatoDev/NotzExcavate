package notzexcavate.notzapi

import notzexcavate.notzapi.apis.NotzYAML
import notzexcavate.notzapi.managers.ItemManager
import notzexcavate.notzapi.managers.MessageManager
import notzexcavate.notzapi.managers.PlaceholderManager
import notzexcavate.notzapi.utils.EventU.setFunction
import notzexcavate.notzapi.utils.MenuU.getLastMenu
import notzexcavate.notzapi.utils.MessageU.c
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class NotzAPI(messageFile: NotzYAML) {
    companion object {
        lateinit var itemManager: ItemManager
        lateinit var messageManager: MessageManager
        lateinit var placeholderManager: PlaceholderManager
    }

    init {
        messageManager = MessageManager(messageFile)

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
