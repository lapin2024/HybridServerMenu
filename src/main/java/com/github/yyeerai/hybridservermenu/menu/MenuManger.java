package com.github.yyeerai.hybridservermenu.menu;

import com.github.yyeerai.hybridserverapi.common.menu.action.ActionExecutor;
import com.github.yyeerai.hybridserverapi.common.menu.action.ActionExecutorFactory;
import com.github.yyeerai.hybridserverapi.common.menu.api.MenuApi;
import com.github.yyeerai.hybridserverapi.common.menu.api.MenuCache;
import com.github.yyeerai.hybridserverapi.common.menu.button.Button;
import com.github.yyeerai.hybridserverapi.common.menu.button.ButtonAction;
import com.github.yyeerai.hybridserverapi.common.menu.button.ButtonBuilder;
import com.github.yyeerai.hybridserverapi.common.menu.enums.EnumClickType;
import com.github.yyeerai.hybridserverapi.common.menu.requirement.RequirementChecker;
import com.github.yyeerai.hybridserverapi.common.menu.requirement.RequirementCheckerFactory;
import com.github.yyeerai.hybridserverapi.common.menu.sound.MenuSound;
import com.github.yyeerai.hybridserverapi.common.serializeitem.ItemUtil;
import com.github.yyeerai.hybridserverapi.common.yaml.ConfigManager;
import com.github.yyeerai.hybridserverapi.common.yaml.RegisterConfig;
import com.github.yyeerai.hybridservermenu.HybridServerMenu;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 菜单管理器
 */
@Getter
public class MenuManger {


    private final Map<String, String> fileNameAndPath = new ConcurrentHashMap<>(); //菜单名称和路径的映射

    public MenuManger() {
        File file = new File(HybridServerMenu.instance.getDataFolder(), "menus");
        if (!file.exists()) {
            if (file.mkdirs()) {
                HybridServerMenu.instance.saveResource("menus/exampleMenu.yml", false);
            }
        }
        loadAllMenu();
    }

    /**
     * 搜索所有的菜单文件
     *
     * @param file 搜索目录
     */
    private void calculateAllFileNames(File file) {
        File[] files = file.listFiles();
        if (files == null) {
            return;
        }
        for (File file1 : files) {
            if (file1.isDirectory()) {
                calculateAllFileNames(file1);
            } else {
                if (file1.getName().endsWith(".yml")) {
                    String fileName = file1.getName().substring(0, file1.getName().length() - 4);
                    String path = file1.getPath().substring(file1.getPath().indexOf("menus") + 6);
                    if (!fileNameAndPath.containsKey(fileName)) {
                        fileNameAndPath.put(fileName, path);
                    } else {
                        HybridServerMenu.instance.getLogger().warning("菜单" + fileName + "存在重复,请检查");
                    }
                }
            }
        }
    }

    /**
     * 加载所有的菜单
     */
    public void loadAllMenu() {
        fileNameAndPath.clear();
        File file = new File(HybridServerMenu.instance.getDataFolder(), "menus");
        calculateAllFileNames(file);
        int count = 0;
        for (Map.Entry<String, String> stringStringEntry : fileNameAndPath.entrySet()) {
            MenuCache menuCache = loadMenu(stringStringEntry.getKey(), stringStringEntry.getValue());
            menuCache.registerHandler();
            count++;
        }
        HybridServerMenu.instance.getLogger().info("加载了" + count + "个菜单");
        fileNameAndPath.clear();
        calculateAllFileNames(file);
    }

    /**
     * 加载单个菜单
     *
     * @param menuName 菜单名称
     * @param path     菜单路径
     * @return 返回菜单缓存对象
     */
    private MenuCache loadMenu(String menuName, String path) {
        if (!fileNameAndPath.containsKey(menuName)) {
            HybridServerMenu.instance.getLogger().warning("菜单" + menuName + "不存在,添加默认配置文件");
        }
        ConfigManager menuConfig = RegisterConfig.registerConfig(HybridServerMenu.instance, "menus/" + path, true);
        initializeBlankMenu(menuConfig); //初始化空白菜单
        convertToLowercase(menuConfig.getConfig()); //所有的键转换为小写
        String title = getTitle(menuConfig);
        int size = menuConfig.getConfig().getInt("size", 9);
        String permission = getPermission(menuConfig);
        MenuSound openSound = getMenuSound(menuConfig, "opensound");
        MenuSound closeSound = getMenuSound(menuConfig, "closesound");
        List<String> openCommands = getOpenCommands(menuConfig);
        ItemStack openItem = getOpenItem(menuConfig);
        List<Button> buttons = getButtons(menuConfig);
        return new MenuApi.MenuCacheBuilder()
                .setPlugin(HybridServerMenu.instance)
                .setName(menuName)
                .setTitle(title)
                .setSize(size)
                .setPermission(permission)
                .setOpenSound(openSound)
                .setCloseSound(closeSound)
                .setButtons(buttons)
                .setOpenCommands(openCommands)
                .setOpenItem(openItem)
                .build();
    }

    /**
     * 初始化空白菜单
     *
     * @param menuConfig 菜单配置文件
     */
    private void initializeBlankMenu(ConfigManager menuConfig) {
        if (!menuConfig.getConfig().contains("title")) {
            menuConfig.getConfig().set("title", "error");
            menuConfig.getConfig().set("size", 9);
            menuConfig.saveConfig();
        }
    }

    /**
     * 这是从插件配置文件中获取打开菜单的命令
     *
     * @param menuConfig 菜单配置文件
     * @return 返回打开菜单的命令
     */
    private List<String> getOpenCommands(ConfigManager menuConfig) {
        return menuConfig.getConfig().getStringList("opencommands", new ArrayList<>());
    }

    /**
     * 这是从插件配置文件中获取打开菜单的物品
     *
     * @param menuConfig 菜单配置文件
     * @return 返回打开菜单的物品
     */
    private ItemStack getOpenItem(ConfigManager menuConfig) {
        if (!menuConfig.getConfig().contains("openitem")) {
            return null;
        }
        return ItemUtil.deserializeItemStack(menuConfig.getConfig().getSection("openitem").getStringRouteMappedValues(true));
    }

    private String getTitle(ConfigManager menuConfig) {
        return menuConfig.getConfig().getString("title", "error");
    }


    private String getPermission(ConfigManager menuConfig) {
        return menuConfig.getConfig().getString("permission", "");
    }

    /**
     * 获取菜单打开和关闭的声音
     *
     * @param menuConfig 菜单配置文件
     * @param sound      声音类型
     * @return 返回声音对象
     */
    private MenuSound getMenuSound(ConfigManager menuConfig, String sound) {
        String soundType;
        float soundVolume;
        float soundPitch;
        if (sound.equals("opensound")) {
            soundType = menuConfig.getConfig().getString("opensound.type", "");
            soundVolume = menuConfig.getConfig().getFloat("opensound.volume", 0.2F);
            soundPitch = menuConfig.getConfig().getFloat("opensound.pitch", 1.0F);
        } else {
            soundType = menuConfig.getConfig().getString("closesound.type", "");
            soundVolume = menuConfig.getConfig().getFloat("closesound.volume", 0.2F);
            soundPitch = menuConfig.getConfig().getFloat("closesound.pitch", 1.0F);
        }
        return soundType.isEmpty() ? null : new MenuSound(soundType, soundVolume, soundPitch);

    }

    /**
     * 获取菜单的按钮集合
     *
     * @param menuConfig 菜单配置文件
     * @return 返回按钮集合
     */
    private List<Button> getButtons(ConfigManager menuConfig) {
        List<Button> buttons = new ArrayList<>();
        if (menuConfig.getConfig().contains("buttons")) {
            for (Object key : menuConfig.getConfig().getSection("buttons").getKeys()) {
                Section section = menuConfig.getConfig().getSection("buttons." + key);
                Button button = getButton(section);
                if (button != null){
                    buttons.add(button);
                }
            }
        }
        return buttons;
    }

    /**
     * 获取单个按钮
     *
     * @param section 配置文件节点
     * @return 返回按钮对象
     */
    private Button getButton(Section section) {
        if(!section.contains("slots") || !section.contains("icon")){
            return null;
        }
        List<Integer> slots = section.getIntList("slots", new ArrayList<>()); //菜单槽位集合
        Map<String, Object> icon = section.getSection("icon").getStringRouteMappedValues(true); //菜单图标
        List<RequirementChecker> viewRequirement = new ArrayList<>(); //查看需求
        if (section.contains("view_requirements")) {
            for (String s : section.getStringList("view_requirements")) {
                RequirementCheckerFactory.getRequirementChecker(HybridServerMenu.instance, s).ifPresent(viewRequirement::add);
            }
        }
        int refreshDelay = section.getInt("refreshdelay", 0);//刷新延迟
        int priority = section.getInt("priority", 0); //优先级
        Map<EnumClickType, List<RequirementChecker>> requirementsMap = getRequirementMap(section);
        Map<EnumClickType, List<ActionExecutor>> allowActionExecutorsMap = getAllowActionExecutorsMap(section);
        Map<EnumClickType, List<ActionExecutor>> denyActionExecutorsMap = getDenyActionExecutorsMap(section);
        ButtonAction buttonAction = new ButtonAction(requirementsMap, allowActionExecutorsMap, denyActionExecutorsMap);
        return new ButtonBuilder()
                .setSlots(slots)
                .setIcon(icon)
                .setViewRequirements(viewRequirement)
                .setRefreshDelay(refreshDelay)
                .setPriority(priority)
                .setButtonAction(buttonAction)
                .build();
    }

    private Map<EnumClickType, List<ActionExecutor>> getDenyActionExecutorsMap(Section section) {
        Map<EnumClickType, List<ActionExecutor>> actionExecutorsMap = new HashMap<>();
        if (!section.contains("action")) {
            return actionExecutorsMap;
        }
        for (Object action : section.getSection("action").getKeys()) {
            EnumClickType.getClickTypeByName((String) action).ifPresent(enumClickType -> {
                if (section.contains("action." + action + ".denyactions")) {
                    List<ActionExecutor> actionExecutors = new ArrayList<>();
                    for (String s : section.getStringList("action." + action + ".denyactions")) {
                        ActionExecutorFactory.getActionExecutor(HybridServerMenu.instance, s).ifPresent(actionExecutors::add);
                    }
                    actionExecutorsMap.put(enumClickType, actionExecutors);
                }
            });
        }
        return actionExecutorsMap;
    }

    private Map<EnumClickType, List<ActionExecutor>> getAllowActionExecutorsMap(Section section) {
        Map<EnumClickType, List<ActionExecutor>> actionExecutorsMap = new HashMap<>();
        if (!section.contains("action")) {
            return actionExecutorsMap;
        }
        for (Object action : section.getSection("action").getKeys()) {
            EnumClickType.getClickTypeByName((String) action).ifPresent(enumClickType -> {
                if (section.contains("action." + action + ".actions")) {
                    List<ActionExecutor> actionExecutors = new ArrayList<>();
                    for (String s : section.getStringList("action." + action + ".actions")) {
                        ActionExecutorFactory.getActionExecutor(HybridServerMenu.instance, s).ifPresent(actionExecutors::add);
                    }
                    actionExecutorsMap.put(enumClickType, actionExecutors);
                }
            });
        }
        return actionExecutorsMap;
    }


    public Map<EnumClickType, List<RequirementChecker>> getRequirementMap(Section section) {
        Map<EnumClickType, List<RequirementChecker>> requirementsMap = new HashMap<>();
        if (!section.contains("action")) {
            return requirementsMap;
        }
        for (Object action : section.getSection("action").getKeys()) {
            EnumClickType.getClickTypeByName((String) action).ifPresent(enumClickType -> {
                List<RequirementChecker> requirementCheckers = new ArrayList<>();
                if (section.contains("action." + action + ".requirements")) {
                    for (String s : section.getStringList("action." + action + ".requirements")) {
                        RequirementCheckerFactory.getRequirementChecker(HybridServerMenu.instance, s).ifPresent(requirementCheckers::add);
                    }
                    requirementsMap.put(enumClickType, requirementCheckers);
                }
            });
        }
        return requirementsMap;
    }

    private String getPath(String s) {
        if (fileNameAndPath.containsKey(s)) {
            return fileNameAndPath.get(s);
        } else {
            return s + ".yml";
        }
    }

    public void reloadAllMenu() {
        MenuApi.unregisterAll();
        loadAllMenu();
    }

    public void reloadMenu(String menuName) {
        MenuApi.unregisterMenu(menuName);
        MenuCache menuCache = loadMenu(menuName, getPath(menuName));
        menuCache.registerHandler();
    }

    public @Nullable MenuCache getMenuCache(String menuName) {
        return MenuApi.getMenuCache(menuName).orElse(null);
    }


    public void saveItem(Player player, String name, ItemStack itemInMainHand) {
        Map<String, Object> stringObjectMap = ItemUtil.serializeItemStack(itemInMainHand);
        for (Map.Entry<String, Object> stringObjectEntry : stringObjectMap.entrySet()) {
            HybridServerMenu.instance.getConfigManager().getConfig().set(name + "." + stringObjectEntry.getKey(), stringObjectEntry.getValue());
        }
        Bukkit.getScheduler().runTaskAsynchronously(HybridServerMenu.instance, () -> {
            HybridServerMenu.instance.getConfigManager().saveConfig();
            player.sendMessage("保存成功,请打开config.yml查看");
        });
    }

    /**
     * 将所有的键转换为小写
     *
     * @param config 配置文件
     */
    @SneakyThrows
    private void convertToLowercase(YamlDocument config) {
        Map<String, Object> lowerCaseMap = new HashMap<>();
        for (String key : config.getRoutesAsStrings(true)) {
            if (config.isSection(key)) {
                continue;
            }
            lowerCaseMap.put(key.toLowerCase(), config.get(key));
        }
        for (Map.Entry<String, Object> entry : lowerCaseMap.entrySet()) {
            config.set(entry.getKey(), entry.getValue());
        }
    }

    @SuppressWarnings("unused")
    @Nullable
    public MenuCache getMenuCacheByName(String menuName) {
        return MenuApi.getMenuCache(menuName).orElse(null);
    }

}