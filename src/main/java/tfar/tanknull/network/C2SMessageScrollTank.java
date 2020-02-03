package tfar.tanknull.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import tfar.tanknull.TankNullItem;
import tfar.tanknull.Utils;

import java.util.function.Supplier;

public class C2SMessageScrollTank {

  boolean right;

  public C2SMessageScrollTank() {
  }

  public C2SMessageScrollTank(boolean right) {
    this.right = right;
  }

  //decode
  public C2SMessageScrollTank(PacketBuffer buf) {
    this.right = buf.readBoolean();
  }

  public void encode(PacketBuffer buf) {
    buf.writeBoolean(right);
  }

  public void handle(Supplier<NetworkEvent.Context> ctx) {
    PlayerEntity player = ctx.get().getSender();
    ctx.get().enqueueWork(() -> {
      ItemStack bag = player.getHeldItemMainhand();
      if (bag.getItem() instanceof TankNullItem) {
        Utils.changeSlot(bag, right);
      }
    });
    ctx.get().setPacketHandled(true);
  }
}
