package notzexcavate

import com.intellectualcrafters.plot.api.PlotAPI
import notzexcavate.commands.NExcavateC
import notzexcavate.events.ExcavatorEv
import notzexcavate.managers.ExcavateManager.loadExcavators
import notzexcavate.managers.ExcavateManager.restartExcavators
import notzexcavate.managers.ExcavateManager.saveExcavators
import notzexcavate.managers.ExcavateManager.stopExcavators
import notzexcavate.managers.ShovelManager.loadShovels
import notzexcavate.managers.ShovelManager.saveShovels
import notzexcavate.znotzapi.NotzAPI
import notzexcavate.znotzapi.NotzAPI.Companion.messageManager
import notzexcavate.znotzapi.apis.NotzYAML
import notzexcavate.znotzapi.utils.MessageU.c
import notzexcavate.znotzapi.utils.MessageU.send
import org.bukkit.Bukkit
import org.bukkit.Bukkit.getPluginManager
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
    companion object {
        lateinit var pathRaw: String
        lateinit var cf: NotzYAML
        lateinit var msgf: NotzYAML

        lateinit var papi: PlotAPI
        var started = false
    }

    override fun onEnable() {
        papi = PlotAPI()
        pathRaw = dataFolder.absolutePath
        println(pathRaw)

        NotzAPI(this)

        cf = NotzYAML(this, "config")
        msgf = messageManager.messageFile

        server.scheduler.runTaskLater(this, {
            startPlugin()
            Bukkit.getOnlinePlayers().forEach { if (it.hasPermission("notzcrates.admin")) send(it, "&2NotzEscavate &ainiciado!") }
        }, 20)
    }

    override fun onDisable() {
        saveExcavators()
        stopExcavators()
        saveShovels()
    }

    private fun startPlugin() {
        loadExcavators()
        loadShovels()
        restartExcavators()

        regCommands()
        regEvents()
        regTab()

        letters()
        started = true
    }


    private fun regCommands() {
        getCommand("nexcavate").executor = NExcavateC()
    }

    private fun regEvents() {
        getPluginManager().registerEvents(ExcavatorEv(), this)
    }

    private fun regTab() {
        getCommand("nexcavate").tabCompleter = NExcavateC()
    }

    private fun letters() {
        Bukkit.getConsoleSender().sendMessage(
            c("""
                &2Inicializado com sucesso.
                &f┳┓    &6┏┓            
                &f┃┃┏┓╋┓&6┣ ┓┏┏┏┓┓┏┏┓╋┏┓
                &f┛┗┗┛┗┗&6┗┛┛┗┗┗┻┗┛┗┻┗┗ 
            """.trimIndent()
            ))
    }
}
