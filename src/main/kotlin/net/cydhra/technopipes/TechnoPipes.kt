package net.cydhra.technopipes

import net.minecraft.block.Block
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent.Register
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent
import net.minecraftforge.fml.event.server.FMLServerStartingEvent
import org.apache.logging.log4j.LogManager
import thedarkcolour.kotlinforforge.forge.MOD_CONTEXT

// The value here should match an entry in the META-INF/mods.toml file
@Mod("technopipes")
class TechnoPipes {
    init {
        MOD_CONTEXT.getKEventBus().addListener { event: FMLCommonSetupEvent -> setup(event) }
        MOD_CONTEXT.getKEventBus().addListener { event: InterModEnqueueEvent -> enqueueIMC(event) }
        MOD_CONTEXT.getKEventBus().addListener { event: InterModProcessEvent -> processIMC(event) }
        MOD_CONTEXT.getKEventBus().addListener { event: FMLClientSetupEvent -> clientSetup(event) }

        MinecraftForge.EVENT_BUS.register(this)
    }

    private fun setup(event: FMLCommonSetupEvent) {

    }

    private fun clientSetup(event: FMLClientSetupEvent) {

    }

    private fun enqueueIMC(event: InterModEnqueueEvent) {

    }

    private fun processIMC(event: InterModProcessEvent) {

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    fun onServerStarting(event: FMLServerStartingEvent) {

    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
    object RegistryEvents {
        @SubscribeEvent
        fun onBlocksRegistry(blockRegistryEvent: Register<Block?>?) {

        }
    }

    companion object {
        private val LOGGER = LogManager.getLogger()
    }
}