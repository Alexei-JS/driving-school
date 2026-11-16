package com.drivingSchool.app.core.confings.loaders;

import com.drivingSchool.app.core.confings.Config;

import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;


public class PropertyConfigLoader implements ConfigLoader {
    private static final String BOT_SETTINGS_FILE = "bot.properties";

    private Config config;

	public PropertyConfigLoader(String pathConfigFile) {
		Properties settings = new Properties();

		try (FileInputStream fileStream = new FileInputStream(pathConfigFile)) {
			settings.load(fileStream);
		}
		catch (IOException error) {
		    throw new IllegalStateException(
		            "Не удалось загрузить конфигурацию: " + pathConfigFile,
		            error
		    );
		}

		// Токен лежит отдельно, потому что config.properties попадает в git
		Properties botSettings = this.loadBotSettings(BOT_SETTINGS_FILE);

		this.config = new Config(
				settings.getProperty("api"),
				settings.getProperty("saveMode"),
				botSettings.getProperty("bot.token"),
				this.parseAdminId(botSettings.getProperty("bot.admin_id"))
		);
	}

	private Properties loadBotSettings(String path) {
		Properties settings = new Properties();

		try (FileInputStream fileStream = new FileInputStream(path)) {
			settings.load(fileStream);
		}
		catch (IOException error) {
			// Токен не обязателен: без него просто не запустится Telegram-режим
		}

		return settings;
	}

	private long parseAdminId(String value) {
		if (value == null) {
			return 0L;
		}

		try {
			return Long.parseLong(value.trim());
		}
		catch (NumberFormatException error) {
			return 0L;
		}
	}

	@Override
	public Config getConfig() { return this.config; }
}