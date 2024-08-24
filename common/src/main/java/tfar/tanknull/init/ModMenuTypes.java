package tfar.tanknull.init;

import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import tfar.tanknull.menu.AbstractTankMenu;

public class ModMenuTypes {

    public static final MenuType<AbstractTankMenu> TANK_1 = new MenuType<>(AbstractTankMenu::t1, FeatureFlags.VANILLA_SET);
    public static final MenuType<AbstractTankMenu> TANK_2 = new MenuType<>(AbstractTankMenu::t2, FeatureFlags.VANILLA_SET);
    public static final MenuType<AbstractTankMenu> TANK_3 = new MenuType<>(AbstractTankMenu::t3, FeatureFlags.VANILLA_SET);
    public static final MenuType<AbstractTankMenu> TANK_4 = new MenuType<>(AbstractTankMenu::t4, FeatureFlags.VANILLA_SET);
    public static final MenuType<AbstractTankMenu> TANK_5 = new MenuType<>(AbstractTankMenu::t5, FeatureFlags.VANILLA_SET);
    public static final MenuType<AbstractTankMenu> TANK_6 = new MenuType<>(AbstractTankMenu::t6, FeatureFlags.VANILLA_SET);
    public static final MenuType<AbstractTankMenu> TANK_7 = new MenuType<>(AbstractTankMenu::t7, FeatureFlags.VANILLA_SET);

}
