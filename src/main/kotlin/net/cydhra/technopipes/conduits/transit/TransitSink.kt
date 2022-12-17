package net.cydhra.technopipes.conduits.transit

import net.cydhra.technopipes.conduits.types.PipeContent
import net.cydhra.technopipes.conduits.types.PipeType
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.WorldServer

/**
 * A specialized kind of transit edge, that represents a connection between the conduit network and a machine or
 * storage interface.
 */
class TransitSink(pos: BlockPos) : TransitEdge(pos) {

    private var transferCoolDown = 0

    // TODO delegate to the tile entity that is represented by this sink, or to the edge
    val transferLimit: Int
        get() = when (this.type) {
            PipeType.ENERGY -> 20
            PipeType.FLUID -> 20
            PipeType.ITEM -> 16
            else -> 0
        }

    val cooldownModifier: Int
        get() = when (this.type) {
            PipeType.ENERGY, PipeType.FLUID -> 1
            PipeType.ITEM -> 20
            else -> 4
        }

    constructor(id: Int, type: PipeType, facing: EnumFacing, pos: BlockPos) : this(pos) {
        this.id = id
        this.type = type
        this.facing = facing
    }

    fun offersContent(world: WorldServer): Boolean {
        if (transferCoolDown > 0) {
            return false
        }

        return this.type.offersContent(world, this.pos.offset(facing), facing.opposite)
    }

    fun acceptsContent(world: WorldServer, content: PipeContent): Boolean {
        if (transferCoolDown > 0) {
            return false
        }

        return this.type.acceptContent(world, this.pos.offset(facing), facing.opposite, content, true) != content
    }

    /**
     * Tick this sink. This will just tick down the transfer cool-down
     */
    fun tick() {
        if (transferCoolDown > 0)
            transferCoolDown--
    }

    fun getContent(world: WorldServer): PipeContent {
        return this.type.getContent(world, this.pos.offset(facing), facing.opposite, this.transferLimit)
    }

    /**
     * Transfer content to this sink and return the remaining content (may be empty). The content source will be
     * drained by the amount actually transferred.
     *
     * @param world the world where this sink is located
     * @param content the content to transfer into this sink
     *
     * @return the remaining content that could not be transferred into this sink
     */
    fun transferContent(world: WorldServer, content: PipeContent): PipeContent {
        if (transferCoolDown > 0)
            return content

        // TODO: obey transfer limits
        val remainingContent = this.type.acceptContent(world, this.pos.offset(facing), facing.opposite, content, false)
        if (remainingContent != content) {
            this.setCoolDown(world)
            content.drainSourceUntil(remainingContent)
        }

        return remainingContent
    }

    /**
     * Set this sink to cooldown. This is done when content is transferred from or to this sink.
     */
    fun setCoolDown(world: WorldServer) {
        // TODO delegate the cooldown modifier to the sink
        this.transferCoolDown = this.cooldownModifier
    }
}
