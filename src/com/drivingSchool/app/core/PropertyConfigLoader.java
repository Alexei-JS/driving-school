package com.drivingSchool.app.core;

import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;


public class PropertyConfigLoader {
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
	
	public Config getConfig() { return this.config; }
}