package notzexcavate.commands

import notzexcavate.entities.Shovel
import notzexcavate.notzapi.utils.MessageU.sendHeader
import notzexcavate.notzapi.utils.OthersU.isntAdmin
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import java.util.*

class NExcavateC : TabExecutor {
    lateinit var player: Player

    override fun onCommand(p: CommandSender?, command: Command?, label: String?, argss: Array<out String>?): Boolean {
        if (p !is Player)
            return false
        player = p

        if (isntAdmin(p)) return true

        if (argss == null ) {
            help()
            return true
        }

        val args = argss.map { var arg = it; if (!it.contains('&')) arg = it.lowercase(); arg }.toTypedArray()



        return true
    }

    override fun onTabComplete(p: CommandSender?, command: Command?, alias: String?, args: Array<out String>?): MutableList<String> {
        return Collections.emptyList()
    }

    fun help() {
        sendHeader(player, """
            &eUtilize &f/&7[&enexcavator &7|| &enex&7] +
            &f<&eShovel&f> &f- Para entrar na edição da Shovel.
            &eexcavator &7- Entra no menu de comandos dos Excavators.
            &ecreate &f<&e&f> &7- Cria uma shovel nova.
            &estatus &7- Vê o status do Excavator da plot. 
            &erestart &7- Reinicia os escavadores. 
            &elist &- Vê a lista de shovels existentes.
            &esave &7- Salva os Excavators e as Shovels. 
        """.trimIndent())
    }

    private fun helpExcavator() {
        sendHeader(player, """
            Utilize: &f/&enex excavator&7 +
            &7+ &eremove &7- Remove o excavator existente da plot.
            &7+ &estop &7- Para o excavator da plot.
            &7+ &elist &7- Vê a lista de excavators ativos ou pausados.
            &7+ 
        """.trimIndent())
    }

    private fun helpShovel(shovel: Shovel) {
        sendHeader(player, """
            Utilize: &f/&enex ${shovel.name}&7 +
            &7+ &edelete &7- Deleta a Shovel.
            &7+ &esetDisplay &f<&edisplay&f> &7- Altera o display da Shovel.
            &7+ &esetDuration &f<&eminutes&f> &7- Altera o tempo da Shovel (em minutos).
            &7+ &esetMaterial &f(&ematerial&f) &7- Altera o material da Shovel.
            &7+ &eget &f<&eplayer&f) &7- Recebe ou dá a Shovel à um player.
        """.trimIndent())
    }
}