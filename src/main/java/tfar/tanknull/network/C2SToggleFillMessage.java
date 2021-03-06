package tfar.tanknull.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import tfar.tanknull.TankNullItem;
import tfar.tanknull.Utils;
import tfar.tanknull.container.BlockTankNullMenu;
import tfar.tanknull.container.ItemStackTankNullMenu;

import java.util.function.Supplier;

public class C2SToggleFillMessage {
  public void handle(Supplier<NetworkEvent.Context> ctx) {
    ServerPlayerEntity player = ctx.get().getSender();

    if (player == null) return;

    ctx.get().enqueueWork(  ()->  {
      ItemStack bag = player.getHeldItemMainhand();
      if (!(bag.getItem() instanceof TankNullItem)){
        bag = player.getHeldItemOffhand();
        if (!(bag.getItem() instanceof TankNullItem))return;
      }
      Utils.toggleFill(bag,player);
      if (player.openContainer instanceof ItemStackTankNullMenu)
      Messages.INSTANCE.sendTo(new S2CSyncItemStackMessage(player.currentWindowId, bag), player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
    });
    ctx.get().setPacketHandled(true);
  }
}
