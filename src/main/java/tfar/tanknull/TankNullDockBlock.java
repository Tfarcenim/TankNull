package tfar.tanknull;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import tfar.tanknull.inventory.FluidHandlerHelper;

import javax.annotation.Nullable;

public class TankNullDockBlock extends Block {

  public static final IntegerProperty TIER = IntegerProperty.create("tier",0,7);
  public TankNullDockBlock(Properties properties) {
    super(properties);
    setDefaultState(getDefaultState().with(TIER,0));
  }

  @Override
  public ActionResultType onBlockActivated(BlockState p_225533_1_, World world, BlockPos pos, PlayerEntity player, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
    if (!world.isRemote && p_225533_1_.get(TIER) > 0) {
      final TileEntity tile = world.getTileEntity(pos);
      if (tile instanceof TankNullDockBlockEntity) {
        if (player.isCrouching() && player.getHeldItem(p_225533_5_).isEmpty()){
          ((TankNullDockBlockEntity) tile).removeTank();
      } else {
          NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tile, tile.getPos());
        }
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
    return new TankNullDockBlockEntity();
  }

  @Override
  public boolean hasComparatorInputOverride(BlockState state) {
    return true;
  }

  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(TIER);
  }

  @Override
  public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
    TileEntity blockEntity = worldIn.getTileEntity(pos);
    return blockEntity instanceof TankNullDockBlockEntity ? FluidHandlerHelper.calcRedstoneFromInventory(((TankNullDockBlockEntity) blockEntity).handler) : 0;
  }
}
