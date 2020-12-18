package tfar.tanknull;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.wrapper.InvWrapper;
import tfar.tanknull.inventory.FluidHandlerHelper;

import javax.annotation.Nullable;

import java.util.stream.IntStream;

import static net.minecraftforge.fluids.capability.CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY;

public class DockBlock extends Block {

	public static final IntegerProperty TIER = IntegerProperty.create("tier", 0, 7);

	public static final VoxelShape EMPTY;

	public static final VoxelShape DOCKED;

	static {
		VoxelShape a1 = Block.makeCuboidShape(0,0,0,16,4,16);
		VoxelShape b1 = Block.makeCuboidShape(4,0,4,12,4,12);
		VoxelShape shape1 = VoxelShapes.combine(a1,b1, IBooleanFunction.NOT_SAME);

		VoxelShape a2 = Block.makeCuboidShape(0,12,0,16,16,16);
		VoxelShape b2 = Block.makeCuboidShape(4,12,4,12,16,12);
		VoxelShape shape2 = VoxelShapes.combine(a2,b2, IBooleanFunction.NOT_SAME);

		VoxelShape p1 = Block.makeCuboidShape(0,4,0,4,12,4);

		VoxelShape p2 = Block.makeCuboidShape(12,4,0,16,12,4);

		VoxelShape p3 = Block.makeCuboidShape(0,4,12,4,12,16);

		VoxelShape p4 = Block.makeCuboidShape(12,4,12,12,12,16);

		EMPTY = VoxelShapes.or(shape1,shape2,p1,p2,p3,p4);

		DOCKED = VoxelShapes.or(EMPTY,Block.makeCuboidShape(4,4,4,12,12,12));

	}

	public DockBlock(Properties p_i48440_1_) {
		super(p_i48440_1_);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		if (context.getEntity() instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity)context.getEntity();
			if (player.getHeldItemMainhand().getItem() instanceof TankNullItem)
				return DOCKED;
		}
		return state.get(TIER) > 0 ? DOCKED : EMPTY;
	}


	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult p_225533_6_) {
		if (!world.isRemote) {
			final TileEntity tile = world.getTileEntity(pos);
			if (tile instanceof TankNullDockBlockEntity) {
				TankNullDockBlockEntity tankNullDockBlockEntity = (TankNullDockBlockEntity) tile;
				int blockTier = state.get(DockBlock.TIER);
				ItemStack stack = player.getHeldItem(hand);
				if (stack.getItem() instanceof TankNullItem) {

					if (blockTier == 0) {
						((TankNullDockBlockEntity) tile).addTank(player.getHeldItem(hand));
						return ActionResultType.SUCCESS;
					}
				}

				if (stack.getCapability(FLUID_HANDLER_ITEM_CAPABILITY).isPresent()) {
					boolean isEmpty = stack.getCapability(FLUID_HANDLER_ITEM_CAPABILITY)
							.map(iFluidHandlerItem -> IntStream.range(0, iFluidHandlerItem.getTanks())
									.allMatch(i -> iFluidHandlerItem.getFluidInTank(i).isEmpty())).orElse(true);
					FluidActionResult result = isEmpty ? FluidUtil.tryFillContainerAndStow(stack, tankNullDockBlockEntity.handler, new InvWrapper(player.inventory), Integer.MAX_VALUE, player, true) :
							FluidUtil.tryEmptyContainerAndStow(stack, tankNullDockBlockEntity.handler, new InvWrapper(player.inventory), Integer.MAX_VALUE, player, true);
					if (result.isSuccess()) {
						player.setItemStackToSlot(hand == Hand.MAIN_HAND ? EquipmentSlotType.MAINHAND : EquipmentSlotType.OFFHAND,result.getResult());
					}
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
