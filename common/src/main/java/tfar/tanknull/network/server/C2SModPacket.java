package tfar.tanknull.network.server;

import net.minecraft.server.level.ServerPlayer;
import tfar.tanknull.network.ModPacket;

public interface C2SModPacket extends ModPacket {

    void handleServer(ServerPlayer player);

}
