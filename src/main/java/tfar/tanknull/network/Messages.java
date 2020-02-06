package tfar.tanknull.network;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import tfar.tanknull.TankNull;

public class Messages {
  public static SimpleChannel INSTANCE;

  public static void registerMessages(String channelName) {
    int id = 0;

    INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(TankNull.MODID, channelName), () -> "1.0", s -> true, s -> true);

    INSTANCE.registerMessage(id++, C2SToggleFillMessage.class,
            (message, buffer) -> {},
            buffer -> new C2SToggleFillMessage(),
            C2SToggleFillMessage::handle);

    INSTANCE.registerMessage(id++, C2SOpenContainerMessage.class,
            (message, buffer) -> {},
            buffer -> new C2SOpenContainerMessage(),
            C2SOpenContainerMessage::handle);

    INSTANCE.registerMessage(id++, C2SToggleSpongeMessage.class,
            (message, buffer) -> {},
            buffer -> new C2SToggleSpongeMessage(),
            C2SToggleSpongeMessage::handle);

    INSTANCE.registerMessage(id++, C2SMessageScrollTank.class,
            C2SMessageScrollTank::encode,
            C2SMessageScrollTank::new,
            C2SMessageScrollTank::handle);

    INSTANCE.registerMessage(id++, S2CSyncItemStackMessage.class,
            S2CSyncItemStackMessage::encode,
            S2CSyncItemStackMessage::new,
            S2CSyncItemStackMessage::handle);
  }
}
