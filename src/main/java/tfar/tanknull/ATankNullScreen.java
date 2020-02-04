package tfar.tanknull;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import tfar.tanknull.container.ATankNullMenu;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;

public abstract class ATankNullScreen<T extends ATankNullMenu> extends ContainerScreen<T> {
  public ATankNullScreen(T screenContainer, PlayerInventory inv, ITextComponent titleIn) {
    super(screenContainer, inv, titleIn);
    ySize += 4;
  }

  public abstract ResourceLocation getBackground();

  @Override
  public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
    renderBackground();
    super.render(p_render_1_, p_render_2_, p_render_3_);
    this.renderHoveredToolTip(p_render_1_, p_render_2_);
  }

  @Override
  protected void renderHoveredToolTip(int mouseX, int mouseY) {
    super.renderHoveredToolTip(mouseX, mouseY);
  }

  /**
   * Draws the background layer of this container (behind the items).
   *
   * @param partialTicks
   * @param mouseX
   * @param mouseY
   */
  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    minecraft.getTextureManager().bindTexture(getBackground());
    blit(guiLeft, guiTop, 0, 0, xSize, ySize);
    drawFluids();
  }

  @Override
  protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
    this.font.drawString(this.playerInventory.getDisplayName().getUnformattedComponentText(), 8, this.ySize - 94, 0x404040);
    int size = font.getStringWidth(title.getFormattedText());
    int start = (this.xSize - size)/2;
    this.font.drawString(this.title.getUnformattedComponentText(), start, 8, 0x404040);
  }

  protected void drawFluids() {
    IFluidHandler fluidStackHandler = container.getHandler();
    int column = fluidStackHandler.getTanks() / 3;
    int xPos = guiLeft + 89 - column * 9;
    int yPos = guiTop+18;
//You need to get ARGB out of the color to pass
    for (int y = 0; y < 3; y++)
      for (int x = 0; x < column; x++) {
        FluidStack fluidStack = fluidStackHandler.getFluidInTank(x + column * y);
        if (!fluidStack.isEmpty()) {
          minecraft.textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
          int color = fluidStack.getFluid().getAttributes().getColor(fluidStack);
          TextureAtlasSprite textureAtlasSprite = getFluidTexture(fluidStack);
          RenderSystem.color3f((color >> 16 & 0xff) / 255f, (color >> 8 & 0xff) / 255f, (color & 0xff) / 255f);
          blit(xPos + x * 18,yPos+y * 18, 0, 16, 16, textureAtlasSprite);
          font.drawStringWithShadow(getStringFromInt(fluidStack.getAmount()),xPos + x * 18,yPos+y * 18+10,0xffffff);
        }
      }
  }

  private static final DecimalFormat decimalFormat = new DecimalFormat("0.#");

  public String getStringFromInt(int number){

    if (number >= 1000000000) return decimalFormat.format(number / 1000000000f) + "b";
    if (number >= 1000000) return decimalFormat.format(number / 1000000f) + "m";
    if (number >= 1000) return decimalFormat.format(number / 1000f) + "k";

    return Float.toString(number).replaceAll("\\.?0*$", "");
  }

  public static TextureAtlasSprite getFluidTexture(@Nonnull FluidStack fluidStack) {
    Fluid fluid = fluidStack.getFluid();
    ResourceLocation spriteLocation = fluid.getAttributes().getStillTexture(fluidStack);
    return getSprite(spriteLocation);
  }

  public static TextureAtlasSprite getSprite(ResourceLocation spriteLocation) {
    return Minecraft.getInstance().getTextureGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(spriteLocation);
  }
}
