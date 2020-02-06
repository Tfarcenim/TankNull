package tfar.tanknull.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import tfar.tanknull.TankNullItem;
import tfar.tanknull.Utils;
import tfar.tanknull.container.ItemStackTankNullMenu;
import tfar.tanknull.container.NamedMenuProvider;

import java.util.function.Supplier;

public class C2SOpenContainerMessage {
  public void handle(Supplier<NetworkEvent.Context> ctx) {
    ServerPlayerEntity player = ctx.get().getSender();

    if (player == null) return;

    ctx.get().enqueueWork(  ()-> {
              ItemStack tank = player.getHeldItemMainhand();
              if (!(tank.getItem() instanceof TankNullItem)) {
                tank = player.getHeldItemOffhand();
                if (!(tank.getItem() instanceof TankNullItem)) return;
              }
              player.openContainer(new NamedMenuProvider(tank));
            });
    ctx.get().setPacketHandled(true);
  }
}
