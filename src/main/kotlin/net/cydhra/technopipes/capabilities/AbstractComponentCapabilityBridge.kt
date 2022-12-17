package net.cydhra.technopipes.capabilities

import net.cydhra.technocracy.foundation.api.ecs.IComponent
import net.cydhra.technocracy.foundation.api.ecs.tileentities.AbstractTileEntityComponent

/**
 * Superclass to capability handlers that can mark a [parent][AbstractTileEntityComponent] dirty upon mutation. To
 * mark a parent dirty, call [markDirty] with a flag whether the client needs to update rendering.
 */
abstract class AbstractComponentCapabilityBridge {
    lateinit var componentParent: IComponent

    open fun markDirty(needsClientRerender: Boolean = false) {
        componentParent.markDirty(needsClientRerender)
    }
}