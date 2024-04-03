package com.github.yyeerai.hybridservermenu.menu;

import com.github.yyeerai.hybridserverapi.common.menu.api.MenuApi;
import com.github.yyeerai.hybridserverapi.common.menu.api.MenuCache;
import com.github.yyeerai.hybridservermenu.HybridServerMenu;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * MangerCommand类实现了TabExecutor接口，用于处理一系列的命令。
 * 这些命令包括：list, open, give, save, reload, reloadall。
 * 每个命令的处理逻辑都被封装到了一个单独的方法中，并存储在一个映射（Map）中。
 */
public class MangerCommand implements TabExecutor {

    // 存储命令和对应处理方法的映射
    private final Map<String, BiConsumer<CommandSender, String[]>> commandHandlers = new HashMap<>();

    // 在构造函数中初始化命令处理方法的映射
    public MangerCommand() {
        commandHandlers.put("list", this::handleList);
        commandHandlers.put("open", this::handleOpen);
        commandHandlers.put("give", this::handleGive);
        commandHandlers.put("save", this::handleSave);
        commandHandlers.put("reload", this::handleReload);
        commandHandlers.put("reloadall", this::handleReloadAll);
    }

    // 处理list命令的方法
    private void handleList(CommandSender commandSender, String[] strings) {
        if (commandSender.hasPermission("hsm.list")) {
            commandSender.sendMessage("菜单列表:");
            MenuApi.getMenuCaches().forEach(menuCache -> commandSender.sendMessage(menuCache.getName()));
        } else {
            commandSender.sendMessage("你没有权限");
        }
    }

    // 处理open命令的方法
    private void handleOpen(CommandSender commandSender, String[] strings) {
        if (commandSender.hasPermission("hsm.open")) {
            if (strings.length < 2) {
                commandSender.sendMessage("请输入菜单名称");
                return;
            }
            if (commandSender instanceof Player) {
                Player player = (Player) commandSender;
                MenuCache menuCache = HybridServerMenu.instance.getMenuManger().getMenuCache(strings[1]);
                if (menuCache != null) {
                    menuCache.toMenu().open(player);
                } else {
                    player.sendMessage("未找到指定菜单");
                }
            }
        } else {
            commandSender.sendMessage("你没有权限");
        }
    }

    // 处理give命令的方法
    private void handleGive(CommandSender commandSender, String[] strings) {
        if (commandSender.hasPermission("hsm.give")) {
            if (strings.length < 3) {
                commandSender.sendMessage("请输入玩家名称和菜单名称");
                return;
            }
            Player player = HybridServerMenu.instance.getServer().getPlayer(strings[1]);
            if (player != null) {
                MenuCache menuCache = HybridServerMenu.instance.getMenuManger().getMenuCache(strings[2]);
                if (menuCache != null) {
                    if (menuCache.getOpenItem() == null) {
                        commandSender.sendMessage("未找到指定菜单的开启物品");
                        return;
                    }
                    player.getInventory().addItem(menuCache.getOpenItem());
                    commandSender.sendMessage("给予成功");
                } else {
                    commandSender.sendMessage("未找到指定菜单");
                }
            } else {
                commandSender.sendMessage("未找到指定玩家");
            }
        } else {
            commandSender.sendMessage("你没有权限");
        }
    }

    // 处理save命令的方法
    private void handleSave(CommandSender commandSender, String[] strings) {
        if (commandSender.hasPermission("hsm.save")) {
            if (strings.length < 2) {
                commandSender.sendMessage("请输入物品要保存的名称");
                return;
            }
            if (commandSender instanceof Player) {
                Player player = (Player) commandSender;
                if (player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
                    commandSender.sendMessage("手中没有物品");
                    return;
                }
                HybridServerMenu.instance.getMenuManger().saveItem(player, strings[1], player.getInventory().getItemInMainHand());
            }
        } else {
            commandSender.sendMessage("你没有权限");
        }
    }

    // 处理reload命令的方法
    private void handleReload(CommandSender commandSender, String[] strings) {
        if (commandSender.hasPermission("hsm.reload")) {
            if (strings.length > 1) {
                HybridServerMenu.instance.getMenuManger().reloadMenu(strings[1]);
                commandSender.sendMessage("重载成功");
            } else {
                commandSender.sendMessage("请输入菜单名称");
            }
        } else {
            commandSender.sendMessage("你没有权限");
        }
    }

    // 处理reloadall命令的方法
    private void handleReloadAll(CommandSender commandSender, String[] strings) {
        if (commandSender.hasPermission("hsm.reload")) {
            HybridServerMenu.instance.getMenuManger().reloadAllMenu();
            commandSender.sendMessage("重载成功");
        } else {
            commandSender.sendMessage("你没有权限");
        }
    }

    // 实现TabExecutor接口的onCommand方法，用于处理命令
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (strings.length > 0) {
            BiConsumer<CommandSender, String[]> handler = commandHandlers.get(strings[0].toLowerCase());
            if (handler != null) {
                handler.accept(commandSender, strings);
                return true;
            }
        }
        return false;
    }

    // 实现TabExecutor接口的onTabComplete方法，用于提供命令补全
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 1) {
            return Stream.of("list", "open", "give", "save", "reload", "reloadall").filter(s1 -> s1.startsWith(strings[0])).collect(Collectors.toList());
        } else if (strings.length == 2) {
            if (strings[0].equalsIgnoreCase("open") || strings[0].equalsIgnoreCase("reload")) {
                return MenuApi.getMenuCaches().stream().map(MenuCache::getName).filter(s1 -> s1.startsWith(strings[1])).collect(Collectors.toList());
            }
            if (strings[0].equalsIgnoreCase("give")) {
                return HybridServerMenu.instance.getServer().getOnlinePlayers().stream().map(Player::getName).filter(s1 -> s1.startsWith(strings[1])).collect(Collectors.toList());
            }
        } else if (strings.length == 3) {
            if (strings[0].equalsIgnoreCase("give")) {
                return MenuApi.getMenuCaches().stream().map(MenuCache::getName).filter(s1 -> s1.startsWith(strings[2])).collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }
}