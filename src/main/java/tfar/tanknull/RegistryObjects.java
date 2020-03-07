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
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import tfar.tanknull.container.BlockTankNullMenu;
import tfar.tanknull.container.ItemStackTankNullMenu;
import tfar.tanknull.recipe.Serializer2;

public class RegistryObjects {
  public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, TankNull.MODID);
  public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, TankNull.MODID);
  public static final DeferredRegister<TileEntityType<?>> BLOCK_ENTITIES = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, TankNull.MODID);
  public static final DeferredRegister<ContainerType<?>> MENUS = new DeferredRegister<>(ForgeRegistries.CONTAINERS, TankNull.MODID);

  public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS = new DeferredRegister<>(ForgeRegistries.RECIPE_SERIALIZERS, TankNull.MODID);

  public static final RegistryObject<Block> dock = BLOCKS.register("dock",
          () -> new TankNullDockBlock(Block.Properties.create(new Material.Builder(MaterialColor.IRON).build())
                  .hardnessAndResistance(1).harvestTool(ToolType.PICKAXE)));

  public static final RegistryObject<Item> dock_item = ITEMS.register("dock",
          () -> new BlockItem(dock.get(),new Item.Properties().group(ItemGroup.TOOLS)));

  public static final RegistryObject<Item> tank_1 = ITEMS.register("tank_1",
          () -> new TankNullItem(new Item.Properties().group(ItemGroup.TOOLS),1));
  public static final RegistryObject<Item> tank_2 = ITEMS.register("tank_2",
          () -> new TankNullItem(new Item.Properties().group(ItemGroup.TOOLS),2));
  public static final RegistryObject<Item> tank_3 = ITEMS.register("tank_3",
          () -> new TankNullItem(new Item.Properties().group(ItemGroup.TOOLS),3));
  public static final RegistryObject<Item> tank_4 = ITEMS.register("tank_4",
          () -> new TankNullItem(new Item.Properties().group(ItemGroup.TOOLS),4));
  public static final RegistryObject<Item> tank_5 = ITEMS.register("tank_5",
          () -> new TankNullItem(new Item.Properties().group(ItemGroup.TOOLS),5));
  public static final RegistryObject<Item> tank_6 = ITEMS.register("tank_6",
          () -> new TankNullItem(new Item.Properties().group(ItemGroup.TOOLS),6));
  public static final RegistryObject<Item> tank_7 = ITEMS.register("tank_7",
          () -> new TankNullItem(new Item.Properties().group(ItemGroup.TOOLS),7));


  public static final RegistryObject<ContainerType<BlockTankNullMenu>> block_container = MENUS.register("tank_block",
          () -> IForgeContainerType.create((windowId, inv, data) ->
                  new BlockTankNullMenu(windowId, inv, inv.player.world, data.readBlockPos()))
  );

  public static final RegistryObject<ContainerType<ItemStackTankNullMenu>> item_container = MENUS.register("tank_item",
          () -> new ContainerType<>(ItemStackTankNullMenu::new)
  );

  public static final RegistryObject<TileEntityType<?>> blockentity = BLOCK_ENTITIES.register("tank",
          () -> TileEntityType.Builder.create(TankNullDockBlockEntity::new,BLOCKS.getEntries().stream()
                  .map(RegistryObject::get).toArray(Block[]::new)).build(null));

  public static final RegistryObject<IRecipeSerializer<?>> upgrade = RECIPE_SERIALIZERS.register("upgrade",
          Serializer2::new);

}
