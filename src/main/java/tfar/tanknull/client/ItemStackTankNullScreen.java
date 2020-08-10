package tfar.tanknull.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import tfar.tanknull.BlockTankNullScreen;
import tfar.tanknull.TankNull;
import tfar.tanknull.Utils;
import tfar.tanknull.client.button.RedGreenToggleButton;
import tfar.tanknull.client.button.TabButton;
import tfar.tanknull.container.BlockTankNullMenu;
import tfar.tanknull.container.ItemStackTankNullMenu;
import tfar.tanknull.network.C2SToggleFillMessage;
import tfar.tanknull.network.C2SToggleSpongeMessage;
import tfar.tanknull.network.Messages;

public class ItemStackTankNullScreen extends BlockTankNullScreen {
  private boolean settings = false;

  public ItemStackTankNullScreen(BlockTankNullMenu screenContainer, PlayerInventory inv, ITextComponent titleIn) {
    super(screenContainer, inv, titleIn);
  }

  @Override
  protected void init() {
    super.init();
    //addButton(new RedGreenToggleButton(guiLeft + 10, guiTop + 10, 8, 8, this::toggleFill, Utils.isFill(container.te, playerInventory.player), this));
    //addButton(new RedGreenToggleButton(guiLeft + 10, guiTop + 22, 8, 8, this::toggleSmartPlacing, Utils.isFill(container.te, playerInventory.player), this));
    //addButton(new RedGreenToggleButton(guiLeft + 10, guiTop + 34, 8, 8, this::sponge, Utils.isSponge(container.te, playerInventory.player), this));

    addButton(new TabButton(guiLeft, guiTop - 22, 22, 22, this::onPress2, new ItemStack(Blocks.IRON_BLOCK), this, false));
    addButton(new TabButton(guiLeft + 20, guiTop - 22, 22, 22, this::onPress3, new ItemStack(Blocks.GOLD_BLOCK), this, true));
  }

  public void sponge(Button b) {
    ((RedGreenToggleButton) b).toggle();
    Messages.INSTANCE.sendToServer(new C2SToggleSpongeMessage());
  }

  public void toggleSmartPlacing(Button b) {
    ((RedGreenToggleButton) b).toggle();
  }


  public void toggleFill(Button b) {
    ((RedGreenToggleButton) b).toggle();
    Messages.INSTANCE.sendToServer(new C2SToggleFillMessage());
  }

  public void onPress2(Button b) {
    settings = false;
  }

  public void onPress3(Button b) {
    settings = true;
  }

  public boolean isSettings() {
    return settings;
  }

  public void drawText(MatrixStack stack) {
    if (isSettings()) {
      int y = 10;
      //String text = Utils.isFill(container.te, playerInventory.player) ? "Filling" : "Emptying";
      //this.font.drawString(stack,text, 20, 10, 0x404040);
      this.font.drawString(stack,"Smart Placing", 20, y+=12, 0x404040);
      this.font.drawString(stack,"Sponge", 20, y + 12, 0x404040);

    }
  }

  @Override
  protected void drawFluids(MatrixStack stack) {
    if (!isSettings())
      super.drawFluids(stack);
  }
}
