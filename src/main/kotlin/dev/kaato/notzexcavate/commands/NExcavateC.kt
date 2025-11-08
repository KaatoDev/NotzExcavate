package dev.kaato.notzexcavate.commands

import dev.kaato.notzapi.utils.MessageU.Companion.join
import dev.kaato.notzexcavate.NotzExcavate.Companion.messageU
import dev.kaato.notzexcavate.NotzExcavate.Companion.othersU
import dev.kaato.notzexcavate.NotzExcavate.Companion.papi
import dev.kaato.notzexcavate.managers.CommandManager.addAllowedCMD
import dev.kaato.notzexcavate.managers.CommandManager.addBlockedCMD
import dev.kaato.notzexcavate.managers.CommandManager.clearAllowedCMD
import dev.kaato.notzexcavate.managers.CommandManager.clearBlockedCMD
import dev.kaato.notzexcavate.managers.CommandManager.deleteShovelCMD
import dev.kaato.notzexcavate.managers.CommandManager.getShovelCMD
import dev.kaato.notzexcavate.managers.CommandManager.remAllowedCMD
import dev.kaato.notzexcavate.managers.CommandManager.remBlockedCMD
import dev.kaato.notzexcavate.managers.CommandManager.removeExcavatorCMD
import dev.kaato.notzexcavate.managers.CommandManager.setDisplayCMD
import dev.kaato.notzexcavate.managers.CommandManager.setDurationCMD
import dev.kaato.notzexcavate.managers.CommandManager.setMaterialCMD
import dev.kaato.notzexcavate.managers.CommandManager.updateItemCMD
import dev.kaato.notzexcavate.managers.CommandManager.viewAllowedCMD
import dev.kaato.notzexcavate.managers.CommandManager.viewBlockedCMD
import dev.kaato.notzexcavate.managers.ExcavateManager.getAllExcavators
import dev.kaato.notzexcavate.managers.ExcavateManager.getCompletedExcavators
import dev.kaato.notzexcavate.managers.ExcavateManager.getExcavatorStatus
import dev.kaato.notzexcavate.managers.ExcavateManager.getRunningExcavators
import dev.kaato.notzexcavate.managers.ExcavateManager.restartExcavators
import dev.kaato.notzexcavate.managers.ExcavateManager.saveExcavators
import dev.kaato.notzexcavate.managers.ExcavateManager.stopExcavator
import dev.kaato.notzexcavate.managers.ShovelManager.createShovel
import dev.kaato.notzexcavate.managers.ShovelManager.getShovels
import dev.kaato.notzexcavate.managers.ShovelManager.hasShovel
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import java.util.*

class NExcavateC : TabExecutor {
    override fun onCommand(p: CommandSender?, command: Command?, label: String?, argss: Array<out String>?): Boolean {
        if (p !is Player) {
            val sender = p as? ConsoleCommandSender ?: Bukkit.getConsoleSender()
            val msg = { messageU.send(sender, "&eUse /nex &f<&eShovel&f> &egive &f<&ePlayer&f> &f(&equantity&f)") }

            if (argss == null || argss.isEmpty()) {
                msg()
                return true
            }

            val args = argss.map(String::lowercase).toTypedArray()

            if (args.size >= 3 && hasShovel(args[0]) && args[1] == "give") {
                return getShovelCMD(sender, args[2], args[0], if (args.size == 3) "1" else args[3])
            } else msg()

            return true
        }

        if (othersU.isntAdmin(p)) return true

        if (argss == null || argss.isEmpty()) {
            helpCmd(p)
            return true
        }

        val args = argss.map { var arg = it; if (!it.contains('&')) arg = it.lowercase(); arg }.toTypedArray()
        val shovel = args[0]
        val hasShovel = hasShovel(shovel)
        val help = { if (hasShovel) helpShovel(p, shovel) else helpCmd(p) }

// -----------------------------------
        when (args.size) {
            1 -> {
                when (args[0]) {
                    "create" -> messageU.send(p, "&eUse: &f/&enexcavator create &f<&ename&f> &f<&edisplay&f>")
                    "excavator" -> helpExcavator(p)
                    "list" -> messageU.send(p, join(getShovels().toList()))
                    "restart" -> restartExcavators(p)
                    "save" -> saveExcavators(p)
                    "status" -> {
                        if (papi.isInPlot(p)) getExcavatorStatus(p, papi.getPlot(p))
                        else messageU.send(p, "notInPlot")
                    }

                    else -> help()
                }
            }
// -----------------------------------
            2 -> {
                when (args[0]) {
                    "create" -> messageU.send(p, "&eUse: &f/&enexcavator create &f<&ename&f> &f<&edisplay&f>")
                    "excavator" -> {
                        if (!papi.isInPlot(p)) {
                            messageU.send(p, "notInPlot")
                            return true
                        }

                        when (args[1]) {
                            "all" -> getAllExcavators(p)
                            "completed" -> getCompletedExcavators(p)
                            "list" -> getRunningExcavators(p)
                            "remove" -> removeExcavatorCMD(p)
                            "stop" -> stopExcavator(p, papi.getPlot(p))
                            else -> helpExcavator(p)
                        }
                    }

                    else -> if (hasShovel) when (args[1]) {
                        "clearallowed" -> clearAllowedCMD(p, shovel)
                        "clearblocked" -> clearBlockedCMD(p, shovel)
                        "delete" -> deleteShovelCMD(p, shovel)
                        "get" -> getShovelCMD(p, sh = shovel)
                        "setdisplay" -> messageU.send(p, "&eUse &f/&enex &f${args[1]}&e setDisplay &f<&edisplay&f> ")
                        "setduration" -> messageU.send(p, "&eUse &f/&enex &f${args[1]}&e setDuration &f<&eminutes&f>")
                        "setmaterial" -> setMaterialCMD(p, shovel)
                        "updateitem" -> updateItemCMD(p, shovel)
                        "viewallowed" -> viewAllowedCMD(p, shovel)
                        "viewblocked" -> viewBlockedCMD(p, shovel)

                        else -> helpShovel(p, shovel)
                    } else help()
                }
            }
// -----------------------------------
            3 -> {
                when (args[0]) {
                    "create" -> createShovel(p, args[1], args[2])
                    "excavator" -> helpExcavator(p)
                    else -> if (hasShovel) when (args[1]) {
                        "addallowed" -> addAllowedCMD(p, args[2].uppercase(), shovel)
                        "addblocked" -> addBlockedCMD(p, args[2].uppercase(), shovel)
                        "get" -> getShovelCMD(p, args[2], sh = shovel)
                        "remallowed" -> remAllowedCMD(p, args[2], shovel)
                        "remblocked" -> remBlockedCMD(p, args[2], shovel)
                        "setdisplay" -> setDisplayCMD(p, argss[2], shovel)
                        "setduration" -> setDurationCMD(p, args[2], shovel)
                        else -> helpShovel(p, shovel)
                    } else help()
                }
            }

            else -> if (args[0] == "excavator") helpExcavator(p) else help()
        }
// -----------------------------------
        return true
    }

    override fun onTabComplete(p: CommandSender?, command: Command?, alias: String?, args: Array<out String>?): MutableList<String> {
        val shovel = args?.getOrNull(0) ?: ""
        val hasShovel = hasShovel(shovel)
        val size = args?.size ?: 0

        return if (shovel == "create") when (size) {
            2 -> mutableListOf("<name>")
            3 -> mutableListOf("<display>")
            else -> Collections.emptyList()
        } else when (size) {
            1 -> mutableListOf("create", "excavator", "list", "restart", "save", "status")
            2 -> if (args!![0] == "excavator") mutableListOf("all", "completed", "list", "remove", "stop") else if (hasShovel) mutableListOf("clearallowed", "clearblocked", "delete", "get", "setdisplay", "setduration", "setmaterial", "updateitem", "viewallowed", "viewblocked") else Collections.emptyList()
            3 -> if (hasShovel && args!![1] == "get") Bukkit.getOnlinePlayers().map(Player::getName).toMutableList() else Collections.emptyList()
            else -> if (args == null) mutableListOf("create", "excavator", "list", "restart", "save", "status") else Collections.emptyList()
        }
    }


    private fun helpCmd(p: Player) {
        messageU.sendHeader(
            p, """
            &eUse &f/&7[&enexcavator &7|| &enex&7] +
            &7+ &f<&eShovel&f> &7-  Enters the Shovel command menu.
            &7+ &ecreate &f<&ename&f> &f<&edisplay&f> &7- Creates a new Shovel.
            &7+ &eexcavator &7- Enters the Excavator command menu.
            &7+ &elist &7- Views the list of existing Shovels.
            &7+ &erestart &7- Restart the Excavators. 
            &7+ &esave &7- Saves the Excavators and Shovels. 
            &7+ &estatus &7- Views the Excavator status for the plot. 
        """.trimIndent()
        )
    }

    private fun helpExcavator(p: Player) {
        messageU.sendHeader(
            p, """
            Use: &f/&enex excavator&7 +
            &7+ &eall &7- Views the list of all existing Excavators.
            &7+ &elist &7- Shows the list of incompleted Excavators.
            &7+ &ecompleted &7- Shows the list of completed Excavators.
            &7+ &eremove &7- Removes the existing Excavator from the plot.
            &7+ &estop &7- Stops the Excavator from the plot.
        """.trimIndent()
        )
    }

    private fun helpShovel(p: Player, shovel: String) {
        messageU.sendHeader(
            p, """
            Use: &f/&enex ${shovel}&7 +
            &7+ &eaddAllowed &f<&eblock&f> &7- Adds a block to the allowed list.
            &7+ &eaddBlocked &f<&eblock&f> &7- Adds a block to the blocked list.
            &7+ &eclearAllowed &7- Resets the allowed list.
            &7+ &eclearBlocked &7- Resets the blocked list.
            &7+ &edelete &7- Deletes the Shovel.
            &7+ &eget &f<&eplayer&f> &7- Gives the Shovel item.
            &7+ &eremAllowed &f<&eblock&f> &7- Removes a block from the allowed list.
            &7+ &eremBlocked &f<&eblock&f> &7- Removes a block from the blocked list.
            &7+ &esetDisplay &f<&edisplay&f> &7- Changes the Shovel display.
            &7+ &esetDuration &f<&eminutes&f> &7- Changes the Shovel duration (in minutes).
            &7+ &esetMaterial &7- Changes the Shovel material item to the material of the item in hand.
            &7+ &eupdateItem &7- Updates the Shovel item in the inventory of all online players.
        """.trimIndent()
        )
    }
}