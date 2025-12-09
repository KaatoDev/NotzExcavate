package dev.kaato.notzexcavate.commands

import dev.kaato.notzapi.utils.MessageU.Companion.join
import dev.kaato.notzexcavate.NotzExcavate.Companion.messageU
import dev.kaato.notzexcavate.NotzExcavate.Companion.othersU
import dev.kaato.notzexcavate.NotzExcavate.Companion.papi
import dev.kaato.notzexcavate.managers.CommandManager.addAllowedCMD
import dev.kaato.notzexcavate.managers.CommandManager.addBlockedCMD
import dev.kaato.notzexcavate.managers.CommandManager.clearAllowedCMD
import dev.kaato.notzexcavate.managers.CommandManager.clearBlockedCMD
import dev.kaato.notzexcavate.managers.CommandManager.convertDatabaseCMD
import dev.kaato.notzexcavate.managers.CommandManager.deleteShovelCMD
import dev.kaato.notzexcavate.managers.CommandManager.getShovelCMD
import dev.kaato.notzexcavate.managers.CommandManager.remAllowedCMD
import dev.kaato.notzexcavate.managers.CommandManager.remBlockedCMD
import dev.kaato.notzexcavate.managers.CommandManager.removeExcavatorCMD
import dev.kaato.notzexcavate.managers.CommandManager.removeOldDatabase
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
    override fun onCommand(player: CommandSender?, command: Command?, label: String?, argss: Array<out String>?): Boolean {
        if (player !is Player) {
            val sender = player as? ConsoleCommandSender ?: Bukkit.getConsoleSender()
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

        if (othersU.isntAdmin(player)) return true

        if (argss == null || argss.isEmpty()) {
            helpCmd(player)
            return true
        }

        val args = argss.map { var arg = it; if (!it.contains('&')) arg = it.lowercase(); arg }.toTypedArray()
        val shovel = args[0]
        val hasShovel = hasShovel(shovel)
        val help = { if (hasShovel) helpShovel(player, shovel) else helpCmd(player) }

// -----------------------------------
        when (args.size) {
            1 -> {
                when (args[0]) {
                    "convert" -> messageU.send(player, "&eUse: &f/&enexcavator convert all&f/<&eshovel&f>")
                    "cleanolddatabase" -> removeOldDatabase(player)
                    "create" -> messageU.send(player, "&eUse: &f/&enexcavator create &f<&ename&f> &f<&edisplay&f>")
                    "excavator" -> helpExcavator(player)
                    "list" -> messageU.send(player, join(getShovels().toList()))
                    "restart" -> restartExcavators(player)
                    "save" -> saveExcavators(player)
                    "status" -> {
                        if (papi.isInPlot(player)) getExcavatorStatus(player, papi.getPlot(player))
                        else messageU.send(player, "notInPlot")
                    }

                    else -> help()
                }
            }
// -----------------------------------
            2 -> {
                when (args[0]) {
                    "convert" -> convertDatabaseCMD(player, args[1])
                    "create" -> messageU.send(player, "&eUse: &f/&enexcavator create &f<&ename&f> &f<&edisplay&f>")
                    "excavator" -> {
                        if (!papi.isInPlot(player)) {
                            messageU.send(player, "notInPlot")
                            return true
                        }

                        when (args[1]) {
                            "all" -> getAllExcavators(player)
                            "completed" -> getCompletedExcavators(player)
                            "list" -> getRunningExcavators(player)
                            "remove" -> removeExcavatorCMD(player)
                            "stop" -> stopExcavator(player, papi.getPlot(player))
                            else -> helpExcavator(player)
                        }
                    }

                    else -> if (hasShovel) when (args[1]) {
                        "clearallowed" -> clearAllowedCMD(player, shovel)
                        "clearblocked" -> clearBlockedCMD(player, shovel)
                        "delete" -> deleteShovelCMD(player, shovel)
                        "get" -> getShovelCMD(player, sh = shovel)
                        "setdisplay" -> messageU.send(player, "&eUse &f/&enex &f${args[1]}&e setDisplay &f<&edisplay&f> ")
                        "setduration" -> messageU.send(player, "&eUse &f/&enex &f${args[1]}&e setDuration &f<&eminutes&f>")
                        "setmaterial" -> setMaterialCMD(player, shovel)
                        "updateitem" -> updateItemCMD(player, shovel)
                        "viewallowed" -> viewAllowedCMD(player, shovel)
                        "viewblocked" -> viewBlockedCMD(player, shovel)

                        else -> helpShovel(player, shovel)
                    } else help()
                }
            }
// -----------------------------------
            3 -> {
                when (args[0]) {
                    "create" -> createShovel(player, args[1], args[2])
                    "excavator" -> helpExcavator(player)
                    else -> if (hasShovel) when (args[1]) {
                        "addallowed" -> addAllowedCMD(player, args[2].uppercase(), shovel)
                        "addblocked" -> addBlockedCMD(player, args[2].uppercase(), shovel)
                        "get" -> getShovelCMD(player, args[2], sh = shovel)
                        "remallowed" -> remAllowedCMD(player, args[2], shovel)
                        "remblocked" -> remBlockedCMD(player, args[2], shovel)
                        "setdisplay" -> setDisplayCMD(player, argss[2], shovel)
                        "setduration" -> setDurationCMD(player, args[2], shovel)
                        else -> helpShovel(player, shovel)
                    } else help()
                }
            }

            else -> if (args[0] == "excavator") helpExcavator(player) else help()
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
            1 -> mutableListOf("cleanOldDatabase", "convert", "create", "excavator", "list", "restart", "save", "status")
            2 -> if (args!![0] == "excavator") mutableListOf("all", "completed", "list", "remove", "stop") else if (hasShovel) mutableListOf("clearallowed", "clearblocked", "delete", "get", "setdisplay", "setduration", "setmaterial", "updateitem", "viewallowed", "viewblocked") else Collections.emptyList()
            3 -> if (hasShovel && args!![1] == "get") Bukkit.getOnlinePlayers().map(Player::getName).toMutableList() else Collections.emptyList()
            else -> if (args == null) mutableListOf("create", "excavator", "list", "restart", "save", "status") else Collections.emptyList()
        }
    }


    private fun helpCmd(player: Player) {
        messageU.sendHeader(
            player, """
            &eUse &f/&7[&enexcavator &7|| &enex&7] +
            &7+ &f<&eShovel&f> &7- Enters the Shovel command menu.
            &7+ &ecleanOldDatabase - Deletes the old database
            &7+ &econvert &f<all&f/<&eshovel&f>> &7- Convert all the old excavators and all or a specifically one of the old Shovels
            &7+ &ecreate &f<&ename&f> &f<&edisplay&f> &7- Creates a new Shovel.
            &7+ &eexcavator &7- Enters the Excavator command menu.
            &7+ &elist &7- Views the list of existing Shovels.
            &7+ &erestart &7- Restart the Excavators. 
            &7+ &esave &7- Saves the Excavators and Shovels. 
            &7+ &estatus &7- Views the Excavator status for the plot. 
        """.trimIndent()
        )
    }

    private fun helpExcavator(player: Player) {
        messageU.sendHeader(
            player, """
            Use: &f/&enex excavator&7 +
            &7+ &eall &7- Views the list of all existing Excavators.
            &7+ &elist &7- Shows the list of incompleted Excavators.
            &7+ &ecompleted &7- Shows the list of completed Excavators.
            &7+ &eremove &7- Removes the existing Excavator from the plot.
            &7+ &estop &7- Stops the Excavator from the plot.
        """.trimIndent()
        )
    }

    private fun helpShovel(player: Player, shovel: String) {
        messageU.sendHeader(
            player, """
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