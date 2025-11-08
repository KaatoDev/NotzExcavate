package dev.kaato.notzexcavate.dao

import com.intellectualcrafters.plot.`object`.PlotId
import dev.kaato.notzexcavate.entities.Excavator
import dev.kaato.notzexcavate.entities.Shovel
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.sql.Connection

class DM {
    private var c: Connection = DAO().database()

    // EXCAVATOR -- START
    fun insertExcavator(excavator: Excavator) {
        val sql = "insert into excavatormodel values (null, ?, ?)"

        c.prepareStatement(sql).use { ps ->
            ps.setString(1, excavator.getPlotID().toString())
            ps.setBytes(2, serializeExcavator(setOf(excavator)))

            ps.execute()
        }
    }

    fun dropExcavator(excavator: Excavator) {
        val sql = "delete from excavatormodel where id = ?"

        c.prepareStatement(sql).use { ps ->
            ps.setInt(1, excavator.id)

            ps.execute()
        }
    }

    fun updateExcavator(excavator: Excavator) {
        val sql = "update excavatormodel set excavator = ? where plotid = ?"

        c.prepareStatement(sql).use { ps ->
            ps.setBytes(1, serializeExcavator(setOf(excavator)))
            ps.setString(2, excavator.getPlotID().toString())

            ps.execute()
        }
    }

    fun getExcavator(plotid: String): Excavator {
        val sql = "select * from excavatormodel where plotid = ?"

        c.prepareStatement(sql).use { ps ->
            ps.setString(1, plotid)

            ps.executeQuery().use { it.next(); return deserializeExcavator(ByteArrayInputStream(it.getBytes("excavator")))[0] }
        }
    }

    fun getExcavatorID(plotid: String): Int {
        val sql = "select * from excavatormodel where plotid = ?"

        c.prepareStatement(sql).use { ps ->
            ps.setString(1, plotid)

            ps.executeQuery().use { it.next(); return it.getInt("id") }
        }
    }

    fun getExcavatorById(excavatorID: Int): Excavator {
        val sql = "select * from excavatormodel where id = ?"

        c.prepareStatement(sql).use { ps ->
            ps.setInt(1, excavatorID)

            ps.executeQuery().use {
                it.next()

                return deserializeExcavator(ByteArrayInputStream(it.getBytes("excavator")))[0]
            }
        }
    }

    fun getLastExcavatorId(): Int {
        val sql = "select * from excavatormodel"

        c.prepareStatement(sql).use { ps ->
            ps.executeQuery().use {
                var id = 0
                while (it.next())
                    id = it.getInt("id")
                return id
            }
        }
    }

    fun loadExcavators(): HashMap<PlotId, Excavator> {
        val sql = "select * from excavatormodel"

        c.prepareStatement(sql).use { ps ->
            ps.executeQuery().use {
                val excavators = hashMapOf<PlotId, Excavator>()

                while (it.next()) {
                    val excavator = deserializeExcavator(ByteArrayInputStream(it.getBytes("excavator")))[0]
                    excavators[excavator.getPlotID()] = excavator
                }

                return excavators
            }
        }
    }
    // EXCAVATORS -- END

    // SHOVELS -- START
    fun insertShovel(shovel: Shovel) {
        val sql = "insert into shovelmodel values (null, ?, ?)"

        c.prepareStatement(sql).use { ps ->
            ps.setString(1, shovel.name)
            ps.setBytes(2, serializeShovel(setOf(shovel)))

            ps.execute()
        }
    }

    fun dropShovel(shovel: Shovel) {
        val sql = "delete from shovelmodel where id = ?"

        c.prepareStatement(sql).use { ps ->
            ps.setInt(1, shovel.id)
            ps.execute()
        }
    }

    fun updateShovel(shovel: Shovel) {
        val sql = "update shovelmodel set shovel = ? where name = ?"

        c.prepareStatement(sql).use { ps ->
            ps.setBytes(1, serializeShovel(setOf(shovel)))
            ps.setString(2, shovel.name)

            ps.execute()
        }
    }

    fun getShovel(name: String): Shovel {
        val sql = "select * from shovelmodel where name = ?"

        c.prepareStatement(sql).use { ps ->
            ps.setString(1, name)

            ps.executeQuery().use {
                it.next()

                return deserializeShovel(ByteArrayInputStream(it.getBytes("shovel")), it.getInt("id"))[0]
            }
        }
    }

    fun getShovelById(shovelID: Int): Shovel {
        val sql = "select * from shovelmodel where id = ?"

        c.prepareStatement(sql).use { ps ->
            ps.setInt(1, shovelID)

            ps.executeQuery().use {
                it.next()

                return deserializeShovel(ByteArrayInputStream(it.getBytes("shovel")), it.getInt("id"))[0]
            }
        }
    }

    fun getLastShovelId(): Int {
        val sql = "select * from shovelmodel"

        c.prepareStatement(sql).use { ps ->
            ps.executeQuery().use {
                var id = 0
                while (it.next())
                    id = it.getInt("id")
                return id
            }
        }
    }

    fun loadShovels(): HashMap<String, Shovel> {
        val sql = "select * from shovelmodel"

        c.prepareStatement(sql).use { ps ->
            ps.executeQuery().use {
                val shovels = hashMapOf<String, Shovel>()

                while (it.next()) {
                    val shovel = deserializeShovel(ByteArrayInputStream(it.getBytes("shovel")), it.getInt("id"))[0]
                    shovels[shovel.name] = shovel
                }

                return shovels
            }
        }
    }
    // SHOVELS -- END

    // others --------------

    @Throws(RuntimeException::class)
    private fun serializeExcavator(excavators: Set<Excavator>): ByteArray {
        val outputStream = ByteArrayOutputStream()
        val dataOutput = BukkitObjectOutputStream(outputStream)
        dataOutput.writeInt(excavators.size)

        for (excavator in excavators) {
            dataOutput.writeObject(excavator.getExcavatorModel())
        }
        dataOutput.close()
        return outputStream.toByteArray()
    }

    @Throws(java.lang.RuntimeException::class)
    private fun deserializeExcavator(inputStream: InputStream): Array<Excavator> {
        if (inputStream.available() == 0) return arrayOf()

        val dataInput = BukkitObjectInputStream(inputStream)
        val excavators = arrayOfNulls<Excavator>(dataInput.readInt())

        for (excavator in excavators.indices) {
            val ex = dataInput.readObject() as Excavator.ExcavatorModel
            excavators[excavator] = Excavator(ex.id, ex.player, ex.plotId, ex.time, ex.timeLeft, ex.blocks, ex.status, ex.allowedBlocks, ex.blockedBlocks)
        }
        dataInput.close()

        return excavators.filterNotNull().toTypedArray()
    }

    @Throws(RuntimeException::class)
    private fun serializeShovel(shovels: Set<Shovel>): ByteArray {
        val outputStream = ByteArrayOutputStream()
        val dataOutput = BukkitObjectOutputStream(outputStream)
        dataOutput.writeInt(shovels.size)

        for (shovel in shovels) {
            dataOutput.writeObject(shovel.getShovelModel())
        }
        dataOutput.close()
        return outputStream.toByteArray()
    }

    @Throws(java.lang.RuntimeException::class)
    private fun deserializeShovel(inputStream: InputStream?, id: Int): Array<Shovel> {
        if (inputStream == null || inputStream.available() == 0) return arrayOf()
        val dataInput = BukkitObjectInputStream(inputStream)
        val shovels = arrayOfNulls<Shovel>(dataInput.readInt())

        for (shovel in shovels.indices) {
            val sh = dataInput.readObject() as Shovel.ShovelModel
            shovels[shovel] = Shovel(id, sh.name, sh.display, sh.duration, sh.material)
        }
        dataInput.close()

        return shovels.filterNotNull().toTypedArray()
    }
}