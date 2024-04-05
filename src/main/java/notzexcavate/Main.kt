package notzexcavate

import com.intellectualcrafters.plot.api.PlotAPI
import notzexcavate.commands.NExcavateC
import notzexcavate.events.ExcavatorEv
import notzexcavate.managers.ExcavateManager.loadExcavators
import notzexcavate.managers.ExcavateManager.saveExcavators
import notzexcavate.managers.ShovelManager.loadShovels
import notzexcavate.managers.ShovelManager.saveShovels
import notzexcavate.notzapi.NotzAPI
import notzexcavate.notzapi.NotzAPI.Companion.messageManager
import notzexcavate.notzapi.apis.NotzYAML
import notzexcavate.notzapi.utils.MessageU.c
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
    companion object {
        lateinit var plugin: JavaPlugin
        lateinit var pathRaw: String
        lateinit var cf: NotzYAML
        lateinit var sqlf: NotzYAML
        lateinit var msgf: NotzYAML

        val papi = PlotAPI()
        var started = false
    }

    override fun onEnable() {
        plugin = this
        pathRaw = dataFolder.absolutePath

        NotzAPI(this)

        cf = NotzYAML(this, "config")
        sqlf = NotzYAML(this, "notzExcavator")
        msgf = messageManager.messageFile
    }

    override fun onDisable() {
        saveExcavators()
        saveShovels()
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
    }

    private fun regEvents() {
        Bukkit.getPluginManager().registerEvents(ExcavatorEv(), this)
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
