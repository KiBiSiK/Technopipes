package net.cydhra.technopipes.capabilities.energy

import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.energy.IEnergyStorage

/**
 * Store an instance of [DynamicEnergyCapability] into NBT. Unlike the forge version, this also stores capacity and
 * transfer limits, as they might be changed for clients of the energy capability.
 */
object DynamicEnergyStorageStrategy {

    /**
     * NBT tag that stores the storage capacity
     */
    const val KEY_CAPACITY = "capacity"

    /**
     * NBT tag that stores the current amount of energy within the storage
     */
    const val KEY_CURRENT_AMOUNT = "amount"

    /**
     * NBT tag that stores the limit of how much energy the storage can receive per tick
     */
    const val KEY_MAX_RECEIVE = "limit_receive"

    /**
     * NBT tag that stores the limit of how much energy can be extracted from the storage per tick
     */
    const val KEY_MAX_EXTRACT = "limit_extract"

    fun readNBT(instance: IEnergyStorage, nbt: NBTBase) {
        with(instance as DynamicEnergyCapability) {
            capacity = (nbt as NBTTagCompound).getInteger(KEY_CAPACITY)
            extractionLimit = nbt.getInteger(KEY_MAX_EXTRACT)
            receivingLimit = nbt.getInteger(KEY_MAX_RECEIVE)
            forceUpdateOfCurrentEnergy(nbt.getInteger(KEY_CURRENT_AMOUNT))
        }
    }

    fun writeNBT(instance: IEnergyStorage): NBTBase {
        return NBTTagCompound().apply {
            setInteger(KEY_CAPACITY, instance.maxEnergyStored)
            setInteger(KEY_MAX_EXTRACT, (instance as DynamicEnergyCapability).extractionLimit)
            setInteger(KEY_MAX_RECEIVE, instance.receivingLimit)
            setInteger(KEY_CURRENT_AMOUNT, instance.energyStored)
        }
    }
}