package net.cydhra.technopipes.capabilities.fluid

import net.cydhra.technopipes.capabilities.AbstractComponentCapabilityBridge
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.util.INBTSerializable
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fluids.capability.IFluidTankProperties
import kotlin.math.abs
import kotlin.math.min

/**
 * @param capacity fluid storage capacity
 * @param allowedFluid a list of fluid names that are allowed to store, empty if all fluids are allowed
 * @param tanktype whether input, output or both are allowed
 */
open class DynamicFluidCapability(
    var capacity: Int = 1000, val allowedFluid: MutableList<String> = mutableListOf(),
    var tanktype: TankType = TankType.BOTH
) :
    IFluidHandler, INBTSerializable<NBTTagCompound>, AbstractComponentCapabilityBridge() {

    var currentFluid: FluidStack? = null
        private set

    val simpleTankProperty = arrayOf<IFluidTankProperties>(SimpleTankProperty(this))

    //Threshold in percent that is needed for the te to send an update to the client
    var fluidChangeThreshold = -1f

    //The last amount of fluid that was synced with the client
    private var lastUpdatedFluidValue = -1

    override fun drain(resource: FluidStack, doDrain: Boolean): FluidStack? {
        return drain(resource, doDrain, false)
    }

    fun drain(resource: FluidStack, doDrain: Boolean, forced: Boolean): FluidStack? {

        if (!forced && tanktype == TankType.INPUT)
            return null

        val lastFluid = currentFluid

        if (currentFluid == null || !currentFluid!!.isFluidEqual(resource)) {
            return null
        }

        val drain = min(resource.amount, currentFluid!!.amount)

        if (doDrain) {
            currentFluid!!.amount -= drain
        }

        val out = FluidStack(currentFluid!!.fluid, drain)

        if (currentFluid!!.amount <= 0) {
            currentFluid = null
        }

        if (doDrain) {
            markDirty(currentFluid != lastFluid)
        }

        return out
    }

    override fun drain(maxDrain: Int, doDrain: Boolean): FluidStack? {
        return drain(maxDrain, doDrain, false)
    }

    /**
     * Drains a amount out of this capability.
     * The force parameter is used to internally manipulate it and ignores the type of the tank
     */
    fun drain(maxDrain: Int, doDrain: Boolean, forced: Boolean): FluidStack? {
        if (currentFluid == null)
            return null

        return drain(FluidStack(currentFluid, maxDrain), doDrain, forced)
    }

    fun setFluid(resource: FluidStack) {
        currentFluid = FluidStack(resource.fluid, 0)

        val fill = min(resource.amount, capacity)

        currentFluid!!.amount = fill

        markDirty(true)
    }

    override fun fill(resource: FluidStack, doFill: Boolean): Int {
        return fill(resource, doFill, false)
    }

    /**
     * Fills a fluidstack into this capability.
     * The force parameter is used to internally manipulate it and ignores the type of the tank
     */
    fun fill(resource: FluidStack, doFill: Boolean, forced: Boolean): Int {
        if (!forced && tanktype == TankType.OUTPUT)
            return 0

        val lastFluid = currentFluid

        if (!allowedFluid.contains(resource.fluid.name) && allowedFluid.isNotEmpty()) {
            return 0
        }

        if (currentFluid != null && !currentFluid!!.isFluidEqual(resource)) {
            return 0
        }


        if (doFill) {
            if (currentFluid == null) {
                currentFluid = FluidStack(resource.fluid, 0)
            }

            val fill = min(resource.amount, capacity - currentFluid!!.amount)

            currentFluid!!.amount += fill

            markDirty(currentFluid != lastFluid)

            return fill
        } else {
            if (currentFluid == null) {
                return min(resource.amount, capacity)
            }

            return min(resource.amount, capacity - currentFluid!!.amount)
        }
    }

    override fun markDirty(needsClientRerender: Boolean) {
        if (fluidChangeThreshold != -1f) {
            if (currentFluid == null) {
                lastUpdatedFluidValue = 0
            } else {
                val change = abs(currentFluid!!.amount - lastUpdatedFluidValue)
                val percentage = abs(change / capacity.toFloat())

                if (percentage >= fluidChangeThreshold / 100f) {
                    lastUpdatedFluidValue = currentFluid!!.amount
                    super.markDirty(true)
                    return
                }
            }
        }

        super.markDirty(needsClientRerender)
    }

    override fun getTankProperties(): Array<IFluidTankProperties> {
        return simpleTankProperty
    }

    override fun deserializeNBT(nbt: NBTTagCompound?) {
        currentFluid = FluidStack.loadFluidStackFromNBT(nbt)
        capacity = nbt?.getInteger("Capacity") ?: 1000
    }

    override fun serializeNBT(): NBTTagCompound {
        val tag = NBTTagCompound()
        tag.setInteger("Amount", this.currentFluid?.amount ?: 0)
        tag.setInteger("Capacity", this.capacity)
        tag.setString("FluidName", if (currentFluid != null) FluidRegistry.getFluidName(this.currentFluid) else "")
        return tag
    }

    enum class TankType {
        INPUT, OUTPUT, BOTH
    }
}