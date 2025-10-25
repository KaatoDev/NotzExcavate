package notzexcavate.entities

import notzexcavate.managers.DatabaseManager.updateShovelDB
import notzexcavate.znotzapi.apis.NotzItems.buildItem
import notzexcavate.znotzapi.utils.MessageU.c
import notzexcavate.znotzapi.utils.MessageU.formatDate
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.io.Serializable

class Shovel(val id: Int, val name: String, private var display: String, private var duration: Int, private var material: Material) {
    data class ShovelModel(val id: Int, val name: String, val display: String, val duration: Int, val material: Material, val serialVersionUID: Long) : Serializable

    constructor(name: String, display: String): this(0, name, display, -1, Material.GOLD_SPADE)

    private var shovel = ItemStack(Material.GOLD_SPADE)

    init {
        buildShovel()
    }

    fun setDisplay(newDisplay: String) {
        display = c(newDisplay)
        buildShovel()
        save()
    }

    fun setDuration(minutes: Int) {
        duration = minutes
        buildShovel()
        save()
    }

    fun setMaterial(newMaterial: Material) {
        material = newMaterial
        buildShovel()
        save()
    }

    fun getDisplay(): String {
        return display
    }

    fun getShovel(): ItemStack {
        return shovel.clone()
    }

    fun getDuration(): Int {
        return duration
    }

    fun getMaterial(): Material {
        return material
    }


    private fun buildShovel() {
        val durationMsg = if (duration < 0) "&cnão setada" else if (duration == 0) "&f1ms&e/&fblock" else formatDate(duration, false)
        val lore = listOf("&fUtilize-o clicando com o", "&fbotão direito em cima da", "&fplot que deseja cavar.", "&eDuração: &a$durationMsg&e.")

        val item = buildItem(material, "&e&lEscavador $display", lore, id)

        if (!shovel.isSimilar(item))
            shovel = item
    }

    fun getShovelModel() : ShovelModel {
        return ShovelModel(id, name, display, duration, material, id.toLong())
    }

    fun save() {
        updateShovelDB(this)
    }
}