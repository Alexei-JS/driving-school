package com.drivingSchool.app.core;

import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;


public class ConfigLoader {
	private String api;
	private String saveMode;

	public ConfigLoader(String pathConfigFile) {
		Properties settings = new Properties();
		
		try (FileInputStream fileStream = new FileInputStream(pathConfigFile)) {
			settings.load(fileStream);
			
			this.api = settings.getProperty("api");
			this.saveMode = settings.getProperty("saveMode");
		}
		catch (IOException error) {
			// Если произошла ошибка при открытии файла
		    throw new IllegalStateException(
		            "Не удалось загрузить конфигурацию: " + pathConfigFile,
		            error
		    );
		}
	}
	
	public String getAPI() {
		return this.api;
	}
	
	public String getSaveMode() {
		return this.saveMode;
	}
}