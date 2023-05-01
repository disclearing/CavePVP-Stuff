package net.frozenorb.foxtrot.gameplay.blockshop.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.ItemBuilder;
import lombok.RequiredArgsConstructor;
import net.frozenorb.foxtrot.gameplay.blockshop.ShopItem;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Not created by vape on 10/30/2020 at 3:44 PM.
 */
@RequiredArgsConstructor
public class BlockShopMenu extends Menu {

    private static final Button GLASS = new GlassButton(7);

    @Override
    public String getTitle(Player player) {
        return "Block Shop";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (int i = 0; i < 9 * 5; i++) {
            buttons.put(i, GLASS);
        }

        buttons.put(12, new CategoryButton(
                ItemBuilder.of(Material.WOOL)
                        .name(CC.GREEN + CC.BOLD + "Wool")
                        .build(),
                IntStream.range(0, 16)
                        .mapToObj(index -> new ShopItem(
                                ItemBuilder.of(Material.WOOL, 64).data((short) index).build(),
                                500, index))
                        .collect(Collectors.toList())
        ));

        buttons.put(14, new CategoryButton(
                ItemBuilder.of(Material.STAINED_GLASS)
                        .name(CC.GREEN + CC.BOLD + "Glass")
                        .build(),

                Stream.concat(
                        Stream.of(new ShopItem(
                                ItemBuilder.of(Material.GLASS, 64).build(),
                                500, 0)),
                        IntStream.range(0, 16)
                                .mapToObj(index -> new ShopItem(
                                        ItemBuilder.of(Material.STAINED_GLASS, 64).data((short) index).build(),
                                        500, index + 1))
                ).collect(Collectors.toList())
        ));

        buttons.put(21, new CategoryButton(
                ItemBuilder.of(Material.SAPLING)
                        .name(CC.GREEN + CC.BOLD + "Saplings")
                        .build(),

                IntStream.range(0, 6)
                        .mapToObj(index -> new ShopItem(
                                ItemBuilder.of(Material.SAPLING, 64).data((short) index).build(),
                                500, index)
                        ).collect(Collectors.toList())
        ));

        buttons.put(22, new CategoryButton(
                ItemBuilder.of(Material.LOG)
                        .name(CC.GREEN + CC.BOLD + "Wood")
                        .build(),

                Stream.concat(
                        IntStream.range(0, 4)
                                .mapToObj(index -> new ShopItem(
                                        ItemBuilder.of(Material.LOG, 64).data((short) index).build(),
                                        500, index)),
                        IntStream.range(0, 2)
                                .mapToObj(index -> new ShopItem(
                                        ItemBuilder.of(Material.LOG_2, 64).data((short) index).build(),
                                        500, index + 4))
                ).collect(Collectors.toList())
        ));

        buttons.put(23, new CategoryButton(
                ItemBuilder.of(Material.GRASS)
                        .name(CC.GREEN + CC.BOLD + "Other Blocks")
                        .build(),
                ((Supplier<List<ShopItem>>) () -> {
                    List<ShopItem> items = new ArrayList<>();
                    int index = 0;
                    items.add(new ShopItem(new ItemStack(Material.COBBLESTONE, 64), 500, index++));
                    items.add(new ShopItem(new ItemStack(Material.STONE, 64), 500, index++));
                    items.add(new ShopItem(new ItemStack(Material.SMOOTH_BRICK, 64), 500, index++));
                    items.add(new ShopItem(new ItemStack(Material.BRICK, 64), 500, index++));
                    items.add(new ShopItem(new ItemStack(Material.SANDSTONE, 64), 500, index++));
                    items.add(new ShopItem(new ItemStack(Material.SAND, 64), 500, index++));
                    items.add(new ShopItem(new ItemStack(Material.SAND, 64, (short) 1), 500, index++));
                    items.add(new ShopItem(new ItemStack(Material.GRASS, 64), 500, index++));
                    items.add(new ShopItem(new ItemStack(Material.DIRT, 64), 500, index++));
                    items.add(new ShopItem(new ItemStack(Material.GRAVEL, 64), 500, index++));
                    items.add(new ShopItem(new ItemStack(Material.QUARTZ_BLOCK, 64), 500, index++));
                    items.add(new ShopItem(new ItemStack(Material.BOOKSHELF, 64), 500, index++));
                    items.add(new ShopItem(new ItemStack(Material.ITEM_FRAME, 1), 750, index++));
                    items.add(new ShopItem(new ItemStack(Material.FLOWER_POT, 1), 750, index++));
                    items.add(new ShopItem(new ItemStack(Material.MYCEL, 64), 750, index++));
                    items.add(new ShopItem(new ItemStack(Material.LEAVES, 64), 500, index++));
                    items.add(new ShopItem(new ItemStack(Material.LEAVES, 64, (byte)1), 500, index++));
                    items.add(new ShopItem(new ItemStack(Material.LEAVES, 64, (byte)2), 500, index++));
                    items.add(new ShopItem(new ItemStack(Material.LEAVES, 64, (byte)3), 500, index++));
                    items.add(new ShopItem(new ItemStack(Material.LEAVES_2, 64), 500, index++));
                    items.add(new ShopItem(new ItemStack(Material.LEAVES_2, 64, (byte)1), 500, index++));
                    return items;
                }).get()
        ));

        buttons.put(30, new CategoryButton(
                ItemBuilder.of(Material.STAINED_CLAY)
                        .name(CC.GREEN + CC.BOLD + "Stained Clay")
                        .build(),
                IntStream.range(0, 16)
                        .mapToObj(index -> new ShopItem(
                                ItemBuilder.of(Material.STAINED_CLAY, 64).data((short) index).build(),
                                500, index))
                        .collect(Collectors.toList())
        ));

        buttons.put(31, new CategoryButton(
                ItemBuilder.of(Material.INK_SACK)
                        .name(CC.GREEN + CC.BOLD + "Dye")
                        .data((short) 1)
                        .build(),

                IntStream.range(0, 16)
                        .mapToObj(index -> new ShopItem(
                                ItemBuilder.of(Material.INK_SACK, 64).data((short) index).build(),
                                500, index)
                        ).collect(Collectors.toList())
        ));

        buttons.put(32, new CategoryButton(
                ItemBuilder.of(Material.SNOW_BLOCK)
                        .name(ChatColor.translate("&c&lChristmas &2&lBlocks"))
                        .build(),
                ((Supplier<List<ShopItem>>) () -> {
                    List<ShopItem> items = new ArrayList<>();
                    int index = 0;
                    items.add(new ShopItem(new ItemStack(Material.SNOW_BLOCK, 64), 500, index++));
                    items.add(new ShopItem(new ItemStack(Material.PUMPKIN, 64), 500, index++));
                    items.add(new ShopItem(new ItemStack(Material.SNOW, 64), 500, index++));
                    return items;
                }).get()
        ));

        return buttons;
    }
}
