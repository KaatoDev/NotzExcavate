package notzexcavate.commands

import notzexcavate.Main.Companion.papi
import notzexcavate.entities.Shovel
import notzexcavate.managers.ExcavateManager.getAllExcavators
import notzexcavate.managers.ExcavateManager.getCompletedExcavators
import notzexcavate.managers.ExcavateManager.getExcavatorStatus
import notzexcavate.managers.ExcavateManager.getRunningExcavators
import notzexcavate.managers.ExcavateManager.removeExcavator
import notzexcavate.managers.ExcavateManager.restartExcavators
import notzexcavate.managers.ExcavateManager.saveExcavators
import notzexcavate.managers.ExcavateManager.stopExcavator
import notzexcavate.managers.ShovelManager
import notzexcavate.managers.ShovelManager.createShovel
import notzexcavate.managers.ShovelManager.deleteShovel
import notzexcavate.managers.ShovelManager.getShovel
import notzexcavate.managers.ShovelManager.getShovels
import notzexcavate.znotzapi.utils.MessageU.formatDate
import notzexcavate.znotzapi.utils.MessageU.send
import notzexcavate.znotzapi.utils.MessageU.sendHeader
import notzexcavate.znotzapi.utils.OthersU.isntAdmin
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import java.text.ParseException
import java.util.*

class NExcavateC : TabExecutor {
    lateinit var player: Player

    override fun onCommand(p: CommandSender?, command: Command?, label: String?, argss: Array<out String>?): Boolean {
        if (p !is Player)
            return false
        player = p

        if (isntAdmin(p)) return true

        if (argss == null || argss.isEmpty()) {
            help()
            return true
        }

        val args = argss.map { var arg = it; if (!it.contains('&')) arg = it.lowercase(); arg }.toTypedArray()
        val shovel = getShovel(args[0])

// -----------------------------------
        when (args.size) {
            1 -> {
                when (args[0]) {
                    "create" -> send(p, "&eUtilize: &f/&enexcavator create &f<&ename&f> &f<&edisplay&f>")
                    "excavator" -> helpExcavator()
                    "list" -> send(p, getShovels().toString())
                    "restart" -> restartExcavators(p)
                    "save" -> saveExcavators(p)
                    "status" -> {
                        if (papi.isInPlot(p))
                            getExcavatorStatus(p, papi.getPlot(p))
                        else send(p, "&cVocê precisa estar numa plot!")
                    }
                    else -> if (shovel != null) helpShovel(shovel) else help()
                }
            }
// -----------------------------------
            2 -> {
                when (args[0]) {
                    "create" -> send(p, "&eUtilize: &f/&enexcavator create &f<&ename&f> &f<&edisplay&f>")

                    "excavator" -> {
                        if (!papi.isInPlot(p)) {
                            send(p, "&cVocê precisa estar numa plot!")
                            return true
                        }

                        when (args[1]) {
                            "all" -> getAllExcavators(p)
                            "list" -> getRunningExcavators(p)
                            "completed" -> getCompletedExcavators(p)
                            "remove" ->
                                if (removeExcavator(papi.getPlot(p)))
                                    send(p, "&eExcavator removido da plot com sucesso!")
                                else send(p, "&cNão foi possível remover o Excavator!")

                            "stop" -> stopExcavator(p, papi.getPlot(p))
                            else -> helpExcavator()
                        }
                    }

                    else -> if (shovel != null) when (args[1]) {
                        "delete" -> {
                            if (deleteShovel(shovel))
                                send(p, "&eShovel &f${args[0]}&e deletada com sucesso.")
                            else send(p, "&cNão foi possível excluir a Shovel ${args[0]}&c.")
                        }
                        "get" -> ShovelManager.giveShovel(p, shovel)
                        "setdisplay" -> send(p, "&eUtilize &f/&enex &f${shovel.name}&e setDisplay &f<&edisplay&f> ")
                        "setduration" -> send(p, "&eUtilize &f/&enex &f${shovel.name}&e setDuration &f<&eminutes&f>")
                        "setmaterial" -> {
                            if (p.itemInHand != null) {
                                send(p, "&eO material da Shovel ${shovel.name}&e foi alterado de &f${shovel.getMaterial().name} &epara &f${p.itemInHand.type.name}&e.")
                                shovel.setMaterial(p.itemInHand.type)

                            } else send(p, "&cSegure um item na mão para ser o material base.")
                        }
                        else -> helpShovel(shovel)
                    } else help()
                }
            }
// -----------------------------------
            3 -> {
                when (args[0]) {
                    "create" -> createShovel(p, args[1], args[2])
                    "excavator" -> helpExcavator()
                    else -> if (shovel != null) when (args[1]) {
                        "get" -> {
                            if (Bukkit.getPlayerExact(args[2]) != null)
                                ShovelManager.giveShovel(p, Bukkit.getPlayerExact(args[2]), shovel)
                            else send(p, "&cEste player está offline ou não existe ")
                        }
                        "setdisplay" -> {
                            send(p, "&eO display da Shovel &f${shovel.name}&e foi alterado de ${shovel.getDisplay()}&e para ${args[2]}")
                            shovel.setDisplay(args[2])
                        }
                        "setduration" -> {
                            try {
                                if (args[2].toInt() < 0) {
                                    send(p, "&cO tempo da shovel precisa ser positivo ou 0.")
                                    return true
                                }
                            } catch (e: ParseException) {
                                send(p, "&cInsira um número válido!")
                                return true
                            }

                            send(p, "&eA duração da Shovel &f${shovel.name}&e " +
                                    "foi alterada de &f${if (shovel.getDuration() > 0) formatDate(shovel.getDuration(), false) else shovel.getDuration().toString() + " segundos"}&e " +
                                    "para &f${if (shovel.getDuration() > 0) formatDate(args[2].toInt(), false) else args[2] + " segundos"}&e.")
                            shovel.setDuration(args[2].toInt())
                        }
                        else -> helpShovel(shovel)
                    } else help()
                }
            }
            else -> if (args[0] == "excavator") helpExcavator() else if(shovel != null) helpShovel(shovel) else help()
        }
// -----------------------------------


        return true
    }

    override fun onTabComplete(p: CommandSender?, command: Command?, alias: String?, args: Array<out String>?): MutableList<String> {
        return Collections.emptyList()
    }

    private fun help() {
        sendHeader(player, """
            &eUtilize &f/&7[&enexcavator &7|| &enex&7] +
            &7+ &f<&eShovel&f> &f- Para entrar na edição da Shovel.
            &7+ &ecreate &f<&ename&f> &f<&edisplay&f> &7- Cria uma shovel nova.
            &7+ &eexcavator &7- Entra no menu de comandos dos Excavators.
            &7+ &elist &7- Vê a lista de shovels existentes.
            &7+ &erestart &7- Reinicia os escavadores. 
            &7+ &esave &7- Salva os Excavators e as Shovels. 
            &7+ &estatus &7- Vê o status do Excavator da plot. 
        """.trimIndent())
    }

    private fun helpExcavator() {
        sendHeader(player, """
            Utilize: &f/&enex excavator&7 +
            &7+ &eall &7- Vê a lista de todos Excavators existentes.
            &7+ &elist &7- Vê a lista de Excavators ativos, pausados ou não iniciados.
            &7+ &ecompleted &7- Vê a lista de Excavators finalizados.
            &7+ &eremove &7- Remove o excavator existente da plot.
            &7+ &estop &7- Para o excavator da plot.
        """.trimIndent())
    }

    private fun helpShovel(shovel: Shovel) {
        sendHeader(player, """
            Utilize: &f/&enex ${shovel.name}&7 +
            &7+ &edelete &7- Deleta a Shovel.
            &7+ &eget &f<&eplayer&f) &7- Recebe ou dá a Shovel à um player.
            &7+ &esetDisplay &f<&edisplay&f> &7- Altera o display da Shovel.
            &7+ &esetDuration &f<&eminutes&f> &7- Altera o tempo da Shovel (em minutos).
            &7+ &esetMaterial &7- Altera o material da Shovel pro material do item selecionado na mão.
        """.trimIndent())
    }
}