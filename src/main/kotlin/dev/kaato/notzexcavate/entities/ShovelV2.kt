package dev.kaato.notzexcavate.entities

import com.intellectualcrafters.json.JSONArray
import dev.kaato.notzapi.apis.NotzItems.buildItem
import dev.kaato.notzapi.utils.MessageU.Companion.c
import dev.kaato.notzapi.utils.MessageU.Companion.formatDateTime
import dev.kaato.notzexcavate.database.DatabaseManager.deleteShovelDB
import dev.kaato.notzexcavate.database.DatabaseManager.getShovelDB
import dev.kaato.notzexcavate.database.DatabaseManager.insertShovelDB
import dev.kaato.notzexcavate.database.DatabaseManager.updateShovelDB
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.time.LocalDateTime


class ShovelV2(val id: Int) {
    data class ShovelModel(
        val id: Int,
        val name: String,
        val display: String,
        val duration: Int,
        val material: Material,
        val allowedBlocks: List<Material>,
        val blockedBlocks: List<Material>,
        val created: LocalDateTime,
        val updated: LocalDateTime?
    )

    constructor(name: String, display: String) : this(insertShovelDB(name, display))

    val name: String
    private var display: String
    private var duration: Int
    private var material: Material
    private val allowedBlocks = mutableListOf<Material>()
    private val blockedBlocks = mutableListOf<Material>()
    private val created: LocalDateTime
    private var updated: LocalDateTime? = null

    init {
        val sh = getShovelDB(id)
        name = sh.name
        display = sh.display
        duration = sh.duration
        material = sh.material
        allowedBlocks.addAll(sh.allowedBlocks)
        blockedBlocks.addAll(sh.blockedBlocks)
        created = sh.created
        updated = sh.updated
    }

    private var shovel = ItemStack(Material.GOLD_SPADE)
    private var oldShovel = ItemStack(Material.GOLD_SPADE)

    init {
        buildShovel()
    }

    fun deleteForever(): Boolean? {
        return deleteShovelDB(id)
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


    fun getAllowedBlocks(): List<Material> {
        return allowedBlocks
    }

    fun getBlockedBlocks(): List<Material> {
        return blockedBlocks
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

    fun getUpdated(): LocalDateTime? {
        return updated
    }


    fun addAllowedBlock(block: Material): Boolean {
        if (allowedBlocks.contains(block)) return false
        allowedBlocks.add(block)
        save()
        return true
    }

    fun addBlockedBlock(block: Material): Boolean {
        if (blockedBlocks.contains(block)) return false
        blockedBlocks.add(block)
        save()
        return true
    }

    fun remAllowedBlock(block: Material): Boolean {
        if (!allowedBlocks.contains(block)) return false
        allowedBlocks.remove(block)
        save()
        return true
    }

    fun remBlockedBlock(block: Material): Boolean {
        if (!blockedBlocks.contains(block)) return false
        blockedBlocks.remove(block)
        save()
        return true
    }

    fun clearAllowedBlocks(): Boolean {
        if (allowedBlocks.isEmpty()) return false
        allowedBlocks.clear()
        save()
        return true
    }

    fun clearBlockedBlocks(): Boolean {
        if (blockedBlocks.isEmpty()) return false
        blockedBlocks.clear()
        save()
        return true
    }


    private fun buildShovel() {
        val durationMsg = if (duration < 0) "&cnot set" else if (duration == 0) "&f1ms&e/&fblock" else formatDateTime(minutes = duration, eng = true)
        val lore = listOf("&fUse it by right-clicking", "&fon the terrain that", "&fyou want to excavate.", "&eDuration: &a$durationMsg&e.")

        val item = buildItem(material, "&e&lExcavator $display", lore, true)

        if (!shovel.isSimilar(item)) {
            oldShovel = shovel.clone()
            shovel = item

            if (oldShovel != ItemStack(Material.GOLD_SPADE)) {
                updateShovel()
            }
        }
    }

    fun updateShovel(): Int {
        val pls = Bukkit.getOnlinePlayers().filter { it.inventory.contains(oldShovel) }
        pls.forEach {
            if (it.isOnline) {
                it.inventory.contents.filter { item -> oldShovel.isSimilar(item) }.indices.forEach { index ->
                    it.inventory.remove(oldShovel)
                    it.inventory.addItem(shovel.clone())
                }
            }
        }
        return pls.size
    }

//    fun toModel(): ShovelModel {
//        return ShovelModel(id, name, display, duration, material, allowedBlocks, blockedBlocks, created, updated)
//    }

    fun save() {
        updateShovelDB(this)
    }

    fun List<Material>.toJson(): String = JSONArray(this.map { it.name }).toString()
}