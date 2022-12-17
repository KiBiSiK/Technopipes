package net.cydhra.technopipes.conduits.transit

import net.cydhra.technopipes.conduits.types.PipeType
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos

/**
 * A transit network edge that connects paths at cross sections
 */
class TransitCrossSectionEdge(pos: BlockPos) : TransitEdge(pos) {

    constructor(id: Int, type: PipeType, facing: EnumFacing, pos: BlockPos) : this(pos) {
        this.id = id
        this.type = type
        this.facing = facing
    }
}