package dev.kaato.notzexcavate.database

import com.intellectualcrafters.json.JSONArray
import com.intellectualcrafters.plot.`object`.Plot
import dev.kaato.notzapi.utils.MessageU.Companion.log
import dev.kaato.notzexcavate.converter.DatabaseManagerConverter
import dev.kaato.notzexcavate.converter.DatabaseManagerConverter.closeConverterDB
import dev.kaato.notzexcavate.converter.DatabaseManagerConverter.dropTablesConverterDB
import dev.kaato.notzexcavate.converter.DatabaseManagerConverter.hasTablesConverterDB
import dev.kaato.notzexcavate.entities.Excavator
import dev.kaato.notzexcavate.entities.ExcavatorV2
import dev.kaato.notzexcavate.entities.ExcavatorV2.ExcavatorModel
import dev.kaato.notzexcavate.entities.Shovel
import dev.kaato.notzexcavate.entities.ShovelV2
import dev.kaato.notzexcavate.entities.ShovelV2.ShovelModel
import org.bukkit.Bukkit
import org.bukkit.Material
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime

object DatabaseManager {
    fun insertExcavatorDB(plot: Plot, time: Long, allowedBlocks: List<Material>, blockedBlocks: List<Material>): Int {
        val id = transaction {
            Excavators.insert {
                it[this.player] = Bukkit.getOfflinePlayer(plot.owners.first()).name
                it[this.playerUuid] = plot.owners.first()
                it[this.plotId] = plot.id.toString()
                it[this.time] = time
                it[this.allowedBlocks] = parseToJson(allowedBlocks)
                it[this.blockedBlocks] = parseToJson(blockedBlocks)
            } get Excavators.id
        }
        return id
    }
    
    fun insertConvertedExcavatorDB(cExcavator: Excavator): Int {
        val id = transaction {
            Excavators.insert {
                it[this.player] = cExcavator.owner.name
                it[this.playerUuid] = cExcavator.owner.uniqueId
                it[this.plotId] = cExcavator.plotId
                it[this.time] = cExcavator.time
                it[this.time] = cExcavator.timeLeft
                it[this.blocks] = cExcavator.blocks
                it[this.status] = cExcavator.status
                it[this.allowedBlocks] = parseToJson(cExcavator.allowedBlocks)
                it[this.blockedBlocks] = parseToJson(cExcavator.blockedBlocks)
            } get Excavators.id
        }
        return id
    }

    fun getExcavatorDB(id: Int): ExcavatorModel {
        return transaction {
            Excavators.selectAll().where { Excavators.id eq id }.first().let {
                ExcavatorModel(
                    it[Excavators.id],
                    it[Excavators.player],
                    it[Excavators.playerUuid],
                    it[Excavators.plotId],
                    it[Excavators.time],
                    it[Excavators.timeLeft],
                    it[Excavators.blocks],
                    it[Excavators.status],
                    parseMaterialList(it[Excavators.allowedBlocks]),
                    parseMaterialList(it[Excavators.blockedBlocks]),
                    it[Excavators.created],
                    it[Excavators.updated]
                )
            }
        }
    }

    fun updateExcavatorDB(excavator: ExcavatorV2) {
        transaction {
            Excavators.update({ Excavators.id eq excavator.id }) {
                it[plotId] = excavator.plotId
                it[time] = excavator.time
                it[timeLeft] = excavator.getTimeLeft()
                it[blocks] = excavator.getBlocks()
                it[status] = excavator.getStatus()
                it[allowedBlocks] = parseToJson(excavator.getAllowedBlocks())
                it[blockedBlocks] = parseToJson(excavator.getBlockedBlocks())
                it[updated] = excavator.getUpdated()
            }
        }
    }

    fun updateExcavatorConverterDB(excavatorConverter: Excavator) {
        transaction {
            Excavators.update({ Excavators.id eq excavatorConverter.id }) {
                it[plotId] = excavatorConverter.plotId
                it[time] = excavatorConverter.time
                it[timeLeft] = excavatorConverter.timeLeft
                it[blocks] = excavatorConverter.blocks
                it[status] = excavatorConverter.status
                it[allowedBlocks] = parseToJson(excavatorConverter.allowedBlocks)
                it[blockedBlocks] = parseToJson(excavatorConverter.blockedBlocks)
                it[updated] = LocalDateTime.now()
            }
        }
    }

    fun deleteExcavatorDB(id: Int): Boolean? {
        val res = transaction {
            Excavators.deleteWhere { Excavators.id eq id }
        }
        return when (res) {
            0 -> false
            1 -> true
            else -> {
                log("More Excavators were deleted!!!!! - $res")
                null
            }
        }
    }

    fun loadExcavatorsDB(): List<ExcavatorV2> {
        return transaction { Excavators.select(Excavators.id).map { ExcavatorV2(it[Excavators.id]) } }
    }

    fun containExcavatorDB(plotId: String): Boolean {
        return transaction {
            Excavators.selectAll().where { Excavators.plotId eq plotId }.toList().isNotEmpty()
        }
    }

    fun containExcavatorsDB(): Boolean {
        return transaction {
            Excavators.selectAll().toList().isNotEmpty()
        }
    }


    fun insertShovelDB(name: String, display: String): Int {
        val id = transaction {
            Shovels.insert {
                it[this.name] = name
                it[this.display] = display
            } get Shovels.id
        }
        return id
    }
    
    fun insertConvertedShovelDB(cShovel: Shovel): Int {
        val id = transaction {
            Shovels.insert {
                it[this.name] = cShovel.name
                it[this.display] = cShovel.getDisplay()
                it[this.duration] = cShovel.getDuration()
                it[this.material] = cShovel.getMaterial().toString()
                it[this.allowedBlocks] = parseToJson(cShovel.allowedBlocks)
                it[this.blockedBlocks] = parseToJson(cShovel.blockedBlocks)
            } get Shovels.id
        }
        return id
    }

    fun getShovelDB(id: Int): ShovelModel {
        return transaction {
            Shovels.selectAll().where { Shovels.id eq id }.first().let {
                ShovelModel(
                    it[Shovels.id],
                    it[Shovels.name],
                    it[Shovels.display],
                    it[Shovels.duration],
                    Material.valueOf(it[Shovels.material]),
                    parseMaterialList(it[Shovels.allowedBlocks]),
                    parseMaterialList(it[Shovels.blockedBlocks]),
                    it[Shovels.created],
                    it[Shovels.updated]
                )
            }
        }
    }

    fun updateShovelDB(shovel: ShovelV2) {
        transaction {
            Shovels.update({ Shovels.id eq shovel.id }) {
                it[display] = shovel.getDisplay()
                it[duration] = shovel.getDuration()
                it[material] = shovel.getMaterial().toString()
                it[allowedBlocks] = parseToJson(shovel.getAllowedBlocks())
                it[blockedBlocks] = parseToJson(shovel.getBlockedBlocks())
                it[updated] = shovel.getUpdated()
            }
        }
    }

    fun updateShovelConverterDB(shovelConverter: Shovel) {
        transaction {
            Shovels.update({ Shovels.name eq shovelConverter.name }) {
                it[display] = shovelConverter.getDisplay()
                it[duration] = shovelConverter.getDuration()
                it[material] = shovelConverter.getMaterial().toString()
                it[allowedBlocks] = parseToJson(shovelConverter.allowedBlocks)
                it[blockedBlocks] = parseToJson(shovelConverter.blockedBlocks)
                it[Excavators.updated] = LocalDateTime.now()
            }
        }
    }

    fun deleteShovelDB(id: Int): Boolean? {
        val res = transaction {
            Shovels.deleteWhere { Shovels.id eq id }
        }
        return when (res) {
            0 -> false
            1 -> true
            else -> {
                log("More Shovels were deleted!!!!! - $res")
                null
            }
        }
    }

    fun loadShovelsDB(): List<ShovelV2> {
        return transaction { Shovels.select(Shovels.id).map { ShovelV2(it[Shovels.id]) } }
    }

    fun containShovelDB(name: String): Boolean {
        return transaction {
            Shovels.selectAll().where { Shovels.name eq name }.toList().isNotEmpty()
        }
    }

    fun containShovelsDB(): Boolean {
        return transaction {
            Shovels.selectAll().toList().isNotEmpty()
        }
    }

    fun parseToJson(list: List<Material>): String {
        return JSONArray(list.map { it.name }).toString()
    }

    fun parseMaterialList(json: String): List<Material> = JSONArray(json).let { arr ->
        (0 until arr.length()).map { i ->
            Material.valueOf(arr.getString(i))
        }
    }

    fun checkOldDatabase() = hasTablesConverterDB()


    fun eraseOldDatabase() {
        dropTablesConverterDB()
        closeConverterDB()
    }

    fun convertExcavatorsDatabase(): List<ExcavatorV2> {
        val exs = DatabaseManagerConverter.loadExcavatorsDB()
        val exIds = mutableListOf<Int>()

        exs.values.forEach {
            if (containExcavatorDB(it.plotId))
                updateExcavatorConverterDB(it)
            else exIds.add(insertConvertedExcavatorDB(it))
        }

        return exIds.map(::ExcavatorV2)
    }

    fun convertShovelsDatabase(onlyShovel: String): List<ShovelV2> {
        val shs = DatabaseManagerConverter.loadShovelsDB()

        val shIds = mutableListOf<Int>()

        shs.values.forEach {
            if (onlyShovel != "all" && onlyShovel.isNotEmpty() && it.name != onlyShovel) return@forEach

            if (containShovelDB(it.name))
                updateShovelConverterDB(it)
            else shIds.add(insertConvertedShovelDB(it))
        }

        return shIds.map(::ShovelV2)
    }
}