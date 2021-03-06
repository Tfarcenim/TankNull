package tfar.tanknull.container;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.wrapper.InvWrapper;
import tfar.tanknull.TankNullDockBlockEntity;
import tfar.tanknull.RegistryObjects;
import tfar.tanknull.inventory.FluidStackHandler;

public class BlockTankNullMenu extends ATankNullMenu {

  public TankNullDockBlockEntity te;

  public BlockTankNullMenu(int id, PlayerInventory inv, World world, BlockPos pos) {
    super(RegistryObjects.block_container.get(), id, inv);
    te = (TankNullDockBlockEntity) world.getTileEntity(pos);
    addPlayerSlots(new InvWrapper(inv));
  }

  @Override
  public FluidStackHandler getHandler() {
    return te.getHandler();
  }

}
