package notzexcavate

import notzexcavate.commands.ExcavateC
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
    companion object {
        lateinit var plugin: JavaPlugin
    }

    override fun onEnable() {
        plugin = this
        getCommand("excavate").executor = ExcavateC()
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
