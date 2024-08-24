package tfar.tanknull.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import tfar.tanknull.DockBlockEntity;
import tfar.tanknull.platform.Services;

public class ModBlockEntityTypes {
    public static final BlockEntityType<DockBlockEntity> DOCK = BlockEntityType.Builder.of(Services.PLATFORM::create, ModBlocks.DOCK).build(null);

}
