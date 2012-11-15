package TFC.Entities.Mobs;

import TFC.*;
import TFC.Core.TFC_Time;
import TFC.Core.TFC_Settings;
import TFC.Entities.EntityAnimalTFC;
import TFC.Entities.AI.EntityAIEatGrassTFC;
import TFC.Entities.AI.EntityAIMateTFC;
import net.minecraft.src.EntityAIFollowParent;
import net.minecraft.src.EntityAILookIdle;
import net.minecraft.src.EntityAIPanic;
import net.minecraft.src.EntityAISwimming;
import net.minecraft.src.EntityAITempt;
import net.minecraft.src.EntityAIWander;
import net.minecraft.src.EntityAIWatchClosest;
import net.minecraft.src.EntityAnimal;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.World;

public class EntityChickenTFC extends EntityAnimalTFC
{
    public boolean field_753_a = false;
    public float field_752_b = 0.0F;
    public float destPos = 0.0F;
    public float field_757_d;
    public float field_756_e;
    public float field_755_h = 1.0F;

    /** The time until the next egg is spawned. */
    public int timeUntilNextEgg;
    private EntityAIEatGrassTFC aiEatGrass = new EntityAIEatGrassTFC(this);
    public EntityChickenTFC(World par1World)
    {
        super(par1World);
        this.texture = "/mob/chicken.png";
        //this.setSize(0.3F, 0.7F);
        this.timeUntilNextEgg = this.rand.nextInt(6000) + 6000;
        float var2 = 0.25F;
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIPanic(this, 0.38F));
        this.tasks.addTask(2, new EntityAIMateTFC(this, var2));
        this.tasks.addTask(3, new EntityAITempt(this, 0.25F, TFCItems.WheatGrain.shiftedIndex, false));
        this.tasks.addTask(4, new EntityAIFollowParent(this, 0.28F));
        this.tasks.addTask(5, this.aiEatGrass);
        this.tasks.addTask(6, new EntityAIWander(this, var2));
        this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));
    }
    public EntityChickenTFC(World par1World,EntityAnimalTFC mother, float F_size)
	{
    	super(par1World,mother,F_size);
    	this.texture = "/mob/chicken.png";
        //this.setSize(0.3F, 0.7F);
        this.timeUntilNextEgg = this.rand.nextInt(6000) + 6000;
        float var2 = 0.25F;
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIPanic(this, 0.38F));
        this.tasks.addTask(2, new EntityAIMateTFC(this, var2));
        this.tasks.addTask(3, new EntityAITempt(this, 0.25F, TFCItems.WheatGrain.shiftedIndex, false));
        this.tasks.addTask(4, new EntityAIFollowParent(this, 0.28F));
        this.tasks.addTask(5, this.aiEatGrass);
        this.tasks.addTask(6, new EntityAIWander(this, var2));
        this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));
	}

    /**
     * Returns true if the newer Entity AI code should be run
     */
    public boolean isAIEnabled()
    {
        return true;
    }

    public int getMaxHealth()
    {
        return 200;
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        super.onLivingUpdate();
        
        float t = (1.0F-(getGrowingAge()/(TFC_Time.getYearRatio() * adultAge * -TFC_Settings.dayLength)));
        setSize(0.7F*t,0.7F*t);
        
        this.field_756_e = this.field_752_b;
        this.field_757_d = this.destPos;
        this.destPos = (float)((double)this.destPos + (double)(this.onGround ? -1 : 4) * 0.3D);
        if(pregnant){
			if(TFC_Time.getTotalTicks() >= conception + pregnancyTime*TFC_Settings.dayLength){
				int i = rand.nextInt(4) + 9;
				for (int x = 0; x<i;x++){
				EntityChickenTFC baby = new EntityChickenTFC(worldObj, this,mateSizeMod);
				giveBirth(baby);
				}
				pregnant = false;
			}
		}

        if (this.destPos < 0.0F)
        {
            this.destPos = 0.0F;
        }

        if (this.destPos > 1.0F)
        {
            this.destPos = 1.0F;
        }

        if (!this.onGround && this.field_755_h < 1.0F)
        {
            this.field_755_h = 1.0F;
        }

        this.field_755_h = (float)((double)this.field_755_h * 0.9D);

        if (!this.onGround && this.motionY < 0.0D)
        {
            this.motionY *= 0.6D;
        }

        this.field_752_b += this.field_755_h * 2.0F;

        if (!this.isChild() && !this.worldObj.isRemote && --this.timeUntilNextEgg <= 0)
        {
            this.worldObj.playSoundAtEntity(this, "mob.chicken.plop", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            this.dropItem(Item.egg.shiftedIndex, 1);
            this.timeUntilNextEgg = this.rand.nextInt(6000) + 6000;
        }
    }

    /**
     * Called when the mob is falling. Calculates and applies fall damage.
     */
    protected void fall(float par1) {}

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    protected String getLivingSound()
    {
        return "mob.chicken.say";
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound()
    {
        return "mob.chicken.hurt";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound()
    {
        return "mob.chicken.hurt";
    }

    /**
     * Returns the item ID for the item the mob drops on death.
     */
    protected int getDropItemId()
    {
        return Item.feather.shiftedIndex;
    }

    /**
     * Drop 0-2 items of this living's type
     */
    protected void dropFewItems(boolean par1, int par2)
    {
        int var3 = 1;

        for (int var4 = 0; var4 < var3; ++var4)
        {
            this.dropItem(Item.feather.shiftedIndex,(int) (this.size_mod * (5+this.rand.nextInt(10))));
        }

        if (this.isBurning())
        {
            this.dropItem(Item.chickenCooked.shiftedIndex, 1);
        }
        else
        {
            this.dropItem(Item.chickenRaw.shiftedIndex, 1);
        }
    }

    /**
     * This function is used when two same-species animals in 'love mode' breed to generate the new baby animal.
     */
    public EntityAnimal spawnBabyAnimal(EntityAnimal par1EntityAnimal)
    {
        return new EntityChickenTFC(this.worldObj);
    }
}
