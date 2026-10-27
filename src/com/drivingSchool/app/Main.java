package com.drivingSchool.app;

import com.drivingSchool.app.core.confings.loaders.PropertyConfigLoader;
import com.drivingSchool.app.core.confings.Config;
import com.drivingSchool.app.core.ConfigsManager;


public class Main
{
    public static void main(String[] args) {
    	PropertyConfigLoader config = new PropertyConfigLoader("src/com/drivingSchool/config.properties");
    	ConfigsManager configsManager = new ConfigsManager(config.getConfig());

		configsManager.run();

    	System.out.print("Бот отработал в штатном режиме.");
    }
}