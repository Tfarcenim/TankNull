package tfar.tanknull;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
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

  public static final RegistryObject<Block> tank_1 = BLOCKS.register("tank_1",
          () -> new TankNullBlock(Block.Properties.create(Material.IRON),1));
  public static final RegistryObject<Block> tank_2 = BLOCKS.register("tank_2",
          () -> new TankNullBlock(Block.Properties.create(Material.IRON),2));
  public static final RegistryObject<Block> tank_3 = BLOCKS.register("tank_3",
          () -> new TankNullBlock(Block.Properties.create(Material.IRON),3));
  public static final RegistryObject<Block> tank_4 = BLOCKS.register("tank_4",
          () -> new TankNullBlock(Block.Properties.create(Material.IRON),4));
  public static final RegistryObject<Block> tank_5 = BLOCKS.register("tank_5",
          () -> new TankNullBlock(Block.Properties.create(Material.IRON),5));
  public static final RegistryObject<Block> tank_6 = BLOCKS.register("tank_6",
          () -> new TankNullBlock(Block.Properties.create(Material.IRON),6));
  public static final RegistryObject<Block> tank_7 = BLOCKS.register("tank_7",
          () -> new TankNullBlock(Block.Properties.create(Material.IRON),7));

  public static final RegistryObject<Item> tank_1_item = ITEMS.register("tank_1",
          () -> new TankNullItem(tank_1.get(),new Item.Properties().group(ItemGroup.TOOLS)));
  public static final RegistryObject<Item> tank_2_item = ITEMS.register("tank_2",
          () -> new TankNullItem(tank_2.get(),new Item.Properties().group(ItemGroup.TOOLS)));
  public static final RegistryObject<Item> tank_3_item = ITEMS.register("tank_3",
          () -> new TankNullItem(tank_3.get(),new Item.Properties().group(ItemGroup.TOOLS)));
  public static final RegistryObject<Item> tank_4_item = ITEMS.register("tank_4",
          () -> new TankNullItem(tank_4.get(),new Item.Properties().group(ItemGroup.TOOLS)));
  public static final RegistryObject<Item> tank_5_item = ITEMS.register("tank_5",
          () -> new TankNullItem(tank_5.get(),new Item.Properties().group(ItemGroup.TOOLS)));
  public static final RegistryObject<Item> tank_6_item = ITEMS.register("tank_6",
          () -> new TankNullItem(tank_6.get(),new Item.Properties().group(ItemGroup.TOOLS)));
  public static final RegistryObject<Item> tank_7_item = ITEMS.register("tank_7",
          () -> new TankNullItem(tank_7.get(),new Item.Properties().group(ItemGroup.TOOLS)));


  public static final RegistryObject<ContainerType<BlockTankNullMenu>> block_container = MENUS.register("tank_block",
          () -> IForgeContainerType.create((windowId, inv, data) ->
                  new BlockTankNullMenu(windowId, inv, inv.player.world, data.readBlockPos()))
  );

  public static final RegistryObject<ContainerType<ItemStackTankNullMenu>> item_container = MENUS.register("tank_item",
          () -> new ContainerType<>(ItemStackTankNullMenu::new)
  );

  public static final RegistryObject<TileEntityType<?>> blockentity = BLOCK_ENTITIES.register("tank",
          () -> TileEntityType.Builder.create(TankNullBlockEntity::new,BLOCKS.getEntries().stream()
                  .map(RegistryObject::get).toArray(Block[]::new)).build(null));

  public static final RegistryObject<IRecipeSerializer<?>> upgrade = RECIPE_SERIALIZERS.register("upgrade",
          Serializer2::new);

}
