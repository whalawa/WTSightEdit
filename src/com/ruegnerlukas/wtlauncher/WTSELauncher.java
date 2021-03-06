package com.ruegnerlukas.wtlauncher;

import java.io.File;
import java.io.IOException;

import com.ruegnerlukas.simpleutils.JarLocation;
import com.ruegnerlukas.simpleutils.SystemUtils;
import com.ruegnerlukas.simpleutils.logging.LogLevel;
import com.ruegnerlukas.simpleutils.logging.filter.FilterLevel;
import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.simpleutils.logging.target.LogFileTarget;
import com.ruegnerlukas.wtsights.ui.main.MainMenuController;
import com.ruegnerlukas.wtsights.ui.view.ViewManager;
import com.ruegnerlukas.wtutils.Config;
import com.ruegnerlukas.wtutils.FXUtils;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class WTSELauncher extends Application {

	
	public static String BASE_DIR = "";
	public static boolean DEV_MODE = false;

	
	
	
	public static void main(String[] args) {
		
		// dev mode
		for(String arg : args) {
			if(arg.equalsIgnoreCase("dev")) {
				DEV_MODE = true;
			}
		}
		
		// base dir
		BASE_DIR = new File(JarLocation.getJarFile(WTSELauncher.class)).getParentFile().getAbsolutePath();
		BASE_DIR = BASE_DIR.replaceAll("%20", " ");
		
		
		// logger
		Logger.get().redirectStdOutput(LogLevel.DEBUG, LogLevel.ERROR);
	    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
	        public void run() {
	    		Logger.get().info("=========================================");
	    		Logger.get().blankLine();
	    		Logger.get().close();
	        }
	    }, "Shutdown-thread"));

		if(DEV_MODE) {
			Logger.get().getFilterManager().addFilter(FilterLevel.only(LogLevel.values()));
			
		} else {
			Logger.get().getFilterManager().addFilter(FilterLevel.not(LogLevel.DEBUG));
			File logFile = new File(BASE_DIR + "\\data\\log.txt");
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
		
		
		launch(args);
	}
	
	
	
	
	@Override
	public void start(Stage primaryStage) throws Exception {
	
		Logger.get().info("=============== LAUNCHER ===============");
		Logger.get().info("BaseDir: " + BASE_DIR);
		Logger.get().info("System information:   JAVA = " + SystemUtils.getJavaRuntimeName() +" "+ SystemUtils.getJavaVersion() + ",   OS = " + SystemUtils.getOSName());

		
		Config.load(new File(BASE_DIR + "\\data\\config.json"));
		
		FXUtils.addIcons(primaryStage);

		FXMLLoader loader = new FXMLLoader(MainMenuController.class.getResource("/ui/layout_launcher.fxml"));
		loader.setResources(ViewManager.getResources());
		Parent root = (Parent) loader.load();
		LauncherController controller = (LauncherController)loader.getController();
		Scene scene = new Scene(root, 470, 135, true);
		primaryStage.setTitle(ViewManager.getResources().getString("lc_title"));
		primaryStage.setScene(scene);
		
		controller.start();
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				System.exit(0);
			} 
		});
		
		primaryStage.show();
		
	}
	
	
	
	
}
