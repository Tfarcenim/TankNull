package tfar.tanknull.init;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import tfar.tanknull.TankItem;
import tfar.tanknull.TankStats;

public class ModItems {

    public static final Item TANK_1 = new TankItem(new Item.Properties(), TankStats.one);
    public static final Item TANK_2 = new TankItem(new Item.Properties(), TankStats.two);
    public static final Item TANK_3 = new TankItem(new Item.Properties(), TankStats.three);
    public static final Item TANK_4 = new TankItem(new Item.Properties(), TankStats.four);
    public static final Item TANK_5 = new TankItem(new Item.Properties(), TankStats.five);
    public static final Item TANK_6 = new TankItem(new Item.Properties(), TankStats.six);
    public static final Item TANK_7 = new TankItem(new Item.Properties(), TankStats.seven);

    public static final Item DOCK = new BlockItem(ModBlocks.DOCK,new Item.Properties());

}
