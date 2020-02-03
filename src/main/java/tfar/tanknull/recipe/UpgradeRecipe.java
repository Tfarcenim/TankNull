package tfar.tanknull.recipe;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.ShapedRecipe;
import tfar.tanknull.RegistryObjects;

import javax.annotation.Nonnull;

public class UpgradeRecipe extends ShapedRecipe {

  public UpgradeRecipe(ShapedRecipe recipe){
    super(recipe.getId(),recipe.getGroup(),recipe.getWidth(),recipe.getHeight(),recipe.getIngredients(),recipe.getRecipeOutput());
  }

  @Nonnull
  @Override
  public ItemStack getCraftingResult(CraftingInventory inv) {
    ItemStack tank = super.getCraftingResult(inv).copy();
    ItemStack oldBag = inv.getStackInSlot(4).copy();
    if (!oldBag.hasTag())return tank;
    tank.setTag(oldBag.getTag());
    return tank;
  }

  @Nonnull
  @Override
  public IRecipeSerializer<?> getSerializer() {
    return RegistryObjects.upgrade.get();
  }
}
