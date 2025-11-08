package dev.kaato.notzexcavate

import com.intellectualcrafters.plot.api.PlotAPI
import dev.kaato.notzapi.NotzAPI.addPlugin
import dev.kaato.notzapi.NotzAPI.removePlugin
import dev.kaato.notzapi.apis.NotzYAML
import dev.kaato.notzapi.managers.ItemManager
import dev.kaato.notzapi.managers.MessageManager
import dev.kaato.notzapi.managers.NotzManager
import dev.kaato.notzapi.managers.PlaceholderManager
import dev.kaato.notzapi.utils.*
import dev.kaato.notzapi.utils.MessageU.Companion.sendHoverURL
import dev.kaato.notzexcavate.commands.ExcavateC
import dev.kaato.notzexcavate.commands.NExcavateC
import dev.kaato.notzexcavate.events.ExcavatorEv
import dev.kaato.notzexcavate.managers.ExcavateManager.loadExcavators
import dev.kaato.notzexcavate.managers.ExcavateManager.saveExcavators
import dev.kaato.notzexcavate.managers.ExcavateManager.stopExcavators
import dev.kaato.notzexcavate.managers.ShovelManager.loadShovels
import dev.kaato.notzexcavate.managers.ShovelManager.saveShovels
import org.bukkit.Bukkit
import org.bukkit.Bukkit.getConsoleSender
import org.bukkit.Bukkit.getPluginManager
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable

class NotzExcavate : JavaPlugin() {
    companion object {
        lateinit var pathRaw: String

        lateinit var cf: NotzYAML
        lateinit var msgf: NotzYAML

        lateinit var plugin: JavaPlugin
        lateinit var napi: NotzManager
        lateinit var itemManager: ItemManager
        lateinit var messageManager: MessageManager
        lateinit var placeholderManager: PlaceholderManager
        lateinit var eventU: EventU
        lateinit var mainU: MainU
        lateinit var menuU: MenuU
        lateinit var messageU: MessageU
        lateinit var othersU: OthersU

        lateinit var papi: PlotAPI
        var started = false
    }

    override fun onEnable() {
        if (getPluginManager().getPlugin("PlotSquared") != null) {
            pathRaw = dataFolder.absolutePath
            papi = PlotAPI()
            plugin = this
            napi = addPlugin(plugin)

            messageManager = napi.messageManager
            itemManager = napi.itemManager
            placeholderManager = napi.placeholderManager
            eventU = napi.eventU
            mainU = napi.mainU
            menuU = napi.menuU
            messageU = napi.messageU
            othersU = napi.othersU

            cf = NotzYAML(this, "config")
            msgf = messageManager.messageFile


            object : BukkitRunnable() {
                override fun run() {
                    startPlugin()
                    Bukkit.getOnlinePlayers().forEach { if (it.hasPermission("notzcrates.admin")) messageU.send(it, "&2NotzEscavate &ainitialized!") }
                }
            }.runTaskLater(this, 4 * 20L)
        }
    }

    override fun onDisable() {
        saveExcavators()
        stopExcavators()
        saveShovels()
        removePlugin(plugin)
    }

    private fun startPlugin() {
        loadExcavators()
        loadShovels()

        regCommands()
        regEvents()
        regTab()

        letters()
        started = true
    }


    private fun regCommands() {
        getCommand("nexcavate").executor = NExcavateC()
        getCommand("excavate").executor = ExcavateC()
    }

    private fun regEvents() {
        getPluginManager().registerEvents(ExcavatorEv(), this)
    }

    private fun regTab() {
        getCommand("nexcavate").tabCompleter = NExcavateC()
    }

    private fun getPlotPlugin() {
    }

    private fun letters() {
        messageU.send(
            getConsoleSender(), """
                &2Inicializado com sucesso.
                &f┳┓    &6┏┓            
                &f┃┃┏┓╋┓&6┣ ┓┏┏┏┓┓┏┏┓╋┏┓
                &f┛┗┗┛┗┗&6┗┛┛┗┗┗┻┗┛┗┻┗┗ 
            """.trimIndent()
        )
        Bukkit.getOnlinePlayers().forEach {
            if (othersU.isAdmin(it)) {
                it.sendMessage(" ")
                sendHoverURL(it, messageU.set("{prefix}") + " &6Para mais plugins como este, acesse o &e&onosso site&6!", arrayOf("&b&okaato.dev/plugins"), "https://kaato.dev/plugins"); it.sendMessage(" ")
                sendHoverURL(it, messageU.set("{prefix}") + " &6For more plugins like this, visit &e&oour website&6!", arrayOf("&b&okaato.dev/plugins"), "https://kaato.dev/plugins"); it.sendMessage(" ")
            }
        }
    }
}
