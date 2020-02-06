package tfar.tanknull;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import tfar.tanknull.container.BlockTankNullMenu;

public class TileTankNullScreen extends ATankNullScreen<BlockTankNullMenu> {
  public final ResourceLocation background;
  public TileTankNullScreen(BlockTankNullMenu screenContainer, PlayerInventory inv, ITextComponent titleIn) {
    super(screenContainer, inv, titleIn);
    this.background = Utils.getBackground(screenContainer.te.getBlockState().get(TankNullDockBlock.TIER));
  }

  @Override
  public ResourceLocation getBackground() {
    return background;
  }
}
