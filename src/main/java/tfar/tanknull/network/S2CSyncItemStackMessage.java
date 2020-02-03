package tfar.tanknull.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import tfar.tanknull.TankNullItem;
import tfar.tanknull.Utils;
import tfar.tanknull.client.ItemStackTankNullScreen;
import tfar.tanknull.container.ItemStackTankNullMenu;

import java.util.function.Supplier;

public class S2CSyncItemStackMessage {
  private int windowId = 0;
  private ItemStack stack = ItemStack.EMPTY;

  public S2CSyncItemStackMessage() {}

  public S2CSyncItemStackMessage(int windowId, ItemStack stack) {
    this.windowId = windowId;
    this.stack = stack;
  }

  public S2CSyncItemStackMessage(PacketBuffer buf) {
    this.windowId = buf.readByte();
    this.stack = buf.readItemStack();
  }

  public void encode(PacketBuffer buf) {
    buf.writeByte(this.windowId);
    buf.writeItemStack(this.stack);
  }

  public void handle(Supplier<NetworkEvent.Context> ctx) {
    PlayerEntity player = DistExecutor.callWhenOn(Dist.CLIENT, () -> () -> Minecraft.getInstance().player);
    if (player == null) return;

    ctx.get().enqueueWork(() -> {
      Container container = player.openContainer;
      if (container instanceof ItemStackTankNullMenu && windowId == container.windowId) {
        ((ItemStackTankNullMenu) container).te = stack;
      }
    });
    ctx.get().setPacketHandled(true);
  }
}
