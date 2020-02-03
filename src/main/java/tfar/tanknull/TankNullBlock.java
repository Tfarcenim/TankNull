package tfar.tanknull;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.network.NetworkHooks;
import tfar.tanknull.inventory.FluidHandlerHelper;

import javax.annotation.Nullable;
import java.util.List;

public class TankNullBlock extends Block {
  public final int tier;

  public TankNullBlock(Properties properties, int tier) {
    super(properties);
    this.tier = tier;
  }

  @Override
  public ActionResultType onBlockActivated(BlockState p_225533_1_, World world, BlockPos pos, PlayerEntity player, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
    if (!world.isRemote) {
      final TileEntity tile = world.getTileEntity(pos);
      if (tile instanceof INamedContainerProvider) {
        NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tile, tile.getPos());

      }
    }
    return ActionResultType.SUCCESS;
  }

  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Nullable
  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    return new TankNullBlockEntity();
  }

  @Override
  public void onBlockPlacedBy(World world, BlockPos pos, BlockState p_180633_3_, @Nullable LivingEntity p_180633_4_, ItemStack stack) {
    TileEntity blockentity = world.getTileEntity(pos);
    if (blockentity instanceof TankNullBlockEntity) {
      TankNullBlockEntity dankBlockEntity = (TankNullBlockEntity) blockentity;
      dankBlockEntity.handler.setSize(Utils.getTanks(this)).setCapacity(Utils.getCapacity(this));
      dankBlockEntity.loadRestorable(stack.getTag());
    }
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void addInformation(ItemStack stack, @Nullable IBlockReader p_190948_2_, List<ITextComponent> tooltip, ITooltipFlag p_190948_4_) {
    if (stack.hasTag() && Utils.DEV) tooltip.add(new StringTextComponent(stack.getTag().toString()));
    CompoundNBT nbt = stack.getTag();
    if (nbt != null && !nbt.getCompound("fluidinv").isEmpty()) {
      ITextComponent component = nbt.getBoolean("fill") ? new StringTextComponent("Filling")
              : new StringTextComponent("Emptying");
      tooltip.add(component);
      ListNBT tagList = nbt.getList("Fluids", Constants.NBT.TAG_COMPOUND);
      for (int i = 0; i < tagList.size(); i++) {
        CompoundNBT fluidTags = tagList.getCompound(i);
        int slot = fluidTags.getInt("Tank");
        FluidStack fluidStack = FluidStack.loadFluidStackFromNBT(fluidTags);
        if (!fluidStack.isEmpty()) {
          tooltip.add(new StringTextComponent("Tank " + slot + ": " + fluidStack.getAmount() + " " + fluidStack.getDisplayName().getFormattedText()));
        }
      }
    }
  }

  @Override
  public boolean hasComparatorInputOverride(BlockState state) {
    return true;
  }

  @Override
  public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
    TileEntity blockEntity = worldIn.getTileEntity(pos);
    return blockEntity instanceof TankNullBlockEntity ? FluidHandlerHelper.calcRedstoneFromInventory(((TankNullBlockEntity) blockEntity).handler) : 0;
  }
}
