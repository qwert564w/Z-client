package com.zclient.client.module.manager;

import com.zclient.client.module.Module;
import com.zclient.client.module.modules.combat.AimAssist;
import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    public static List<Module> modules = new ArrayList<>();

    public static void init() {
        // Регистрация модулей
        modules.add(new AimAssist());
        
        // Здесь будут добавлены другие модули по мере их создания
        // modules.add(new Killaura());
        // modules.add(new ESP());
        // и т.д.
    }

    public static List<Module> getModules() {
        return modules;
    }

    public static Module getModuleByName(String name) {
        for (Module module : modules) {
            if (module.getName().equalsIgnoreCase(name)) {
                return module;
            }
        }
        return null;
    }
}
