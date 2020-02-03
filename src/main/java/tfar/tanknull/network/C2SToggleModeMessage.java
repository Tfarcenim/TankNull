package tfar.tanknull.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import tfar.tanknull.TankNullItem;
import tfar.tanknull.Utils;

import java.util.function.Supplier;

public class C2SToggleModeMessage {
  public void handle(Supplier<NetworkEvent.Context> ctx) {
    ServerPlayerEntity player = ctx.get().getSender();

    if (player == null) return;

    ctx.get().enqueueWork(  ()->  {
      ItemStack bag = player.getHeldItemMainhand();
      if (!(bag.getItem() instanceof TankNullItem)){
        bag = player.getHeldItemOffhand();
        if (!(bag.getItem() instanceof TankNullItem))return;
      }
      Utils.toggleMode(bag);
      Messages.INSTANCE.sendTo(new S2CSyncItemStackMessage(player.currentWindowId, bag), player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
    });
    ctx.get().setPacketHandled(true);
  }
}
