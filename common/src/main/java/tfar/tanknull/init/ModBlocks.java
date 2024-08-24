package tfar.tanknull.init;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import tfar.tanknull.DockBlock;

public class ModBlocks {
    public static final Block DOCK = new DockBlock(BlockBehaviour.Properties.of().strength(2.5f));
}
