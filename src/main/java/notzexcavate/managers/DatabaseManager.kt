package notzexcavate.managers

import com.intellectualcrafters.plot.`object`.PlotId
import notzexcavate.dao.DM
import notzexcavate.entities.Excavator
import notzexcavate.entities.Shovel

object DatabaseManager {
    val dm = DM()

    fun insertExcavatorDB(excavator: Excavator) {
        dm.insertExcavator(excavator)
    }

    fun dropExcavatorDB(excavator: Excavator) {
        dm.dropExcavator(excavator)
    }

    fun updateExcavatorDB(excavator: Excavator) {
        dm.updateExcavator(excavator)
    }

    fun getExcavatorDB(plotID: String) {
        dm.getExcavator(plotID)
    }

    fun loadExcavatorsDB(): HashMap<PlotId, Excavator> {
        return dm.loadExcavators()
    }


    fun insertShovelDB(shovel: Shovel): Shovel {
        dm.insertShovel(shovel)
        return Shovel(dm.getLastShovelId(), shovel.name, shovel.getDisplay(), shovel.getDuration(), shovel.getMaterial())
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
        dm.getShovelByID(shovelID)
    }

    fun loadShovelsDB(): HashMap<String, Shovel> {
        return dm.loadShovels()
    }
}