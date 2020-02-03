package tfar.tanknull.client;

import net.minecraft.block.Blocks;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import tfar.tanknull.ATankNullScreen;
import tfar.tanknull.TankNull;
import tfar.tanknull.Utils;
import tfar.tanknull.client.button.RedGreenToggleButton;
import tfar.tanknull.client.button.TabButton;
import tfar.tanknull.container.ItemStackTankNullMenu;
import tfar.tanknull.network.C2SToggleFillMessage;
import tfar.tanknull.network.C2SToggleSpongeMessage;
import tfar.tanknull.network.Messages;

public class ItemStackTankNullScreen extends ATankNullScreen<ItemStackTankNullMenu> {
  public ResourceLocation background;
  private boolean settings = false;

  public ItemStackTankNullScreen(ItemStackTankNullMenu screenContainer, PlayerInventory inv, ITextComponent titleIn) {
    super(screenContainer, inv, titleIn);
    this.background = Utils.getBackground(screenContainer.te);
  }

  @Override
  protected void init() {
    super.init();
    addButton(new RedGreenToggleButton(guiLeft + 10, guiTop + 10, 8, 8, this::toggleFill, Utils.isFill(container.te, playerInventory.player), this));
    addButton(new RedGreenToggleButton(guiLeft + 10, guiTop + 22, 8, 8, this::toggleSmartPlacing, Utils.isFill(container.te, playerInventory.player), this));
    addButton(new RedGreenToggleButton(guiLeft + 10, guiTop + 34, 8, 8, this::sponge, Utils.isSponge(container.te, playerInventory.player), this));

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
    background = Utils.getBackground(container.te);
  }

  public void onPress3(Button b) {
    settings = true;
    background = new ResourceLocation(TankNull.MODID, "textures/container/gui/settings.png");
  }

  public boolean isSettings() {
    return settings;
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

  }

  @Override
  protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
    super.drawGuiContainerForegroundLayer(p_146979_1_, p_146979_2_);
    drawText();
  }

  public void drawText() {
    if (isSettings()) {
      int y = 10;
      String text = Utils.isFill(container.te, playerInventory.player) ? "Filling" : "Emptying";
      this.font.drawString(text, 20, 10, 0x404040);
      this.font.drawString("Smart Placing", 20, y+=12, 0x404040);
      this.font.drawString("Sponge", 20, y + 12, 0x404040);

    }
  }

  @Override
  protected void drawFluids() {
    if (!isSettings())
      super.drawFluids();
  }

  @Override
  public ResourceLocation getBackground() {
    return background;
  }
}
