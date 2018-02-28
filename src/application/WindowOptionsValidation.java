package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

public class WindowOptionsValidation {

	private boolean isSolutionReadyToStart;
	private boolean isFileValid;
	private boolean isNumberOfRoutesValid;
	private boolean isNumberOfGenerationValid;
	private boolean isNumberOfSameRoutesValid;
	private int[][] distancesArray;
	int problemSize;
	// TextFormatter textFormatter = new TextFormatter();

	public WindowOptionsValidation() {
		this.isFileValid = false;
		this.isNumberOfRoutesValid = false;
		this.isNumberOfGenerationValid = false;
		this.isNumberOfSameRoutesValid = true;
		problemSize = 0;
	}

	public boolean isSolutionReadyToStart() {

		if (isFileValid == true && isNumberOfRoutesValid == true && isNumberOfGenerationValid == true
				&& isNumberOfSameRoutesValid == true)
			return true;
		else
			return false;
	}
	

	public boolean checkFile(String filePath, ArrayList<String> fileLines) {
		if (getFileLines(filePath, fileLines)) {
			int tempProblemSize = 0;
			int[][] tempDistances;
			if (fileLines.size() != 0) {
				try {
					tempProblemSize = Integer.parseInt(fileLines.get(0).trim());
				}

				catch (NumberFormatException numberFormatException) {
					InfoWindow.displayWarning("Wrong content type", "first line must be integer");
					isFileValid = false;
					return false;
				}
			}

			tempDistances = new int[tempProblemSize][tempProblemSize];

			if (tempProblemSize != 0 && fileLines.size() - 1 == tempProblemSize) {

				try {
					for (int lineNumber = 1; lineNumber <= tempProblemSize; lineNumber++) {
						String[] tempLineValues = fileLines.get(lineNumber).split(" ");

						if (tempLineValues.length == lineNumber) {
							int rowCounter = 0;

							for (String value : tempLineValues) {

								tempDistances[rowCounter][lineNumber - 1] = Integer.parseInt(value);
								tempDistances[lineNumber - 1][rowCounter] = Integer.parseInt(value);
								rowCounter++;
							}
						} else {
							InfoWindow.displayWarning("Wrong file Content", "Wrong file Content");
							isFileValid = false;
							return false;
						}
					}
				}

				catch (NumberFormatException numberFormatException) {
					InfoWindow.displayWarning("Wrong File Content", "Wrong file content");
					isFileValid = false;
					return false;
				}
			}

			else {
				InfoWindow.displayWarning("Wrong File Content", "Wrong file content");
				isFileValid = false;
				return false;
			}

			this.isFileValid = true;
			distancesArray = tempDistances;
			return true;

		} else
			return false;
	}

	public void CheckNumberOfRoutes(TextField routesTextField) {
		String value = routesTextField.getText();
		if (value.length() == 0 || value.charAt(0) == '0' || value.equals("1"))
			isNumberOfRoutesValid = false;
		else
			isNumberOfRoutesValid = true;
	}

	public void CheckNumberOfGeneration(TextField generationsTextField) {
		String value = generationsTextField.getText();
		if (value.length() == 0 || value.charAt(0) == '0')
			isNumberOfGenerationValid = false;
		else
			isNumberOfGenerationValid = true;

	}

	public void checkNumberOfSameRoutes(TextField sameRoutesTextField, CheckBox sameRoutesCheckBox) {
		String value = sameRoutesTextField.getText();

		if (sameRoutesCheckBox.isSelected()) {
			if (value.length() == 0 || value.charAt(0) == '0')
				isNumberOfSameRoutesValid = false;
			else
				isNumberOfSameRoutesValid = true;
		} else {
			isNumberOfSameRoutesValid = true;
		}
	}

	private boolean getFileLines(String filePath, ArrayList<String> fileLines) {
		fileLines.clear();
		File file = new File(filePath);

		try (FileReader fileReader = new FileReader(file);
				BufferedReader bufferedReader = new BufferedReader(fileReader);) {

			String line = "";

			while ((line = bufferedReader.readLine()) != null)
				fileLines.add(line.trim());

		} catch (FileNotFoundException e) {
			InfoWindow.displayWarning("File not found", "Selected file not found");
			isFileValid = false;
			return false;

		} catch (IOException e) {
			InfoWindow.displayWarning("File handling error", "Problem with reading the file");
			isFileValid = false;
			return false;
		}
		return true;
	}
	
	public int[][] getDistancesArray(){
		return distancesArray;
	}
}
