package dev.kaato.notzexcavate.managers

import com.intellectualcrafters.plot.`object`.PlotId
import dev.kaato.notzexcavate.dao.DM
import dev.kaato.notzexcavate.entities.Excavator
import dev.kaato.notzexcavate.entities.Shovel

object DatabaseManager {
    private val dm = DM()

    fun insertExcavatorDB(excavator: Excavator): Excavator {
        dm.insertExcavator(excavator)

        val ex = Excavator(dm.getLastExcavatorId(), excavator.owner, excavator.plotId, excavator.time, excavator.timeLeft, excavator.blocks, excavator.status, excavator.allowedBlocks, excavator.blockedBlocks)
        ex.save()
        return ex
    }

    fun dropExcavatorDB(excavator: Excavator) {
        dm.dropExcavator(excavator)
    }

    fun updateExcavatorDB(excavator: Excavator) {
        dm.updateExcavator(excavator)
    }

    fun getExcavatorDB(plotID: String): Excavator {
        return dm.getExcavator(plotID)
    }

    fun getExcavatorIDDB(plotID: String): Int {
        return dm.getExcavatorID(plotID)
    }

    fun loadExcavatorsDB(): HashMap<PlotId, Excavator> {
        return dm.loadExcavators()
    }


    fun insertShovelDB(shovel: Shovel): Shovel {
        dm.insertShovel(shovel)
        val sh = Shovel(dm.getLastExcavatorId(), shovel.name, shovel.getDisplay(), shovel.getDuration(), shovel.getMaterial())
        sh.save()
        return sh
    }

    fun dropShovelDB(shovel: Shovel) {
        dm.dropShovel(shovel)
    }

    fun updateShovelDB(shovel: Shovel) {
        dm.updateShovel(shovel)
    }

    fun getShovelDB(name: String) {
        dm.getShovel(name)
    }

    fun getShovelByIdDB(shovelID: Int) {
        dm.getShovelById(shovelID)
    }

    fun loadShovelsDB(): HashMap<String, Shovel> {
        return dm.loadShovels()
    }
}