package com.bioxx.tfc.Render.Blocks;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import com.bioxx.tfc.TFCBlocks;
import com.bioxx.tfc.TileEntities.TEBarrel;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class RenderBarrel implements ISimpleBlockRenderingHandler
{
	static float min = 0.1F;
	static float max = 0.9F;

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
	{
		TEBarrel te = (TEBarrel) world.getTileEntity(x, y, z);
		Block planksBlock;
		Block lidBlock;
		if(block == TFCBlocks.Barrel)
		{
			planksBlock = TFCBlocks.Planks;
			lidBlock = TFCBlocks.WoodSupportH;
		}
		else
		{
			planksBlock = TFCBlocks.Planks2;
			lidBlock = TFCBlocks.WoodSupportH2;
		}
		renderer.renderAllFaces = true;

		if((te.rotation & -128) == 0)
		{
			if(te.getSealed())
			{
				renderer.setRenderBounds(min+0.05F, min, min+0.05F, max-0.05F, 0.95F, max-0.05F);
			}
			else
			{
				renderer.setRenderBounds(min+0.05F, min, min+0.05F, max-0.05F, min+0.05F, max-0.05F);
			}
			renderer.renderStandardBlock(lidBlock, x, y, z);
			renderer.setRenderBounds(min, 0F, min+0.05F, min+0.05F, 1F, max-0.05F);
			rotate(renderer, 1);
			renderer.renderStandardBlock(planksBlock, x, y, z);
			rotate(renderer, 0);
			renderer.renderStandardBlock(block, x, y, z);

			renderer.setRenderBounds(max-0.05F, 0F, min+0.05F, max, 1F, max-0.05F);
			rotate(renderer, 1);
			renderer.renderStandardBlock(planksBlock, x, y, z);
			rotate(renderer, 0);
			renderer.renderStandardBlock(block, x, y, z);

			renderer.setRenderBounds(min, 0F, min, max, 1F, min+0.05F);
			rotate(renderer, 1);
			renderer.renderStandardBlock(planksBlock, x, y, z);
			rotate(renderer, 0);
			renderer.renderStandardBlock(block, x, y, z);

			renderer.setRenderBounds(min, 0F, max-0.05F, max, 1F, max);
			rotate(renderer, 1);
			renderer.renderStandardBlock(planksBlock, x, y, z);
			rotate(renderer, 0);
			renderer.renderStandardBlock(block, x, y, z);
		}
		else
		{
			if((te.rotation & 3) == 0)
			{
				renderer.setRenderBounds(min, min, min+0.05F, 0.95F, min+0.05F, max-0.05F);
				renderer.renderStandardBlock(lidBlock, x, y, z);
			}
			if((te.rotation & 3) == 1)
			{
				renderer.setRenderBounds(min+0.05F, min, min,max-0.05F, min+0.05F, 0.95F);
				renderer.renderStandardBlock(lidBlock, x, y, z);
			}
		}


		renderer.renderAllFaces = false;

		return true;
	}

	public void rotate(RenderBlocks renderer, int i)
	{
		renderer.uvRotateEast = i;
		renderer.uvRotateWest = i;
		renderer.uvRotateNorth = i;
		renderer.uvRotateSouth = i;
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer)
	{
		Block planksBlock;
		Block lidBlock;
		if(block == TFCBlocks.Barrel)
		{
			planksBlock = TFCBlocks.Planks;
			lidBlock = TFCBlocks.WoodSupportH;
		}
		else
		{
			planksBlock = TFCBlocks.Planks2;
			lidBlock = TFCBlocks.WoodSupportH2;
		}

		renderer.setRenderBounds(min+0.05F, min, min+0.05F, max-0.05F, 0.95F, max-0.05F);
		rotate(renderer, 1);
		renderInvBlock(lidBlock, metadata, renderer);

		renderer.setRenderBounds(min, 0F, min+0.05F, min+0.05F, 1F, max-0.05F);
		rotate(renderer, 1);
		renderInvBlock(planksBlock, metadata, renderer);
		rotate(renderer, 0);
		renderInvBlock(block, metadata, renderer);

		renderer.setRenderBounds(max-0.05F, 0F, min+0.05F, max, 1F, max-0.05F);
		rotate(renderer, 1);
		renderInvBlock(planksBlock, metadata, renderer);
		rotate(renderer, 0);
		renderInvBlock(block, metadata, renderer);

		renderer.setRenderBounds(min, 0F, min, max, 1F, min+0.05F);
		rotate(renderer, 1);
		renderInvBlock(planksBlock, metadata, renderer);
		rotate(renderer, 0);
		renderInvBlock(block, metadata, renderer);

		renderer.setRenderBounds(min, 0F, max-0.05F, max, 1F, max);
		rotate(renderer, 1);
		renderInvBlock(planksBlock, metadata, renderer);
		rotate(renderer, 0);
		renderInvBlock(block, metadata, renderer);
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId)
	{
		return true;
	}

	@Override
	public int getRenderId()
	{
		return 0;
	}

	public static void renderInvBlock(Block block, int m, RenderBlocks renderer)
	{
		Tessellator var14 = Tessellator.instance;
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		var14.startDrawingQuads();
		var14.setNormal(0.0F, -1.0F, 0.0F);
		renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(0, m));
		var14.draw();
		var14.startDrawingQuads();
		var14.setNormal(0.0F, 1.0F, 0.0F);
		renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(1, m));
		var14.draw();
		var14.startDrawingQuads();
		var14.setNormal(0.0F, 0.0F, -1.0F);
		renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(2, m));
		var14.draw();
		var14.startDrawingQuads();
		var14.setNormal(0.0F, 0.0F, 1.0F);
		renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(3, m));
		var14.draw();
		var14.startDrawingQuads();
		var14.setNormal(-1.0F, 0.0F, 0.0F);
		renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(4, m));
		var14.draw();
		var14.startDrawingQuads();
		var14.setNormal(1.0F, 0.0F, 0.0F);
		renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(5, m));
		var14.draw();
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	}
}
