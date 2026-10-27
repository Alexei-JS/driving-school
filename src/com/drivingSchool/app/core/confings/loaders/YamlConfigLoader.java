package com.drivingSchool.app.core.confings.loaders;

import com.drivingSchool.app.core.confings.Config;


public class YamlConfigLoader implements ConfigLoader {
    private Config config;

    public YamlConfigLoader(String pathConfigFile) {
        // Для возможности использовать конфиг файлы с расширением .yaml
    }

    @Override
    public Config getConfig() { return this.config; }
}
