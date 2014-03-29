package TFC.WorldGen;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.client.IRenderHandler;
import TFC.TFCBlocks;
import TFC.Core.TFC_Climate;
import TFC.Core.TFC_Core;
import TFC.Core.TFC_Time;
import TFC.TileEntities.TESeaWeed;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TFCProvider extends WorldProvider
{
	public IRenderHandler skyprovider;

	@Override
	protected void registerWorldChunkManager()
	{
		worldChunkMgr = new TFCWorldChunkManager(this.worldObj);
		TFC_Climate.manager = (TFCWorldChunkManager) worldChunkMgr;
		TFC_Climate.worldObj = worldObj;
	}

	@Override
	public IChunkProvider createChunkGenerator()
	{
		return new TFCChunkProviderGenerate(worldObj, worldObj.getSeed(), worldObj.getWorldInfo().isMapFeaturesEnabled());
	}

	@Override
	public boolean canCoordinateBeSpawn(int par1, int par2)
	{
		Block var3 = this.worldObj.getTopBlock(par1, par2);
		return TFC_Core.isGrass(var3);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getMoonPhase(long par1)
	{
		return (int)(par1 / TFC_Time.dayLength) % 8;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Vec3 getSkyColor(Entity cameraEntity, float partialTicks)
	{
		return worldObj.getSkyColorBody(cameraEntity, partialTicks);
	}

	@Override
	public float getCloudHeight()
	{
		return 256.0F;
	}

	@Override
	public BiomeGenBase getBiomeGenForCoords(int x, int z)
	{
		BiomeGenBase biome = TFCBiome.ocean;
		try
		{
			biome = worldObj.getBiomeGenForCoordsBody(x, z);
			if(canSnowAtTemp(x,145,z))
				biome.temperature = 0;
			else
				biome.temperature = 0.21f;
		}
		catch(Exception Ex)
		{
		}
		return biome;
	}

	@Override
	public ChunkCoordinates getRandomizedSpawnPoint()
	{
		TFCWorldChunkManager var2 = (TFCWorldChunkManager) worldChunkMgr;
		List var3 = var2.getBiomesToSpawnIn();
		long seed = worldObj.getWorldInfo().getSeed();
		Random var4 = new Random(seed);

		ChunkPosition chunkcoordinates = null;
		int xOffset = 0;
		int var6 = 0;
		int var7 = getAverageGroundLevel();
		int var8 = 10000;
		int startingZ = 3000 + var4.nextInt(12000);

		while(chunkcoordinates == null)
		{
			chunkcoordinates = var2.findBiomePosition(xOffset, -startingZ, 64, var3, var4);
			if (chunkcoordinates != null)
			{
				var6 = chunkcoordinates.chunkPosX;
				var8 = chunkcoordinates.chunkPosZ;
			}
			else
				xOffset += 512;
			//System.out.println("Unable to find spawn biome");
		}

		int var9 = 0;
		while (!canCoordinateBeSpawn(var6, var8))
		{
			var6 += var4.nextInt(64) - var4.nextInt(64);
			var8 += var4.nextInt(64) - var4.nextInt(64);
			++var9;

			if (var9 == 1000)
				break;
		}
		WorldInfo info = worldObj.getWorldInfo();
		info.setSpawnPosition(var6, this.worldObj.getHeightValue(var6, var8), var8);
		return new ChunkCoordinates(var6, this.worldObj.getHeightValue(var6, var8), var8);
	}

	@Override
	public ChunkCoordinates getSpawnPoint()
	{
		WorldInfo info = worldObj.getWorldInfo();
		if(info.getSpawnZ() > -2999)
			return getRandomizedSpawnPoint();
		return super.getSpawnPoint();
		//return new ChunkCoordinates(info.getSpawnX(), info.getSpawnY(), info.getSpawnZ());
	}

	@Override
	public boolean canBlockFreeze(int x, int y, int z, boolean byWater)
	{
		if (TFC_Climate.getHeightAdjustedTemp(x, y, z) <= 0)
		{
			Material mat = worldObj.getBlock(x, y, z).getMaterial();
			Block id = worldObj.getBlock(x,y,z);
			int meta = worldObj.getBlockMetadata(x, y, z);
			boolean salty = TFC_Core.isSaltWaterIncludeIce(id,meta,mat);
			TileEntity te = (worldObj.getTileEntity(x, y, z));
			if(te!=null && te instanceof TESeaWeed){
				//in case the block is salty sea grass, we don't want that to freeze when it's too warm
				salty = salty || (((TESeaWeed)te).getType()!=1 && ((TESeaWeed)te).getType()!=2);
			}
			if(TFC_Climate.getHeightAdjustedTemp(x, y, z) <= -2)
				salty = false;
			if((mat == Material.water || mat == Material.ice) && !salty)
			{
				if(id == TFCBlocks.FreshWaterStill || id == TFCBlocks.FreshWaterFlowing)
					worldObj.setBlock(x, y, z, Blocks.ice, 1, 2);
				else if(id == Blocks.water || id == Blocks.flowing_water)
					worldObj.setBlock(x, y, z, Blocks.ice, 0, 2);
				else if(id == Blocks.ice || id == TFCBlocks.SeaGrassFrozen)
				{
					worldObj.setBlock(x, y, z, id, meta, 1);
					te = (worldObj.getTileEntity(x, y, z));
					if(te!=null)
						((TESeaWeed)te).setType(meta);
				}
				else if(id == TFCBlocks.SeaGrassStill || id == TFCBlocks.SeaGrassFlowing)
				{
					int type = -1;
					if(te !=null)
						type = ((TESeaWeed)te).getType();
					worldObj.setBlock(x, y, z, TFCBlocks.SeaGrassFrozen,type,2);
					te = ((TESeaWeed)(worldObj.getTileEntity(x,y,z)));
					if(te!=null)
						((TESeaWeed)te).setType(type);
				}
				else
					worldObj.setBlock(x, y, z, Blocks.ice,0,2);
			}
			return false;//(mat == Material.water) && !salty;
		}
		return false;
	}

	//We use this in place of the vanilla method, for the vanilla, it allows us to stop it from doing things we don't like.
	public boolean canBlockFreezeTFC(int x, int y, int z, boolean byWater)
	{
		if (TFC_Climate.getHeightAdjustedTemp(x, y, z) <= 0)
		{
			Material mat = worldObj.getBlock(x, y, z).getMaterial();
			Block id = worldObj.getBlock(x,y,z);
			int meta = worldObj.getBlockMetadata(x, y, z);
			boolean salty = TFC_Core.isSaltWaterIncludeIce(id,meta,mat);
			TileEntity te = (worldObj.getTileEntity(x, y, z));
			if(te!=null && te instanceof TESeaWeed)
			{
				//in case the block is salty sea grass, we don't want that to freeze when it's too warm
				salty = salty || (((TESeaWeed)te).getType()!=1 && ((TESeaWeed)te).getType()!=2);
			}
			if(TFC_Climate.getHeightAdjustedTemp(x, y, z) <= -2)
				salty = false;
			return (mat == Material.water || mat == Material.ice) && !salty;
		}
		return false;
	}

	@Override
	public boolean canDoRainSnowIce(Chunk chunk)
	{
		return true;
	}

	@Override
	public boolean canSnowAt(int x, int y, int z, boolean checkLight)
	{
		Block id = worldObj.getBlock(x, y, z);
		if(TFC_Climate.getHeightAdjustedTemp(x, y, z) <= 0
				&& Blocks.snow.canPlaceBlockAt(worldObj, x, y, z) && worldObj.getBlock(x, y, z).getMaterial().isReplaceable())
			return true;
		return false;
	}

	private boolean canSnowAtTemp(int x, int y, int z)
	{
		if(TFC_Climate.getHeightAdjustedTemp(x, y, z) <= 0)
			return true;
		return false;
	}

	@Override
	public String getDimensionName()
	{
		return "DEFAULT";
	}

}