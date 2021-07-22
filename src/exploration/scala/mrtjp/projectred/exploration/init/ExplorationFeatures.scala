package mrtjp.projectred.exploration.init

import mrtjp.projectred.ProjectRedExploration.MOD_ID
import mrtjp.projectred.core.Configurator
import mrtjp.projectred.exploration.ExplorationContent._
import net.minecraft.block.Block
import net.minecraft.util.ResourceLocation
import net.minecraft.util.registry.{Registry, WorldGenRegistries}
import net.minecraft.world.gen.GenerationStage
import net.minecraft.world.gen.feature.{ConfiguredFeature, Feature, OreFeatureConfig}
import net.minecraft.world.gen.placement.{Placement, TopSolidRangeConfig}
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.world.BiomeLoadingEvent
import net.minecraftforge.eventbus.api.EventPriority

import java.util.function.Supplier

object ExplorationFeatures
{
    var oreRuby:ConfiguredFeature[_, _] = _
    var oreSapphire:ConfiguredFeature[_, _] = _
    var orePeridot:ConfiguredFeature[_, _] = _
    var oreCopper:ConfiguredFeature[_, _] = _
    var oreTin:ConfiguredFeature[_, _] = _
    var oreSilver:ConfiguredFeature[_, _] = _
    var oreElectrotine:ConfiguredFeature[_, _] = _

    def load():Unit = {
        oreRuby = registerStandardOre("ruby_ore", blockRubyOre, 8, 12, 20, 1)
        oreSapphire = registerStandardOre("sapphire_ore", blockSapphireOre, 8, 12, 20, 1)
        orePeridot = registerStandardOre("peridot_dore", blockPeridotOre, 10, 18, 26, 1)

        oreCopper = registerStandardOre("ore_copper", blockCopperOre, 8, 0, 64, 16)
        oreTin = registerStandardOre("ore_tin", blockTinOre, 8, 0, 48, 10)
        oreSilver = registerStandardOre("ore_silver", blockSilverOre, 9, 0, 32, 8)

        oreElectrotine = registerStandardOre("ore_electrotine", blockElectrotineOre, 8, 0, 16, 4)

        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, onBiomeLoadingEvent)
    }

    def onBiomeLoadingEvent(event:BiomeLoadingEvent):Unit = {
        val c = event.getCategory
        import net.minecraft.world.biome.Biome.Category._

        def addOre(cf:ConfiguredFeature[_, _]):Unit = {
            event.getGeneration.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, cf)
        }

        if (c != NONE && c != THEEND && c != NETHER) { // If overworld
            import Configurator._
            if (gen_Ruby)        addOre(oreRuby)
            if (gen_Sapphire)    addOre(oreSapphire)
            if (gen_Peridot)     addOre(orePeridot)
            if (gen_Copper)      addOre(oreCopper)
            if (gen_Tin)         addOre(oreTin)
            if (gen_Silver)      addOre(oreSilver)
            if (gen_Electrotine) addOre(oreElectrotine)
        }
    }

    private def registerStandardOre(key:String, block:Supplier[Block], veinSize:Int, minY:Int, maxY:Int, count:Int):ConfiguredFeature[_, _] = {
        val cf = Feature.ORE.configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, block.get().defaultBlockState(), veinSize))
                .squared()
                .count(count)
        val cf2:ConfiguredFeature[_, _] = cf.decorated(Placement.RANGE.configured(new TopSolidRangeConfig(minY, minY, maxY))) //Scalac bug requires this on separate line
        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, new ResourceLocation(MOD_ID, key), cf2)
    }
}
