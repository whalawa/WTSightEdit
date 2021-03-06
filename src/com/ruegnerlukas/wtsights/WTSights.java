package com.ruegnerlukas.wtsights;

import com.ruegnerlukas.simpleutils.JarLocation;
import com.ruegnerlukas.simpleutils.SystemUtils;
import com.ruegnerlukas.simpleutils.collectionbuilders.MapBuilder;
import com.ruegnerlukas.simpleutils.logging.LogLevel;
import com.ruegnerlukas.simpleutils.logging.builder.DefaultMessageBuilder;
import com.ruegnerlukas.simpleutils.logging.filter.FilterLevel;
import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.simpleutils.logging.target.LogFileTarget;
import com.ruegnerlukas.wtsights.data.Database;
import com.ruegnerlukas.wtsights.ui.AmmoIcons;
import com.ruegnerlukas.wtsights.ui.ElementIcons;
import com.ruegnerlukas.wtsights.ui.view.ViewManager;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.ParamKey;
import com.ruegnerlukas.wtsights.ui.view.ViewManager.View;
import com.ruegnerlukas.wtutils.Config;
import com.ruegnerlukas.wtutils.FXUtils;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class WTSights extends Application {

	
	private static Stage primaryStage;
	
	public static boolean DEV_MODE = false;
	
	public static boolean wasStartedInsideData;

	
			
	public static void main(String[] args) {
		
		for(String arg : args) {
			if(arg.equalsIgnoreCase("dev")) {
				DEV_MODE = true;
			}
		}
		
		wasStartedInsideData = false;
		if(JarLocation.getJarLocation(WTSights.class).endsWith("\\data")) {
			wasStartedInsideData = true;
		}
		if(DEV_MODE) {
			wasStartedInsideData = false;
		}
		
		
		Logger.get().redirectStdOutput(LogLevel.DEBUG, LogLevel.ERROR);
		((DefaultMessageBuilder)Logger.get().getMessageBuilder()).setSourceNameSizeMin(23);
	    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
	        public void run() {
	    		Logger.get().info("=========================================");
	    		Logger.get().blankLine();
	    		Logger.get().blankLine();
	    		Logger.get().blankLine();
	    		Logger.get().blankLine();
	    		Logger.get().close();
	        }
	    }, "Shutdown-thread"));
		
	    
		if(DEV_MODE) {
			Logger.get().getFilterManager().addFilter(FilterLevel.only(LogLevel.values()));
			
		} else {
			Logger.get().getFilterManager().addFilter(FilterLevel.not(LogLevel.DEBUG));
			File logFile = new File(JarLocation.getJarLocation(WTSights.class) + (wasStartedInsideData ? "" : "/data") + "/log.txt");
			if(!logFile.exists()) {
				try {
					if(!logFile.getParentFile().exists()) {
						logFile.getParentFile().mkdir();
					}
					logFile.createNewFile();
				} catch (IOException e) {
					Logger.get().error(e);
				}
			}
			
			Logger.get().setLogTarget(new LogFileTarget(logFile, true));
		}
		
		Logger.get().blankLine();
		Logger.get().info("Starting Application (" + JarLocation.getJarLocation(WTSights.class) + ") DEV_MODE=" + DEV_MODE + " PATH=" + JarLocation.getJarLocation(WTSights.class) + "  inside=" + wasStartedInsideData);
		Logger.get().info("System information:   JAVA = " + SystemUtils.getJavaRuntimeName() +" "+ SystemUtils.getJavaVersion() + ",   OS = " + SystemUtils.getOSName());
		
		
		launch(args);
	}
	
	
	
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		
		Config.load(new File(JarLocation.getJarLocation(WTSights.class) + (wasStartedInsideData ? "" : "/data") + "/config.json"));
		
		WTSights.primaryStage = primaryStage;
		
		FXUtils.addIcons(primaryStage);
		
		if(DEV_MODE) {
			AmmoIcons.load("res/assets/ammo_icons_2.png", false);
			ElementIcons.load("res/assets/elementIcons.png", false);
		} else {
			AmmoIcons.load("/assets/ammo_icons_2.png", true);
			ElementIcons.load("/assets/elementIcons.png", true);
		}
		
		Database.loadVehicles(new File(JarLocation.getJarLocation(WTSights.class) + (wasStartedInsideData ? "" : "/data") + "/vehicle_data.xml"));
		
		primaryStage.setOnCloseRequest(event -> System.exit(0));
		
		ViewManager.getLoader(View.MAIN_MENU).openNew(primaryStage, new MapBuilder<ParamKey,Object>().get());
	}

	
	
	
	public static Stage getPrimaryStage() {
		return primaryStage;
	}
	
	
	
}
