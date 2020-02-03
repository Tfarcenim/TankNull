package tfar.tanknull.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;

public class NamedMenuProvider implements INamedContainerProvider {

  private final ItemStack stack;

  public NamedMenuProvider(ItemStack stack){
    this.stack = stack;
  }

  @Override
  public ITextComponent getDisplayName() {
    return stack.getDisplayName();
  }

  @Nullable
  @Override
  public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
    return new ItemStackTankNullMenu(p_createMenu_1_,p_createMenu_2_);
  }
}
