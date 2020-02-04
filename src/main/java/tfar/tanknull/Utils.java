package tfar.tanknull;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import tfar.tanknull.inventory.TankNullItemStackFluidStackHandler;

public class Utils {
  public static ResourceLocation getBackground(Block block){
    if (block instanceof TankNullBlock){
      int tier = ((TankNullBlock) block).tier;
      return new ResourceLocation(TankNull.MODID,"textures/container/gui/tank"+tier+".png");
    }
    throw new IllegalStateException("no");
  }

  public static ResourceLocation getBackground(ItemStack stack){
    if (stack.getItem() instanceof TankNullItem){
      int tier = ((TankNullItem)stack.getItem()).getBlock().tier;
      return new ResourceLocation(TankNull.MODID,"textures/container/gui/tank"+tier+".png");
    }
    throw new IllegalStateException("no");
  }

  public static int getTanks(Block block){
    if (block instanceof TankNullBlock){
      int tier = ((TankNullBlock) block).tier;
      switch (tier){
        case 1:return 3;
        case 2:return 6;
        case 3:return 9;
        case 4:return 12;
        case 5:return 15;
        case 6:return 18;
        case 7:return 27;
      }
    }
    throw new IllegalStateException("no");
  }

  public static int getTanks(ItemStack stack){
    Block block = Block.getBlockFromItem(stack.getItem());
    return getTanks(block);
  }

  public static int getCapacity(ItemStack stack){
    Block block = Block.getBlockFromItem(stack.getItem());
    return getCapacity(block);
  }

  public static int getCapacity(Block block){
    if (block instanceof TankNullBlock){
      int tier = ((TankNullBlock) block).tier;
      switch (tier){
        case 1:return 4000;
        case 2:return 16000;
        case 3:return 64000;
        case 4:return 256000;
        case 5:return 1024000;
        case 6:return 4096000;
        case 7:return Integer.MAX_VALUE;
      }
    }
    throw new IllegalStateException("no");
  }

  public static boolean DEV;

  static {
    try {
      Items.class.getField("field_190931_a");
      DEV = false;
    } catch (NoSuchFieldException e) {
      DEV = true;
    }
  }

  public static boolean isFill(ItemStack bag, PlayerEntity player) {
    return TankNullItemStackFluidStackHandler.create(bag).fill;
  }

  public static void toggleFill(ItemStack bag, PlayerEntity player) {
    TankNullItemStackFluidStackHandler.create(bag).toggleFill();
  }

  public static boolean isSponge(ItemStack bag, PlayerEntity player) {
    return TankNullItemStackFluidStackHandler.create(bag).sponge;
  }

  public static void toggleSponge(ItemStack bag, PlayerEntity player) {
    TankNullItemStackFluidStackHandler.create(bag).toggleSponge();
  }

  public static void toggleMode(ItemStack bag){
    TankNullItemStackFluidStackHandler.create(bag).cycleMode();
  }

  public static UseMode getMode(ItemStack stack){
    return TankNullItemStackFluidStackHandler.create(stack).getMode();
  }

  public static void changeSlot(ItemStack bag, boolean right) {
      TankNullItemStackFluidStackHandler.create(bag).scroll(right);
    }
  }
