package tfar.tanknull;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import tfar.tanknull.container.BlockTankNullMenu;
import tfar.tanknull.inventory.FluidStackHandler;
import tfar.tanknull.inventory.TankNullBlockFluidStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TankNullDockBlockEntity extends TileEntity implements INamedContainerProvider {

  public TankNullBlockFluidStackHandler handler = new TankNullBlockFluidStackHandler(0,0){
    @Override
    public void onContentsChanged() {
      super.onContentsChanged();
      markDirty();
    }
  };
  private LazyOptional<IFluidHandler> optional = LazyOptional.of(() -> handler);

  public TankNullDockBlockEntity() {
    super(RegistryObjects.blockentity.get());
  }
  public FluidStackHandler getHandler(){
    return handler;
  }

  @Override
  public void read(CompoundNBT compound) {
    loadRestorable(compound);
    super.read(compound);
  }

  public void loadRestorable(@Nullable CompoundNBT compound) {
    if (compound != null && compound.contains("fluidinv")) {
      CompoundNBT tanks = (CompoundNBT) compound.get("fluidinv");
      handler.deserializeNBT(tanks);
    }
  }

  @Nonnull
  @Override
  public CompoundNBT write(CompoundNBT compound) {
    CompoundNBT tanks = handler.serializeNBT();
    compound.put("fluidinv",tanks);
    return super.write(compound);
  }

  @Nonnull
  @Override
  public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
    return cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ? optional.cast() : super.getCapability(cap, side);
  }

  @Override
  public ITextComponent getDisplayName() {
    return new TranslationTextComponent("Tank Null");
  }

  @Nullable
  @Override
  public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
    return new BlockTankNullMenu(i,playerInventory,world,pos);
  }

  @Override
  public void markDirty() {
    super.markDirty();
    world.notifyBlockUpdate(pos,getBlockState(),getBlockState(),3);
  }

  @Override
  public SUpdateTileEntityPacket getUpdatePacket() {
    CompoundNBT nbt = new CompoundNBT();
    nbt.put("fluidinv",handler.serializeNBT());
    return new SUpdateTileEntityPacket(getPos(), 1, nbt);
  }

  @Override
  @Nonnull
  public CompoundNBT getUpdateTag() {
    CompoundNBT nbt = super.getUpdateTag();
    nbt.put("fluidinv",handler.serializeNBT());
    return nbt;
  }

  @Override
  public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
    handler.deserializeNBT((CompoundNBT)packet.getNbtCompound().get("fluidinv"));
  }

  @Override
  public void remove() {
    super.remove();
    optional.invalidate();
  }

  public void removeTank(){
    int tier = getBlockState().get(TankNullDockBlock.TIER);
    CompoundNBT nbt = handler.serializeNBT();
    world.setBlockState(pos,getBlockState().with(TankNullDockBlock.TIER,0));
    optional.invalidate();
    ItemStack stack = new ItemStack(Utils.getItem(tier));
    stack.getOrCreateTag().put("fluidinv",nbt);
    ItemEntity entity = new ItemEntity(world,pos.getX(),pos.getY(),pos.getZ(),stack);
    world.addEntity(entity);
  }

  public void addTank(ItemStack tank){
    if (tank.getItem() instanceof TankNullItem) {
      int tier = ((TankNullItem)tank.getItem()).tier;
      world.setBlockState(pos,getBlockState().with(TankNullDockBlock.TIER,tier));
      handler.setCapacity(Utils.getCapacity(tank)).setSize(Utils.getTanks(tank));
      handler.deserializeNBT(tank.getOrCreateTag().getCompound("fluidinv"));
      optional = LazyOptional.of(() -> handler);
      tank.shrink(1);
    }
  }

  @Override
  public void handleUpdateTag(CompoundNBT tag) {
    super.handleUpdateTag(tag);
    handler.deserializeNBT(tag.getCompound("fluidinv"));
  }
}
