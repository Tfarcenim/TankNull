package tfar.tanknull;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import tfar.tanknull.container.BlockTankNullMenu;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;

public class BlockTankNullScreen extends ContainerScreen<BlockTankNullMenu> implements IContainerListener {
  public BlockTankNullScreen(BlockTankNullMenu screenContainer, PlayerInventory inv, ITextComponent titleIn) {
    super(screenContainer, inv, titleIn);
    TEXTURE = new ResourceLocation(TankNull.MODID,"textures/container/gui/tank"+screenContainer.stats.ordinal()+".png");
    ySize += 4;
  }
  private final ResourceLocation TEXTURE;

  @Override
  public void render(MatrixStack stack,int p_render_1_, int p_render_2_, float p_render_3_) {
    renderBackground(stack);
    super.render(stack,p_render_1_, p_render_2_, p_render_3_);
    this.func_230459_a_(stack,p_render_1_, p_render_2_);
  }

  /**
   * Draws the background layer of this container (behind the items).
   *
   * @param partialTicks
   * @param mouseX
   * @param mouseY
   */
  @Override
  protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack,float partialTicks, int mouseX, int mouseY) {
    RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    this.minecraft.getTextureManager().bindTexture(TEXTURE);
    int i = (this.width - this.xSize) / 2;
    int j = (this.height - this.ySize) / 2;
    this.blit(matrixStack, i, j, 0, 0, this.xSize, this.ySize);
    drawFluids(matrixStack);
  }

  protected void drawFluids(MatrixStack stack) {
    IFluidHandler fluidStackHandler = container.fluidStackHandler;
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
          blit(stack,xPos + x * 18,yPos+y * 18, 0, 16, 16, textureAtlasSprite);
          font.drawStringWithShadow(stack,getStringFromInt(fluidStack.getAmount()),xPos + x * 18,yPos+y * 18+9,0xffffff);
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
    return Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(spriteLocation);
  }

  @Override
  public void sendAllContents(Container containerToSend, NonNullList<ItemStack> itemsList) {

  }

  @Override
  public void sendSlotContents(Container containerToSend, int slotInd, ItemStack stack) {

  }

  @Override
  public void sendWindowProperty(Container containerIn, int varToUpdate, int newValue) {

  }
}
