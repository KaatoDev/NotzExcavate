package dev.kaato.notzexcavate.enums

import dev.kaato.notzexcavate.NotzExcavate.Companion.placeholderManager

enum class Status(val description: String) {
    NOTSTARTED(placeholderManager.set("Not started")),
    RUNNING(placeholderManager.set("Running")),
    PAUSED(placeholderManager.set("Paused")),
    COMPLETED(placeholderManager.set("Completed"))
}