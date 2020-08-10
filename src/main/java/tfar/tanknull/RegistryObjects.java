package tfar.tanknull;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import tfar.tanknull.container.BlockTankNullMenu;
import tfar.tanknull.container.ItemStackTankNullMenu;
import tfar.tanknull.recipe.Serializer2;

public class RegistryObjects {

  public static final Block dock = new TankNullDockBlock(Block.Properties.create(new Material.Builder(MaterialColor.IRON).build()).hardnessAndResistance(1).harvestTool(ToolType.PICKAXE));

  public static final Item dock_item = new BlockItem(dock,new Item.Properties().group(ItemGroup.TOOLS));

  public static final Item tank_1 = new TankNullItem(new Item.Properties().group(ItemGroup.TOOLS),TankStats.one);
  public static final Item tank_2 = new TankNullItem(new Item.Properties().group(ItemGroup.TOOLS),TankStats.two);
  public static final Item tank_3 = new TankNullItem(new Item.Properties().group(ItemGroup.TOOLS),TankStats.three);
  public static final Item tank_4 = new TankNullItem(new Item.Properties().group(ItemGroup.TOOLS),TankStats.four);
  public static final Item tank_5 = new TankNullItem(new Item.Properties().group(ItemGroup.TOOLS),TankStats.five);
  public static final Item tank_6 = new TankNullItem(new Item.Properties().group(ItemGroup.TOOLS),TankStats.six);
  public static final Item tank_7 = new TankNullItem(new Item.Properties().group(ItemGroup.TOOLS),TankStats.seven);

  public static final ContainerType<BlockTankNullMenu> tank_1_container = IForgeContainerType.create(BlockTankNullMenu::t1);
  public static final ContainerType<BlockTankNullMenu> tank_2_container = IForgeContainerType.create(BlockTankNullMenu::t2);
  public static final ContainerType<BlockTankNullMenu> tank_3_container = IForgeContainerType.create(BlockTankNullMenu::t3);
  public static final ContainerType<BlockTankNullMenu> tank_4_container = IForgeContainerType.create(BlockTankNullMenu::t4);
  public static final ContainerType<BlockTankNullMenu> tank_5_container = IForgeContainerType.create(BlockTankNullMenu::t5);
  public static final ContainerType<BlockTankNullMenu> tank_6_container = IForgeContainerType.create(BlockTankNullMenu::t6);
  public static final ContainerType<BlockTankNullMenu> tank_7_container = IForgeContainerType.create(BlockTankNullMenu::t7);

  public static final ContainerType<BlockTankNullMenu> tank_1_portable = new ContainerType<>(ItemStackTankNullMenu::t1c);
  public static final ContainerType<BlockTankNullMenu> tank_2_portable = new ContainerType<>(ItemStackTankNullMenu::t2c);
  public static final ContainerType<BlockTankNullMenu> tank_3_portable = new ContainerType<>(ItemStackTankNullMenu::t3c);
  public static final ContainerType<BlockTankNullMenu> tank_4_portable = new ContainerType<>(ItemStackTankNullMenu::t4c);
  public static final ContainerType<BlockTankNullMenu> tank_5_portable = new ContainerType<>(ItemStackTankNullMenu::t5c);
  public static final ContainerType<BlockTankNullMenu> tank_6_portable = new ContainerType<>(ItemStackTankNullMenu::t6c);
  public static final ContainerType<BlockTankNullMenu> tank_7_portable = new ContainerType<>(ItemStackTankNullMenu::t7c);


  public static final TileEntityType<?> blockentity = TileEntityType.Builder.create(TankNullDockBlockEntity::new,dock).build(null);

  public static final IRecipeSerializer<?> upgrade = new Serializer2();

}
