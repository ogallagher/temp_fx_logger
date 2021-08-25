package ogallagher.temp_fx_logger.test;

import ogallagher.temp_fx_logger.System;

import javafx.application.Application;
import javafx.stage.Stage;

public class TempFXLoggerDriver extends Application {
	static {
		System.out.println("message from TempFXLoggerDriver.static");
	}
	
	public static void main(String[] args) {
		System.out.println("message from TempFXLoggerDriver.main");
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		System.out.println("message from TempFXLoggerDriver.start");
	}
	
	@Override
	public void stop() {
		System.out.println("message from TempFXLoggerDriver.stop");
	}
}
