package tfar.tanknull;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import tfar.tanknull.client.TankNullClient;
import tfar.tanknull.inventory.TankNullBlockFluidStackHandler;
import tfar.tanknull.inventory.TankNullItemStackFluidStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class TankNullItem extends Item {
  public final TankStats stats;

  public TankNullItem(Properties builder, TankStats stats) {
    super(builder);
    this.stats = stats;
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void addInformation(ItemStack stack, @Nullable World p_190948_2_, List<ITextComponent> tooltip, ITooltipFlag p_190948_4_) {
    if (stack.hasTag() && Utils.DEV) tooltip.add(new StringTextComponent(stack.getTag().toString()).mergeStyle(TextFormatting.DARK_GRAY));
    CompoundNBT nbt = stack.getTag();
    tooltip.add(new TranslationTextComponent("text.tanknull.stacklimit",Utils.getCapacity(this)));
    if (nbt != null && !nbt.getCompound("fluidinv").isEmpty()) {
      CompoundNBT fluidTag = nbt.getCompound("fluidinv");
      ITextComponent text = new TranslationTextComponent("text.tanknull.mode",
              fluidTag.getBoolean("fill") ?
                      new TranslationTextComponent("text.tanknull.mode.fill").mergeStyle(TextFormatting.AQUA) :
                      new TranslationTextComponent("text.tanknull.mode.empty").mergeStyle(TextFormatting.AQUA))
              .append(new TranslationTextComponent(" Alt + Right Click to swap").mergeStyle(TextFormatting.GRAY));
      tooltip.add(text);
      tooltip.add(new TranslationTextComponent("text.tanknull.settings",
              new StringTextComponent(TankNullClient.MODE.getTranslationKey()).mergeStyle(TextFormatting.YELLOW)));
      ListNBT tagList = fluidTag.getList("Fluids", Constants.NBT.TAG_COMPOUND);
      for (int i = 0; i < tagList.size(); i++) {
        CompoundNBT fluidTags = tagList.getCompound(i);
        FluidStack fluidStack = FluidStack.loadFluidStackFromNBT(fluidTags);
        if (!fluidStack.isEmpty()) {
          tooltip.add(new TranslationTextComponent("text.tanknull.formatcontainedfluids",
                  new StringTextComponent(String.valueOf(i)).mergeStyle(TextFormatting.GREEN),
                  new StringTextComponent(String.valueOf(fluidStack.getAmount())).mergeStyle(TextFormatting.AQUA),
                  fluidStack.getDisplayName()).mergeStyle(TextFormatting.WHITE));
        }
      }
    }
  }


  @Nonnull
  @Override
  public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
    ItemStack itemstack = player.getHeldItem(hand);
    if (!world.isRemote) {
      TankNullItemStackFluidStackHandler handler = TankNullItemStackFluidStackHandler.create(itemstack);
      boolean filling = handler.fill;
      if (filling) {
        return fill(world, player, hand,itemstack,handler);
      } else {
        return empty(world,player,hand,itemstack,handler);
      }
    }
    return new ActionResult<>(ActionResultType.PASS, player.getHeldItem(hand));
  }

  public ActionResult<ItemStack> fill(World world, PlayerEntity player, Hand hand, ItemStack itemstack,TankNullItemStackFluidStackHandler handler){

    FluidStack selectedFluidStack = handler.getFluidInTank(handler.selectedTank);
    RayTraceResult raytraceresult = rayTrace(world, player, RayTraceContext.FluidMode.SOURCE_ONLY);
    ActionResult<ItemStack> ret = net.minecraftforge.event.ForgeEventFactory.onBucketUse(player, world, itemstack, raytraceresult);
    if (ret != null) return ret;
    if (raytraceresult.getType() == RayTraceResult.Type.MISS) {
      return ActionResult.resultFail(itemstack);
    } else if (raytraceresult.getType() != RayTraceResult.Type.BLOCK) {
      return ActionResult.resultFail(itemstack);
    } else {
      BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult) raytraceresult;
      BlockPos tracedPos = blockraytraceresult.getPos();

      Direction direction = blockraytraceresult.getFace();
      BlockPos offsetPos = tracedPos.offset(direction);
      if (world.isBlockModifiable(player, tracedPos) && player.canPlayerEdit(offsetPos, direction, itemstack)) {
          BlockState rayTracedState = world.getBlockState(tracedPos);
          if (rayTracedState.getBlock() instanceof IBucketPickupHandler) {
            Fluid fluid = Fluids.EMPTY;
            if (rayTracedState.getBlock() instanceof IWaterLoggable)
            fluid = Fluids.WATER;
            else if (rayTracedState.getBlock() instanceof FlowingFluidBlock)
              fluid = (((FlowingFluidBlock) rayTracedState.getBlock()).getFluid());
            if (fluid != Fluids.EMPTY) {
              if (handler.hasRoomForBlockFluid(new FluidStack(fluid,1000))/*todo or isvoid*/) {
                ((IBucketPickupHandler) rayTracedState.getBlock()).pickupFluid(world, tracedPos, rayTracedState);
                player.addStat(Stats.ITEM_USED.get(this));
              SoundEvent soundevent = selectedFluidStack.getFluid().getAttributes().getEmptySound();
              if (soundevent == null)
                soundevent = fluid.isIn(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_FILL_LAVA : SoundEvents.ITEM_BUCKET_FILL;
              player.playSound(soundevent, 1.0F, 1.0F);

              return ActionResult.resultConsume(this.fillBucket(fluid, itemstack, handler, player));
            }
          }
          return ActionResult.resultPass(itemstack);
        } else {
          return ActionResult.resultPass(itemstack);
        }
      } else {
        return ActionResult.resultFail(itemstack);
      }
    }
  }

  public ActionResult<ItemStack> empty(World world, PlayerEntity player, Hand hand, ItemStack itemstack,TankNullItemStackFluidStackHandler handler){
    FluidStack selectedFluidStack = handler.getFluidInTank(handler.selectedTank);
    if (selectedFluidStack.isEmpty())return ActionResult.resultFail(itemstack);

    selectedFluidStack.isEmpty();
    RayTraceResult raytraceresult = rayTrace(world, player, RayTraceContext.FluidMode.SOURCE_ONLY);
    ActionResult<ItemStack> ret = net.minecraftforge.event.ForgeEventFactory.onBucketUse(player, world, itemstack, raytraceresult);
    if (ret != null) return ret;
    if (raytraceresult.getType() == RayTraceResult.Type.MISS) {
      return ActionResult.resultFail(itemstack);
    } else if (raytraceresult.getType() != RayTraceResult.Type.BLOCK) {
      return ActionResult.resultFail(itemstack);
    }
    BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult) raytraceresult;
    BlockPos rayTracePoa = blockraytraceresult.getPos();
    Direction direction = blockraytraceresult.getFace();
    BlockPos placingPosition = rayTracePoa.offset(direction);
    if (world.isBlockModifiable(player, rayTracePoa) && player.canPlayerEdit(placingPosition, direction, itemstack)) {

      if (this.tryPlaceContainedLiquid(player, world, placingPosition, blockraytraceresult, selectedFluidStack)) {
        this.onLiquidPlaced(world, itemstack, placingPosition);
        if (player instanceof ServerPlayerEntity) {
          CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity) player, placingPosition, itemstack);
        }

        player.addStat(Stats.ITEM_USED.get(this));
        return ActionResult.resultConsume(this.emptyBucket(itemstack, handler, player));
      }
      return ActionResult.resultPass(itemstack);
    }
    return ActionResult.resultPass(itemstack);

    //BlockState blockstate1 = world.getBlockState(rayTracePoa);



       /* if (blockstate1.getBlock() instanceof IBucketPickupHandler) {
          Fluid fluid = ((IBucketPickupHandler) blockstate1.getBlock()).pickupFluid(world, rayTracePoa, blockstate1);
          if (fluid != Fluids.EMPTY) {
            player.addStat(Stats.ITEM_USED.get(this));

            SoundEvent soundevent = selectedFluidStack.getFluid().getAttributes().getEmptySound();
            if (soundevent == null)
              soundevent = fluid.isIn(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_FILL_LAVA : SoundEvents.ITEM_BUCKET_FILL;
            player.playSound(soundevent, 1.0F, 1.0F);

            return ActionResult.func_226248_a_(this.fillBucket(fluid, itemstack, handler, player));
          }

        return ActionResult.func_226251_d_(itemstack);
      } else {
        BlockState blockstate = world.getBlockState(rayTracePoa);
        BlockPos blockpos2 = blockstate.getBlock() instanceof ILiquidContainer && selectedFluidStack.getFluid() == Fluids.WATER ? rayTracePoa : placingPosition;
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
      }*/
    //return ActionResult.func_226251_d_(itemstack);
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
        if (worldIn.func_230315_m_().func_236040_e_() && selectedFluidStack.getFluid().isIn(FluidTags.WATER)) {
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
      return Utils.createFluidProvider(TankNullItemStackFluidStackHandler.create(stack));
  }

  @Nonnull
  @Override
  public ActionResultType onItemUse(ItemUseContext ctx) {
    BlockPos pos = ctx.getPos();
    World world = ctx.getWorld();
    BlockState state =  world.getBlockState(pos);
    if (!(state.getBlock() instanceof TankNullDockBlock))
    return ActionResultType.PASS;


    return ActionResultType.PASS;
  }

  public static class ItemUseContextExt extends ItemUseContext {
    protected ItemUseContextExt(World p_i50034_1_, @Nullable PlayerEntity p_i50034_2_, Hand p_i50034_3_, ItemStack p_i50034_4_, BlockRayTraceResult p_i50034_5_) {
      super(p_i50034_1_, p_i50034_2_, p_i50034_3_, p_i50034_4_, p_i50034_5_);
    }
  }

}
