package tfar.tanknull;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.network.NetworkHooks;
import tfar.tanknull.container.NamedMenuProvider;
import tfar.tanknull.inventory.TankNullItemStackFluidStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TankNullItem extends BlockItem {
  public TankNullItem(Block blockIn, Properties builder) {
    super(blockIn, builder);
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
    ItemStack itemstack = player.getHeldItem(hand);
    if (!world.isRemote) {
      if (Utils.getMode(itemstack) == UseMode.ITEM) {
        NetworkHooks.openGui((ServerPlayerEntity) player, new NamedMenuProvider(itemstack));
        return super.onItemRightClick(world, player, hand);
      } else {
        TankNullItemStackFluidStackHandler handler = TankNullItemStackFluidStackHandler.create(itemstack);
        boolean filling = handler.fill;
        if (filling) {
          FluidStack selectedFluidStack = handler.getFluidInTank(handler.selectedTank);
          RayTraceResult raytraceresult = rayTrace(world, player, RayTraceContext.FluidMode.SOURCE_ONLY);
          ActionResult<ItemStack> ret = net.minecraftforge.event.ForgeEventFactory.onBucketUse(player, world, itemstack, raytraceresult);
          if (ret != null) return ret;
          if (raytraceresult.getType() == RayTraceResult.Type.MISS) {
            return ActionResult.func_226250_c_(itemstack);
          } else if (raytraceresult.getType() != RayTraceResult.Type.BLOCK) {
            return ActionResult.func_226250_c_(itemstack);
          } else {
            BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult) raytraceresult;
            BlockPos blockpos = blockraytraceresult.getPos();
            Direction direction = blockraytraceresult.getFace();
            BlockPos blockpos1 = blockpos.offset(direction);
            if (world.isBlockModifiable(player, blockpos) && player.canPlayerEdit(blockpos1, direction, itemstack)) {
              if (selectedFluidStack.isEmpty()) {
                BlockState blockstate1 = world.getBlockState(blockpos);
                if (blockstate1.getBlock() instanceof IBucketPickupHandler) {
                  Fluid fluid = ((IBucketPickupHandler) blockstate1.getBlock()).pickupFluid(world, blockpos, blockstate1);
                  if (fluid != Fluids.EMPTY) {
                    player.addStat(Stats.ITEM_USED.get(this));

                    SoundEvent soundevent = selectedFluidStack.getFluid().getAttributes().getEmptySound();
                    if (soundevent == null)
                      soundevent = fluid.isIn(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_FILL_LAVA : SoundEvents.ITEM_BUCKET_FILL;
                    player.playSound(soundevent, 1.0F, 1.0F);

                    return ActionResult.func_226248_a_(this.fillBucket(fluid, itemstack, handler, player));
                  }
                }

                return ActionResult.func_226251_d_(itemstack);
              } else {
                BlockState blockstate = world.getBlockState(blockpos);
                BlockPos blockpos2 = blockstate.getBlock() instanceof ILiquidContainer && selectedFluidStack.getFluid() == Fluids.WATER ? blockpos : blockpos1;
                if (this.tryPlaceContainedLiquid(player, world, blockpos2, blockraytraceresult, selectedFluidStack)) {
                  this.onLiquidPlaced(world, itemstack, blockpos2);
                  if (player instanceof ServerPlayerEntity) {
                    CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity) player, blockpos2, itemstack);
                  }

                  player.addStat(Stats.ITEM_USED.get(this));
                  return ActionResult.func_226248_a_(this.emptyBucket(itemstack, handler, player));
                } else {
                  return ActionResult.func_226251_d_(itemstack);
                }
              }
            } else {
              return ActionResult.func_226251_d_(itemstack);
            }
          }
        } else {
          FluidStack selectedFluidStack = handler.getFluidInTank(handler.selectedTank);
          RayTraceResult raytraceresult = rayTrace(world, player, selectedFluidStack.isEmpty()
                  ? RayTraceContext.FluidMode.SOURCE_ONLY : RayTraceContext.FluidMode.NONE);
          ActionResult<ItemStack> ret = net.minecraftforge.event.ForgeEventFactory.onBucketUse(player, world, itemstack, raytraceresult);
          if (ret != null) return ret;
          if (raytraceresult.getType() == RayTraceResult.Type.MISS) {
            return ActionResult.func_226250_c_(itemstack);
          } else if (raytraceresult.getType() != RayTraceResult.Type.BLOCK) {
            return ActionResult.func_226250_c_(itemstack);
          } else {
            BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult) raytraceresult;
            BlockPos blockpos = blockraytraceresult.getPos();
            Direction direction = blockraytraceresult.getFace();
            BlockPos blockpos1 = blockpos.offset(direction);
            if (world.isBlockModifiable(player, blockpos) && player.canPlayerEdit(blockpos1, direction, itemstack)) {
              if (selectedFluidStack.isEmpty()) {
                BlockState blockstate1 = world.getBlockState(blockpos);
                if (blockstate1.getBlock() instanceof IBucketPickupHandler) {
                  Fluid fluid = ((IBucketPickupHandler) blockstate1.getBlock()).pickupFluid(world, blockpos, blockstate1);
                  if (fluid != Fluids.EMPTY) {
                    player.addStat(Stats.ITEM_USED.get(this));

                    SoundEvent soundevent = selectedFluidStack.getFluid().getAttributes().getEmptySound();
                    if (soundevent == null)
                      soundevent = fluid.isIn(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_FILL_LAVA : SoundEvents.ITEM_BUCKET_FILL;
                    player.playSound(soundevent, 1.0F, 1.0F);

                    return ActionResult.func_226248_a_(this.fillBucket(fluid, itemstack, handler, player));
                  }
                }

                return ActionResult.func_226251_d_(itemstack);
              } else {
                BlockState blockstate = world.getBlockState(blockpos);
                BlockPos blockpos2 = blockstate.getBlock() instanceof ILiquidContainer && selectedFluidStack.getFluid() == Fluids.WATER ? blockpos : blockpos1;
                if (this.tryPlaceContainedLiquid(player, world, blockpos2, blockraytraceresult, selectedFluidStack)) {
                  this.onLiquidPlaced(world, itemstack, blockpos2);
                  if (player instanceof ServerPlayerEntity) {
                    CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity) player, blockpos2, itemstack);
                  }

                  player.addStat(Stats.ITEM_USED.get(this));
                  return ActionResult.func_226248_a_(this.emptyBucket(itemstack, handler, player));
                } else {
                  return ActionResult.func_226251_d_(itemstack);
                }
              }
            } else {
              return ActionResult.func_226251_d_(itemstack);
            }
          }
        }
      }
    }
    return new ActionResult<>(ActionResultType.PASS, player.getHeldItem(hand));
  }

  protected ItemStack emptyBucket(ItemStack current, TankNullItemStackFluidStackHandler handler, PlayerEntity player) {
    handler.drain1000(IFluidHandler.FluidAction.EXECUTE);
    return current;
  }

  public void onLiquidPlaced(World worldIn, ItemStack p_203792_2_, BlockPos pos) {
  }

  protected ItemStack fillBucket(Fluid fluid, ItemStack emptyBucket, TankNullItemStackFluidStackHandler handler, PlayerEntity player) {
    handler.fill1000(IFluidHandler.FluidAction.EXECUTE,new FluidStack(fluid,1000));
    return emptyBucket;
  }

  public boolean tryPlaceContainedLiquid(@Nullable PlayerEntity player, World worldIn, BlockPos posIn, @Nullable BlockRayTraceResult result, FluidStack selectedFluidStack) {
    if (!(selectedFluidStack.getFluid() instanceof FlowingFluid)) {
      return false;
    } else {
      BlockState blockstate = worldIn.getBlockState(posIn);
      Material material = blockstate.getMaterial();
      boolean flag = blockstate.isReplaceable(selectedFluidStack.getFluid());
      if (blockstate.isAir(worldIn, posIn) || flag || blockstate.getBlock() instanceof ILiquidContainer && ((ILiquidContainer)blockstate.getBlock())
              .canContainFluid(worldIn, posIn, blockstate, selectedFluidStack.getFluid())) {
        if (worldIn.dimension.doesWaterVaporize() && selectedFluidStack.getFluid().isIn(FluidTags.WATER)) {
          int i = posIn.getX();
          int j = posIn.getY();
          int k = posIn.getZ();
          worldIn.playSound(player, posIn, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.8F);

          for(int l = 0; l < 8; ++l) {
            worldIn.addParticle(ParticleTypes.LARGE_SMOKE, (double)i + Math.random(), (double)j + Math.random(), (double)k + Math.random(), 0.0D, 0.0D, 0.0D);
          }
        } else if (blockstate.getBlock() instanceof ILiquidContainer && selectedFluidStack.getFluid() == Fluids.WATER) {
          if (((ILiquidContainer)blockstate.getBlock()).receiveFluid(worldIn, posIn, blockstate, ((FlowingFluid)selectedFluidStack.getFluid()).getStillFluidState(false))) {
            this.playEmptySound(player, worldIn, posIn,selectedFluidStack);
          }
        } else {
          if (!worldIn.isRemote && flag && !material.isLiquid()) {
            worldIn.destroyBlock(posIn, true);
          }

          this.playEmptySound(player, worldIn, posIn,selectedFluidStack);
          worldIn.setBlockState(posIn, selectedFluidStack.getFluid().getDefaultState().getBlockState(), 11);
        }

        return true;
      } else {
        return result != null && this.tryPlaceContainedLiquid(player, worldIn, result.getPos().offset(result.getFace()), null, selectedFluidStack);
      }
    }
  }

  protected void playEmptySound(@Nullable PlayerEntity player, IWorld worldIn, BlockPos pos,FluidStack fluidStack) {
    SoundEvent soundevent = fluidStack.getFluid().getAttributes().getEmptySound();
    if(soundevent == null) soundevent = fluidStack.getFluid().isIn(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_EMPTY_LAVA : SoundEvents.ITEM_BUCKET_EMPTY;
    worldIn.playSound(player, pos, soundevent, SoundCategory.BLOCKS, 1.0F, 1.0F);
  }

  @Override
  public net.minecraftforge.common.capabilities.ICapabilityProvider initCapabilities(ItemStack stack, @Nullable net.minecraft.nbt.CompoundNBT nbt) {
      return TankNullItemStackFluidStackHandler.create(stack);
  }

  @Nonnull
  @Override
  public ActionResultType onItemUse(ItemUseContext ctx) {
    ItemStack bag = ctx.getItem();
    if (ctx.getPlayer()!= null && Utils.getMode(bag) == UseMode.BLOCK)
      return super.onItemUse(ctx);
    return ActionResultType.PASS;
  }

  @Override
  public TankNullBlock getBlock() {
    return (TankNullBlock)super.getBlock();
  }

  public static class ItemUseContextExt extends ItemUseContext {
    protected ItemUseContextExt(World p_i50034_1_, @Nullable PlayerEntity p_i50034_2_, Hand p_i50034_3_, ItemStack p_i50034_4_, BlockRayTraceResult p_i50034_5_) {
      super(p_i50034_1_, p_i50034_2_, p_i50034_3_, p_i50034_4_, p_i50034_5_);
    }
  }

}
