package tfar.tanknull.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Mod;
import tfar.tanknull.TankNull;
import tfar.tanknull.TankNullItem;
import tfar.tanknull.inventory.TankNullItemStackFluidStackHandler;
import tfar.tanknull.network.C2SMessageScrollTank;
import tfar.tanknull.network.C2SOpenContainerMessage;
import tfar.tanknull.network.C2SToggleFillMessage;
import tfar.tanknull.network.Messages;

import static tfar.tanknull.ATankNullScreen.getFluidTexture;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = TankNull.MODID)
public class TankNullClient {

  public static KeyBinding MODE;
  public static final Minecraft mc = Minecraft.getInstance();

  @SubscribeEvent
  public static void mousewheel(InputEvent.MouseScrollEvent e) {
    PlayerEntity player = Minecraft.getInstance().player;
    if (player != null) {
      ItemStack stack = player.getHeldItem(Hand.MAIN_HAND);
      if (player.isCrouching() && stack.getItem() instanceof TankNullItem) {
        boolean right = e.getScrollDelta() < 0;
        Messages.INSTANCE.sendToServer(new C2SMessageScrollTank(right));
        e.setCanceled(true);
      }
    }
  }

  @SubscribeEvent
  public static void onKeyInput(InputEvent.KeyInputEvent event) {
    if (mc.player == null || !(mc.player.getHeldItemMainhand().getItem() instanceof TankNullItem ||
            mc.player.getHeldItemOffhand().getItem() instanceof TankNullItem))
      return;
    if (MODE.isPressed()) {
      Messages.INSTANCE.sendToServer(new C2SOpenContainerMessage());
    }
  }

  @SubscribeEvent
  public static void onKeyInput(InputEvent.MouseInputEvent event) {
    if (mc.player == null || !(mc.player.getHeldItemMainhand().getItem() instanceof TankNullItem ||
            mc.player.getHeldItemOffhand().getItem() instanceof TankNullItem) || event.getAction() != 1)
      return;
    if (Screen.hasAltDown()){
      Messages.INSTANCE.sendToServer(new C2SToggleFillMessage());
    }
  }

  @SubscribeEvent
  public static void onRenderTick(RenderGameOverlayEvent.Post event) {
    PlayerEntity player = mc.player;
    if (event.getType() != RenderGameOverlayEvent.ElementType.HOTBAR || player == null)
      return;
    if (!(player.openContainer instanceof PlayerContainer)) return;
    ItemStack bag = player.getHeldItemMainhand();
    if (!(bag.getItem() instanceof TankNullItem)) {
      bag = player.getHeldItemOffhand();
      if (!(bag.getItem() instanceof TankNullItem))
        return;
    }
    int xStart = event.getWindow().getScaledWidth() / 2;
    int yStart = event.getWindow().getScaledHeight();
    TankNullItemStackFluidStackHandler handler = TankNullItemStackFluidStackHandler.create(bag);
    if(handler.getTanks() == 0)return;
    FluidStack fluidStack = handler.getSelectedFluid();

    if (!fluidStack.isEmpty()) {
      final int itemX = xStart - 150;
      final int itemY = yStart - 25;
      renderSelectedFluid(itemX, itemY, 0, player, fluidStack);
    }
    mc.getTextureManager().bindTexture(AbstractGui.GUI_ICONS_LOCATION);
  }

  private static void renderSelectedFluid(int x, int y, float partialTicks, PlayerEntity player, FluidStack fluidStack) {
      mc.textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
      int color = fluidStack.getFluid().getAttributes().getColor(fluidStack);
      TextureAtlasSprite textureAtlasSprite = getFluidTexture(fluidStack);
      RenderSystem.color3f((color >> 16 & 0xff) / 255f, (color >> 8 & 0xff) / 255f, (color & 0xff) / 255f);
      AbstractGui.blit(x,y, 0, 16, 16, textureAtlasSprite);
      RenderSystem.pushMatrix();
      RenderSystem.scaled(.75,.75,.75);
      int len = mc.fontRenderer.getStringWidth(fluidStack.getAmount()/1000+"B");
      mc.fontRenderer.drawStringWithShadow(fluidStack.getAmount()/1000+"B",1.33f*(x-len+20),1.33f*(y+12),0xffffff);
      RenderSystem.popMatrix();
  }
}
