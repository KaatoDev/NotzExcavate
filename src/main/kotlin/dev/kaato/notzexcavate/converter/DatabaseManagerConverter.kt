package dev.kaato.notzexcavate.converter

import com.intellectualcrafters.plot.`object`.PlotId
import dev.kaato.notzexcavate.entities.Excavator
import dev.kaato.notzexcavate.entities.Shovel

object DatabaseManagerConverter {
    private val dm = DMConverter()

    fun updateExcavatorDB(excavator: Excavator) {
        dm.updateExcavator(excavator)
    }

    fun updateShovelDB(shovel: Shovel) {
        dm.updateShovel(shovel)
    }

    fun loadExcavatorsDB(): HashMap<PlotId, Excavator> = dm.loadExcavators()
    fun loadShovelsDB(): HashMap<String, Shovel> = dm.loadShovels()
    fun hasTablesConverterDB() = dm.hasTables()
    fun dropTablesConverterDB() = dm.dropTables()
    fun closeConverterDB() = dm.close()
}