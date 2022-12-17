package net.cydhra.technopipes.capabilities

import net.minecraft.item.ItemStack


interface IItemCapability {
    fun getContainer(): ItemStack
}