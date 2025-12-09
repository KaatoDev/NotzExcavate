package dev.kaato.notzexcavate.database

import dev.kaato.notzexcavate.enums.Status
import org.bukkit.Material
import org.jetbrains.annotations.NotNull
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

const val max_varchar = 128

object Excavators : Table("excavators") {
    @NotNull val id = integer("id").autoIncrement()
    override val primaryKey = PrimaryKey(Excavators.id)
    @NotNull val player = varchar("player",max_varchar)
    @NotNull val playerUuid = uuid("playeruuid")
    @NotNull val plotId = varchar("plotId", max_varchar).uniqueIndex()
    @NotNull val time = long("time").default(60000)
    @NotNull val timeLeft = long("timeLeft").default(60000)
    @NotNull val blocks = integer("blocks").default(0)
    @NotNull val status = enumerationByName("status", max_varchar, Status::class).default(Status.NOTSTARTED)
    @NotNull val allowedBlocks = text("allowedBlocks").default("[]")
    @NotNull val blockedBlocks = text("blockedBlocks").default("[]")
    @NotNull val created = datetime("created").defaultExpression(CurrentDateTime)
    @NotNull val updated = datetime("updated").nullable()
}

object Shovels : Table("shovels") {
    @NotNull val id = integer("id").autoIncrement()
    override val primaryKey = PrimaryKey(id)
    @NotNull val name = varchar("name",max_varchar)
    @NotNull val display = varchar("display",max_varchar)
    @NotNull val duration = integer("duration").default(0)
    @NotNull val material = varchar("material",max_varchar).default("GOLD_SPADE")
    @NotNull val allowedBlocks = text("allowedBlocks").default("[]")
    @NotNull val blockedBlocks = text("blockedBlocks").default("[]")
    @NotNull val created = datetime("created").defaultExpression(CurrentDateTime)
    @NotNull val updated = datetime("updated").nullable()
}