package com.drivingSchool.app.core.confings.loaders;

import com.drivingSchool.app.core.confings.Config;

import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;


public class PropertyConfigLoader implements ConfigLoader {
    private Config config;

	public PropertyConfigLoader(String pathConfigFile) {
		Properties settings = new Properties();
		
		try (FileInputStream fileStream = new FileInputStream(pathConfigFile)) {
			settings.load(fileStream);

			this.config = new Config(settings.getProperty("api"), settings.getProperty("saveMode"));
		}
		catch (IOException error) {
			// Если произошла ошибка при открытии файла
		    throw new IllegalStateException(
		            "Не удалось загрузить конфигурацию: " + pathConfigFile,
		            error
		    );
		}
	}

	@Override
	public Config getConfig() { return this.config; }
}