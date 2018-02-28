package application;

import java.util.ArrayList;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;

public class InfoWindow {

	public static void displayWarning(String title, String message)
	{
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
	
	public static void displayBestRoute(ArrayList<Integer> citiesOrder, int routeLenght) {
		
		TextArea textArea = new TextArea(citiesOrder.toString() + " lenght = " + routeLenght);
		textArea.setEditable(false);
		textArea.setWrapText(true);
		GridPane gridPane = new GridPane();
		gridPane.setMaxWidth(Double.MAX_VALUE);
		gridPane.add(textArea, 0, 0);
		
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Best Route cities order");
		alert.getDialogPane().setContent(gridPane);
		alert.showAndWait();
	}
}
