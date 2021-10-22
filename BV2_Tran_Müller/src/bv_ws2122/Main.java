// BV Ue2 WS2021/22 Vorgabe
//
// Copyright (C) 2021 by Klaus Jung
// All rights reserved.
// Date: 2021-07-22
 		   		     	

package bv_ws2122;
	
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) throws Exception {
		BorderPane root = (BorderPane)loadFXML("PerspectiveAppView.fxml");
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Perspective - WS2021/22 - Tran_Müller"); 
		primaryStage.show();
	}
 		   		     	
	public static void main(String[] args) {
		launch(args);
	}
	
	private Object loadFXML(String resourceName) throws Exception {
		String path = System.getProperty("user.home") + File.separator + "src" + File.separator + "Java" + File.separator + "PixelatedImageView.jar";
		try {
			URL url = new File(path).toURI().toURL();
			URLClassLoader classLoader = (URLClassLoader)ClassLoader.getSystemClassLoader();
			Method addMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
			addMethod.setAccessible(true);
			addMethod.invoke(classLoader, url);
			Method method = classLoader.loadClass("de.htw.lcs.fx.FXMLLoaderEx").getMethod("load", InputStream.class);
			Object object = method.invoke(null, getClass().getResourceAsStream(resourceName));
			return object;
		} catch (Exception e) {
			System.out.println("Cannot load methodes from " + path);
    		return FXMLLoader.load(getClass().getResource(resourceName));
	    }		
	}
}
 		   		     	




