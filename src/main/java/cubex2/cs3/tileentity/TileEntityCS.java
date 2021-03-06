package cubex2.cs3.tileentity;

import cubex2.cs3.api.scripting.ITriggerData;
import cubex2.cs3.api.scripting.TriggerType;
import cubex2.cs3.common.BaseContentPack;
import cubex2.cs3.common.BaseContentPackLoader;
import cubex2.cs3.common.WrappedTileEntity;
import cubex2.cs3.common.scripting.TriggerData;
import cubex2.cs3.registry.TileEntityRegistry;
import cubex2.cs3.tileentity.attributes.TileEntityAttributes;
import cubex2.cs3.util.JavaScriptHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;


public class TileEntityCS extends TileEntity
{

    // 豆焰写的
    public double BSize;
    public double BRotate;

    public ResourceLocation BTexture = new ResourceLocation("cs3",
            "textures/blocks/magic_circle.png");

    public IModelCustom BModel = AdvancedModelLoader.loadModel(new ResourceLocation("cs3",
            "obj/magic_circle.obj"));

    

    protected WrappedTileEntity wrappedTileEntity;
    protected TileEntityAttributes container;

    private NBTTagCompound tileCompound;

    public TileEntityCS(WrappedTileEntity wrappedTileEntity)
    {
        this.wrappedTileEntity = wrappedTileEntity;
        container = wrappedTileEntity.container;
    }

    public TileEntityCS()
    {

    }

    public NBTTagCompound getCompound()
    {
        if (tileCompound == null)
        {
            tileCompound = new NBTTagCompound();
        }
        return tileCompound;
    }

    @Override
    public void updateEntity()
    {

        //this.BSize;//=1.0;
        //this.BRotate;// = ((int)System.currentTimeMillis()%360000)/100D;


        if (container.onUpdate != null && container.onUpdate.script != null)
        {
            ITriggerData data = new TriggerData("onUpdate", TriggerType.TILE_ENTITY).setWorld(worldObj).setPosition(xCoord, yCoord, zCoord);
            JavaScriptHelper.executeTrigger(container.onUpdate.script, data, container.getPack());
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readFromNBT(nbttagcompound);

        if (wrappedTileEntity == null && nbttagcompound.hasKey("PackName"))
        {
            BaseContentPack pack = BaseContentPackLoader.instance().getContentPack(nbttagcompound.getString("PackName"));
            TileEntityRegistry registry = (TileEntityRegistry) pack.getContentRegistry(WrappedTileEntity.class);
            wrappedTileEntity = registry.getTileEntity(nbttagcompound.getString("ContainerName"));
            container = wrappedTileEntity.container;
        }

        if (nbttagcompound.hasKey("TileTag"))
        {
            tileCompound = (NBTTagCompound) nbttagcompound.getTag("TileTag");
        } else
        {
            tileCompound = new NBTTagCompound();
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeToNBT(nbttagcompound);

        if (wrappedTileEntity != null)
        {
            nbttagcompound.setString("PackName", wrappedTileEntity.getPack().getName());
            nbttagcompound.setString("ContainerName", wrappedTileEntity.getName());
        }

        if (tileCompound != null)
        {
            nbttagcompound.setTag("TileTag", tileCompound);
        }
    }

    @Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        writeToNBT(nbt);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
    {
        readFromNBT(pkt.func_148857_g());
    }
}
