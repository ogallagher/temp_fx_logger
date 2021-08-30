package ogallagher.temp_fx_logger;

import java.awt.List;
import java.util.LinkedList;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * 
 * <p>
 * This is just a placeholder until a proper debugging console is built into the 
 * parent app's GUI.
 * </p>
 * <p>
 * Its name collides with java.lang.System purposefully so it can
 * be quickly plugged into an app that otherwise logs everything to the console.
 * </p>
 * <p>
 * Methods and properties with the debug prefix indicate something that's not part of
 * the java.lang.System API.
 * </p>
 * 
 * @author Owen Gallagher
 * @version {@value System#VERSION}
 *
 */
public class System {
	public static final String VERSION = "0.1.3";
	
	public static Out out = new Out();
	
	public static String getProperty(String key) {
		return java.lang.System.getProperty(key);
	}
	
	public static void debugSetConsoleTitle(String title) {
		out.consoleWindow.setTitle(title);
	}
	
	public static void debugSetConsoleMaxHistory(long maxHistory) {
		 out.setMaxHistory(maxHistory);
	}
	
	public static Stage getConsoleWindow() {
		return out.consoleWindow;
	}
	
	public static class Out {
		private final String WINDOW_NAME = "temp_fx_logger.System.out.console";
		private long maxHistory = Long.MAX_VALUE;
		
		private Stage consoleWindow;
		private ListView<String> consoleView;
		private ObservableList<String> console;
		private LinkedList<String> preguiConsole;
		
		private boolean tryInit = true;
		
		public Out() {
			preguiConsole = new LinkedList<>();
			
			init();
		}
		
		/**
		 * Attempt to initialize javafx-dependent members {@link #consoleWindow}, 
		 * {@link #consoleView}, {@link #console}.<br><br>
		 * 
		 * Updates {@link #tryInit} to {@code false} if the initialization is successful.
		 */
		private void init() {			
			try {
				Platform.runLater(new Runnable() {
					public void run() {
						Rectangle2D monitor = Screen.getPrimary().getBounds();
						
						consoleWindow = new Stage(StageStyle.DECORATED);
						consoleWindow.setTitle(WINDOW_NAME);
						
						consoleWindow.setWidth(500);
						consoleWindow.setHeight(800);
						
						//move to bottom right corner
						consoleWindow.setX(monitor.getWidth() - consoleWindow.getWidth());
						consoleWindow.setY(monitor.getHeight() - consoleWindow.getHeight());
						
						consoleView = new ListView<>();
						consoleView.prefWidthProperty().bind(consoleWindow.widthProperty());
						consoleView.prefHeightProperty().bind(consoleWindow.heightProperty());
						
						console = consoleView.getItems();
						
						Scene consoleScene = new Scene(consoleView);
						consoleWindow.setScene(consoleScene);
						consoleWindow.show();
					}
				});
				
				// initialization complete
				tryInit = false;
				
				// flush pre-gui console
				for (String preguiMessage : preguiConsole) {
					print(preguiMessage);
				}
				preguiConsole = null;
			}
			catch (IllegalStateException e) {
				// javafx thread not ready, try again later
				tryInit = true;
			}
		}
		
		public void print(final Object object) {
			// ensure members exist
			if (tryInit) {
				init();
			}
			
			if (!tryInit) {
				//print to system console
				java.lang.System.out.print(object);
				
				//print to javafx console
				Platform.runLater(new Runnable() {
					public void run() {
						console.add(object.toString());
						
						if (console.size() > maxHistory) {
							console.remove(0);
						}
					}
				});
			}
			else {
				// enqueue pre-gui message
				preguiConsole.add(object.toString());
			}
		}
		
		public void println(Object object) {
			print(object.toString() + '\n');
		}
		
		public void println() {
			print("\n");
		}
		
		public void setMaxHistory(long maxHistory) {
			this.maxHistory = maxHistory;
		}
	}
}
