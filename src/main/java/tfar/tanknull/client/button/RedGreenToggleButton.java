package tfar.tanknull.client.button;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import tfar.tanknull.client.ItemStackTankNullScreen;

public class RedGreenToggleButton extends SmallButton {

  protected boolean toggled;
  private final ItemStackTankNullScreen screen;

  public RedGreenToggleButton(int x, int y, int widthIn, int heightIn, Button.IPressable callback, boolean toggled, ItemStackTankNullScreen screen) {
    super(x, y, widthIn, heightIn,new StringTextComponent(""), callback);
    this.toggled = toggled;
    this.screen = screen;
  }

  public void toggle(){
    this.toggled = !this.toggled;
  }

  @Override
  public void render(MatrixStack stack,int mouseX, int mouseY, float partialTicks) {
    active = visible = screen.isSettings();
    super.render(stack,mouseX, mouseY, partialTicks);
  }

  @Override
  public void tint() {
    if (toggled) RenderSystem.color3f(0,1,0);
    else RenderSystem.color3f(1,0,0);
  }
}