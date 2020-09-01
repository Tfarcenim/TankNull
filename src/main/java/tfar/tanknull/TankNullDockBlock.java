package tfar.tanknull;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.network.NetworkHooks;
import tfar.tanknull.inventory.FluidHandlerHelper;
import tfar.tanknull.inventory.TankNullBlockFluidStackHandler;

import javax.annotation.Nullable;

import static net.minecraftforge.fluids.capability.CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY;

public class TankNullDockBlock extends Block {

	public static final IntegerProperty TIER = IntegerProperty.create("tier", 0, 7);

	public TankNullDockBlock(Properties properties) {
		super(properties);
		setDefaultState(getDefaultState().with(TIER, 0));
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult p_225533_6_) {
		if (!world.isRemote) {
			final TileEntity tile = world.getTileEntity(pos);
			if (tile instanceof TankNullDockBlockEntity) {
				TankNullDockBlockEntity tankNullDockBlockEntity = (TankNullDockBlockEntity) tile;
				int blockTier = state.get(TankNullDockBlock.TIER);
				ItemStack stack = player.getHeldItem(hand);
				if (stack.getItem() instanceof TankNullItem) {

					if (blockTier == 0) {
						((TankNullDockBlockEntity) tile).addTank(player.getHeldItem(hand));
						return ActionResultType.SUCCESS;
					}
				} boolean drained = stack.getCapability(FLUID_HANDLER_ITEM_CAPABILITY)
								.map(iFluidHandlerItem -> processFluidItemHandler(stack,iFluidHandlerItem,tankNullDockBlockEntity.handler))
								.orElse(false);
					if (drained) {
						return ActionResultType.SUCCESS;
				}

				if (player.isCrouching() && player.getHeldItem(hand).isEmpty() && blockTier > 0) {
					((TankNullDockBlockEntity) tile).removeTank();
				} else {
					NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tile, ((TankNullDockBlockEntity) tile)::writeFluids);
				}
			}
		}
		return ActionResultType.SUCCESS;
	}

	public boolean processFluidItemHandler(ItemStack stack, IFluidHandlerItem iFluidHandlerItem, TankNullBlockFluidStackHandler tankBlock) {
		int tanks = iFluidHandlerItem.getTanks();
		for (int i = 0; i < tanks; i++) {
			FluidStack fluidStackInItem = iFluidHandlerItem.getFluidInTank(i);
			for (int j = 0; j < tankBlock.getTanks(); j++) {
				FluidStack tankBlockFluidStack = tankBlock.getFluidInTank(j);
				if (!fluidStackInItem.isEmpty())
					if (tankBlockFluidStack.isEmpty() ||
									fluidStackInItem.isFluidEqual(tankBlockFluidStack)) {
						FluidStack drain = iFluidHandlerItem.drain(tankBlock.getTankCapacity(j), IFluidHandler.FluidAction.SIMULATE);
						if (!drain.isEmpty()) {
							int fill = tankBlock.fill(drain, IFluidHandler.FluidAction.EXECUTE);
							if (fill > 0) {
								iFluidHandlerItem.drain(fill, IFluidHandler.FluidAction.EXECUTE);
							}
						}
					}
			}
		}
		return true;
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
