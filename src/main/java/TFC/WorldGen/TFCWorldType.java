package TFC.WorldGen;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.FlatGeneratorInfo;

public class TFCWorldType extends WorldType
{
	public static TFCWorldType DEFAULT;
	public static TFCWorldType FLAT;

//	private static final  BiomeGenBase[] tfcBiomes = new BiomeGenBase[] {
//		TFCBiome.HighHills, TFCBiome.swampland, TFCBiome.plains,
//		TFCBiome.plains, TFCBiome.rollingHills, TFCBiome.Mountains };
	private static final  BiomeGenBase[] tfcBiomes = new BiomeGenBase[] {
		TFCBiome.ocean,
		TFCBiome.river,
//		TFCBiome.hell,
		TFCBiome.beach,
		TFCBiome.jungle,
		TFCBiome.jungleHills,
		TFCBiome.desert,
		TFCBiome.HighHills,
		TFCBiome.forest,
		TFCBiome.plains,
		TFCBiome.taiga,
		TFCBiome.swampland,
		TFCBiome.HighHillsEdge,
		TFCBiome.rollingHills,
		TFCBiome.Mountains,
		TFCBiome.MountainsEdge,
		TFCBiome.MountainsSeismic,
		TFCBiome.MountainsEdgeSeismic,
		TFCBiome.PlainsSeismic
	};
	
	public TFCWorldType(String par2Str)
	{
		super(par2Str);
	}

	//@Override // ??Was commented out in WorldType??
	public BiomeGenBase[] getBiomesForWorldType()
	{
		BiomeGenBase[] bgb0 = null;
		BiomeGenBase[] bgb = TFCBiome.getBiomeGenArray();
		for(int i=0; i < bgb.length; i++)
		{
			if(!bgb[i].biomeName.equalsIgnoreCase("hell"))
			{
				bgb0[i] = bgb[i];
				System.out.println(bgb[i].biomeID+" : "+bgb[i].biomeName);
			}
		}
		return bgb0;
	}

	@Override
	public WorldChunkManager getChunkManager(World world)
	{
		if (this == FLAT)
		{
//			FlatGeneratorInfo var1 = FlatGeneratorInfo.createFlatGeneratorFromString(world.getWorldInfo().getGeneratorOptions());
//			return new TFCWorldChunkManagerHell(BiomeGenBase.getBiome(var1.getBiome()), 0.5F, 0.5F);
			return new TFCWorldChunkManagerHell(TFCBiome.getBiome(TFCBiome.GetBiomeByName("hell").biomeID), 0.5F, 0.5F);
		}
		else
			return new TFCWorldChunkManager(world);
	}

	@Override
	public IChunkProvider getChunkGenerator(World world, String generatorOptions)
	{
		return new TFCChunkProviderGenerate(world, world.getSeed(), world.getWorldInfo().isMapFeaturesEnabled());
	}

	@Override
	public int getMinimumSpawnHeight(World world)
	{
		return 145;
	}

	@Override
	public double getHorizon(World world)
	{
		return 144.0D;
	}

}