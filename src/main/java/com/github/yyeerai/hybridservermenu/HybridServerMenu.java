package com.github.yyeerai.hybridservermenu;


import com.github.yyeerai.hybridserverapi.common.yaml.ConfigManager;
import com.github.yyeerai.hybridserverapi.common.yaml.RegisterConfig;
import com.github.yyeerai.hybridservermenu.menu.MangerCommand;
import com.github.yyeerai.hybridservermenu.menu.MenuManger;
import lombok.Getter;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
@SuppressWarnings("unused")
public class HybridServerMenu extends JavaPlugin {

    public static HybridServerMenu instance;
    private ConfigManager configManager;
    private MenuManger menuManger;

    @Override
    public void onEnable() {
        instance = this;
        configManager = RegisterConfig.registerConfig(this, "config.yml", false);
        menuManger = new MenuManger();
        PluginCommand menu = getCommand("hybridservermenu");
        if (menu != null) {
            menu.setExecutor(new MangerCommand());
            menu.setTabCompleter(new MangerCommand());
        }
        getLogger().info("混合服务器菜单插件已启用");
    }

}