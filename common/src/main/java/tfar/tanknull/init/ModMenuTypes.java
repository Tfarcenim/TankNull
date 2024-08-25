package tfar.tanknull.init;

import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import tfar.tanknull.menu.TankConfigMenu;
import tfar.tanknull.menu.TankMenu;

public class ModMenuTypes {

    public static final MenuType<TankMenu> TANK_1 = new MenuType<>(TankMenu::t1, FeatureFlags.VANILLA_SET);
    public static final MenuType<TankMenu> TANK_2 = new MenuType<>(TankMenu::t2, FeatureFlags.VANILLA_SET);
    public static final MenuType<TankMenu> TANK_3 = new MenuType<>(TankMenu::t3, FeatureFlags.VANILLA_SET);
    public static final MenuType<TankMenu> TANK_4 = new MenuType<>(TankMenu::t4, FeatureFlags.VANILLA_SET);
    public static final MenuType<TankMenu> TANK_5 = new MenuType<>(TankMenu::t5, FeatureFlags.VANILLA_SET);
    public static final MenuType<TankMenu> TANK_6 = new MenuType<>(TankMenu::t6, FeatureFlags.VANILLA_SET);
    public static final MenuType<TankMenu> TANK_7 = new MenuType<>(TankMenu::t7, FeatureFlags.VANILLA_SET);

    public static final MenuType<TankConfigMenu> CONFIG = new MenuType<>(TankConfigMenu::new, FeatureFlags.VANILLA_SET);

}
