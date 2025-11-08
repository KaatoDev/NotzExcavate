package dev.kaato.notzexcavate.commands

import dev.kaato.notzexcavate.NotzExcavate.Companion.messageU
import dev.kaato.notzexcavate.NotzExcavate.Companion.papi
import dev.kaato.notzexcavate.managers.ExcavateManager.getExcavatorStatus
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ExcavateC : CommandExecutor {
    override fun onCommand(p: CommandSender?, command: Command?, label: String?, argss: Array<out String>?): Boolean {
        if (p !is Player) {
            return false
        }

        if (papi.isInPlot(p)) getExcavatorStatus(p, papi.getPlot(p))
        else messageU.send(p, "notInPlot")

        return true
    }
}