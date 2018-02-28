package application;

import algorithmTypes.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ChangeEvent;

import algorithm.GenerationStats;
import algorithm.Population;
import algorithm.SolutionInfo;
import algorithm.SolutionInfo.SolutionInfoBuilder;
import algorithm.SolutionStats;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Main extends Application {

	Stage primaryStage;
	Scene mainScene;

	SolutionStats currentSolutionStats;
	SolutionInfo currentSolutionInfo;
	Population currentPopulation;
	WindowOptionsValidation windowOptionsValidatdion = new WindowOptionsValidation();
	ArrayList<String> fileLines = new ArrayList<>();
	Pane root = new VBox(30);
	boolean isSolutionRunning = false;

	// Declaration and initialiation controls in Panes that Root Pane Contains, from
	// top to bottom
	Pane fileBarPane = new HBox(25);
	Button openFileButton = new Button("Open file");
	TextField fileAdressTextField = new TextField();
	Label problemSizeLabel = new Label("Problem size: no file selected");
	TextArea problemSizeTextArea = new TextArea();

	GridPane selectOptionsPane = new GridPane();
	Label numberOfRoutesLabel = new Label("Number of specimans");
	Label numberOfRoutesInfoLabel = new Label("(Greater than 1)");
	Label selectionTypeLabel = new Label("Selection Type");
	Label crossOverTypeLabel = new Label("Crossover Type");
	Label crossOverRateLabel = new Label("Crossover Rate");
	Label mutationTypeLabel = new Label("Mutation Type");
	Label mutationRateLabel = new Label("Mutation Rate");
	Label numberOfGenerationsLabel = new Label("Number of generations");
	CheckBox stopAfterSameBestCheckBox = new CheckBox("Stop after x same best routes");
	TextField numberOfRoutesTextField = new TextField();
	TextField numberOfGenerationsTextField = new TextField();
	TextField stopAfterSameBestTextField = new TextField();
	TextField currentBestTextField = new TextField("Current best distance");
	ChoiceBox selectionTypeChoiceBox = new ChoiceBox();
	ChoiceBox crossoverTypeChoiceBox = new ChoiceBox();
	ChoiceBox mutationTypeChoiceBox = new ChoiceBox();
	Slider crossoverRateSlider = new Slider(0, 100, 20);
	Label crossoverRateValueLabel = new Label(Math.round(crossoverRateSlider.getValue()) + "%");
	Slider mutationRateSlider = new Slider(0, 100, 10);
	Label mutationRateValueLabel = new Label(Math.round(mutationRateSlider.getValue()) + "%");;

	Pane solutionButtonsPane = new HBox(20);
	Button startStopSolutionButton = new Button("Start solution");
	Button showStatisticsButton = new Button("Show Statistics");
	Button showBestRouteButton = new Button("Show Best Route");

	HBox progressBarPane = new HBox();
	ProgressBar algorithmProgressBar = new ProgressBar();

	@Override
	public void start(Stage primaryStage) {
		try {
			this.primaryStage = primaryStage;
			setFileBarPane();
			setSelectOptionsPane();
			setSolutionButtonsPane();
			setProgressBarPane();
			setRootPane();

			Scene mainSceneTemp = new Scene(root, 820, 500);
			mainSceneTemp.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			this.mainScene = mainSceneTemp;
			primaryStage.setScene(mainSceneTemp);
			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	private void setFileBarPane() {
		openFileButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				openFile();
			}
		});

		fileAdressTextField.setId("fileAdressTextField");
		fileAdressTextField.setEditable(false);
		fileBarPane.setId("fileBarPane");
		fileBarPane.setPadding(new Insets(5));
		fileBarPane.getChildren().addAll(openFileButton, fileAdressTextField, problemSizeLabel);
	}

	private void setSelectOptionsPane() {
		selectionTypeChoiceBox.getItems().setAll(SelectionTypes.values());
		selectionTypeChoiceBox.setValue(SelectionTypes.TOURNAMENT);

		crossoverTypeChoiceBox.getItems().setAll(CrossoverTypes.values());
		crossoverTypeChoiceBox.valueProperty().setValue(CrossoverTypes.PARTIALLY_MAPPED_CROSSOVER);

		crossoverRateSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

				crossoverRateValueLabel.setText(Math.round(newValue.intValue()) + "%");
			}
		});

		mutationTypeChoiceBox.getItems().setAll(MutationTypes.values());
		mutationTypeChoiceBox.setValue(MutationTypes.INVERSION);

		mutationRateSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

				mutationRateValueLabel.setText(Math.round(newValue.intValue()) + "%");
			}
		});

		numberOfRoutesTextField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("\\d{0,2}?")) {
					numberOfRoutesTextField.setText(oldValue);
				}

				windowOptionsValidatdion.CheckNumberOfRoutes(numberOfRoutesTextField);
				setStartSolutionButtonState();
			}
		});

		numberOfGenerationsTextField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("\\d{0,7}?")) {
					numberOfGenerationsTextField.setText(oldValue);
				}

				windowOptionsValidatdion.CheckNumberOfGeneration(numberOfGenerationsTextField);
				setStartSolutionButtonState();
			}
		});

		stopAfterSameBestTextField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("\\d{0,7}?")) {
					stopAfterSameBestTextField.setText(oldValue);
				}

				windowOptionsValidatdion.checkNumberOfSameRoutes(stopAfterSameBestTextField, stopAfterSameBestCheckBox);
				setStartSolutionButtonState();
			}
		});

		stopAfterSameBestCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) {

				windowOptionsValidatdion.checkNumberOfSameRoutes(stopAfterSameBestTextField, stopAfterSameBestCheckBox);
				setStartSolutionButtonState();
			}
		});

		selectOptionsPane.setId("selectOptionsPane");
		selectOptionsPane.setHgap(30);
		selectOptionsPane.setVgap(15);
		selectOptionsPane.add(numberOfRoutesLabel, 0, 0);
		selectOptionsPane.add(numberOfRoutesTextField, 1, 0);
		selectOptionsPane.add(numberOfRoutesInfoLabel, 2, 0);
		selectOptionsPane.add(selectionTypeLabel, 0, 1);
		selectOptionsPane.add(selectionTypeChoiceBox, 1, 1);
		selectOptionsPane.add(crossOverTypeLabel, 0, 2);
		selectOptionsPane.add(crossoverTypeChoiceBox, 1, 2);
		selectOptionsPane.add(crossOverRateLabel, 0, 3);
		selectOptionsPane.add(crossoverRateSlider, 1, 3);
		selectOptionsPane.add(crossoverRateValueLabel, 2, 3);
		selectOptionsPane.add(mutationTypeLabel, 0, 4);
		selectOptionsPane.add(mutationTypeChoiceBox, 1, 4);
		selectOptionsPane.add(mutationRateLabel, 0, 5);
		selectOptionsPane.add(mutationRateSlider, 1, 5);
		selectOptionsPane.add(mutationRateValueLabel, 2, 5);
		selectOptionsPane.add(numberOfGenerationsLabel, 0, 6);
		selectOptionsPane.add(numberOfGenerationsTextField, 1, 6);
		selectOptionsPane.add(stopAfterSameBestCheckBox, 0, 7);
		selectOptionsPane.add(stopAfterSameBestTextField, 1, 7);

	}

	private void setSolutionButtonsPane() {
		solutionButtonsPane.setId("solutionButtonsPane");
		startStopSolutionButton.setDisable(true);
		startStopSolutionButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				try {
					if (isSolutionRunning) {
						currentPopulation.stopAlgorithm();
					} else {
						startSolution();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		showBestRouteButton.setDisable(true);
		showBestRouteButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				InfoWindow bestRouteWindow = new InfoWindow();
				bestRouteWindow.displayBestRoute(currentSolutionStats.getBestRoute(), currentSolutionStats.getBestLenght());

			}
		});
		showStatisticsButton.setDisable(true);
		showStatisticsButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				showStatistics();

			}
		});
		
		currentBestTextField.setEditable(false);
		solutionButtonsPane.getChildren().addAll(startStopSolutionButton, showStatisticsButton, showBestRouteButton,
				currentBestTextField);
	}

	private void setProgressBarPane() {
		algorithmProgressBar.setId("progressBar");
		algorithmProgressBar.setProgress(0.0);

		progressBarPane.setId("progressBarPane");
		progressBarPane.getChildren().add(algorithmProgressBar);
	}

	private void setRootPane() {
		root.setId("root");
		root.setPadding(new Insets(10));
		root.getChildren().addAll(fileBarPane, selectOptionsPane, solutionButtonsPane, progressBarPane);
	}

	private void openFile() {
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("TEXT files (*.txt)", "*.txt");
		fileChooser.getExtensionFilters().add(extensionFilter);
		File selectedFile = fileChooser.showOpenDialog(null);

		if (selectedFile != null) {
			String fileAdress = selectedFile.getAbsolutePath();
			if (windowOptionsValidatdion.checkFile(selectedFile.getPath(), fileLines)) {
				setFileAdressTextField(fileAdress);
				setProblemSizeLabelText(fileLines.get(0));
				setStartSolutionButtonState();
			} else {
				setFileAdressTextField("");
				setProblemSizeLabelText("No file selected");
			}
		}
		setStartSolutionButtonState();
	}

	private void setFileAdressTextField(String filePath) {
		fileAdressTextField.setText(filePath);
	}

	private void setProblemSizeLabelText(String problemSize) {
		problemSizeLabel.setText("Problem size: " + problemSize);
	}

	private void setStartSolutionButtonState() {
		if (windowOptionsValidatdion.isSolutionReadyToStart())
			startStopSolutionButton.setDisable(false);
		else
			startStopSolutionButton.setDisable(true);
	}

	private void startSolution() throws Exception {

		int[][] distancesArray = windowOptionsValidatdion.getDistancesArray();
		int problemSize = Integer.parseInt(fileLines.get(0));
		int numberOfRoutes = Integer.parseInt(numberOfRoutesTextField.getText());
		SelectionTypes selectionType = (SelectionTypes) selectionTypeChoiceBox.getValue();
		CrossoverTypes crossoverType = (CrossoverTypes) crossoverTypeChoiceBox.getValue();
		MutationTypes mutationType = (MutationTypes) mutationTypeChoiceBox.getValue();
		double crossoverRate = crossoverRateSlider.getValue();
		double mutationRate = mutationRateSlider.getValue();
		int numberOfGenerations = Integer.parseInt(numberOfGenerationsTextField.getText());
		boolean stopAfterSameBestRoutes = stopAfterSameBestCheckBox.isSelected();
		int stopAfterSameBestRoutesNumber;
		
		if (!stopAfterSameBestTextField.getText().equals("")) {
			stopAfterSameBestRoutesNumber = Integer.parseInt(stopAfterSameBestTextField.getText());
		}	else {
			stopAfterSameBestRoutesNumber = 0;
		}

		currentSolutionInfo = new SolutionInfo.SolutionInfoBuilder(distancesArray, problemSize)
				.setNumberOfRoutes(numberOfRoutes).setNumberOfGeneration(numberOfGenerations)
				.setSelectionType(selectionType).setCrossoverType(crossoverType).setMutationType(mutationType)
				.setCrossoverRate(crossoverRate).setMutationRate(mutationRate)
				.setStopAfterSameRoutes(stopAfterSameBestRoutes)
				.setStopAfterSameRoutesNumber(stopAfterSameBestRoutesNumber).build();

		currentSolutionStats = new SolutionStats(currentSolutionInfo, this);
		currentPopulation = new Population(currentSolutionInfo, currentSolutionStats, this);

		Thread executionThread = new Thread(currentPopulation);
		algorithmProgressBar.progressProperty().bind(currentPopulation.progressProperty());
		executionThread.setDaemon(true);
		setIsSolutionRunning(true);
		startStopSolutionButton.setText("Stop Solution");
		setSolutionButtonsDisabled(true);
		executionThread.start();
	}

	public void update(boolean wasSolutionStopped) {

		if (!wasSolutionStopped) {
			currentBestTextField.setText(String.valueOf(currentSolutionStats.getBestLenght()));
			setSolutionButtonsDisabled(false);
		}

		setIsSolutionRunning(false);
		startStopSolutionButton.setText("Start Solution");
	}

	private void setSolutionButtonsDisabled(boolean state) {

		showBestRouteButton.setDisable(state);
		showStatisticsButton.setDisable(state);
	}

	private void showStatistics() {
		StatisticsWindow statisticsScene = new StatisticsWindow(this);
		primaryStage.setScene(statisticsScene.getStatisticsScene());
	}

	private void setIsSolutionRunning(boolean state) {
		isSolutionRunning = state;
	}

	public void setTextStartStopSolutionButton() {
		if (isSolutionRunning) {
			startStopSolutionButton.setText("Stop Solution");
		} else {
			startStopSolutionButton.setText("Start Solution");
		}
	}

	public SolutionStats getSolutionStats() {
		return currentSolutionStats;
	}

	public SolutionInfo getSolutionInfo() {
		return currentSolutionInfo;
	}

	public Scene getMainScene() {
		return this.mainScene;
	}
}
