package dev.kaato.notzexcavate.entities

import dev.kaato.notzapi.apis.NotzItems.buildItem
import dev.kaato.notzapi.utils.MessageU.Companion.c
import dev.kaato.notzapi.utils.MessageU.Companion.formatDateTime
import dev.kaato.notzexcavate.managers.DatabaseManager.updateShovelDB
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.io.Serializable

class Shovel(val id: Int, val name: String, private var display: String, private var duration: Int, private var material: Material, private var allowedBlocks: MutableList<Material> = mutableListOf(), private var blockedBlocks: MutableList<Material> = mutableListOf()) {
    data class ShovelModel(val id: Int, val name: String, val display: String, val duration: Int, val material: Material, val allowedBlocks: List<Material>, val blockedBlocks: List<Material>, val serialVersionUID: Long) : Serializable

    constructor(name: String, display: String) : this(0, name, display, -1, Material.GOLD_SPADE)

    private var shovel = ItemStack(Material.GOLD_SPADE)
    private var oldShovel = ItemStack(Material.GOLD_SPADE)

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

    fun getShovelModel(): ShovelModel {
        return ShovelModel(id, name, display, duration, material, allowedBlocks, blockedBlocks, id.toLong())
    }

    fun save() {
        updateShovelDB(this)
    }
}