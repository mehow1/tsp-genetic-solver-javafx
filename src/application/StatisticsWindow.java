package application;

import com.sun.javafx.scene.layout.region.Margins;

import algorithm.GenerationStats;
import algorithm.SolutionStats;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class StatisticsWindow {

	Main mainWindow;
	Pane root = new VBox();
	Pane topBarPane = new HBox(30);
	Pane spliterPane = new HBox(5);
	GridPane solutionParametersPane = new GridPane();
	Pane wrapperChartPane = new VBox();
	Pane chartPane = new VBox();
	
	NumberAxis lenghtAxis = new NumberAxis();
	NumberAxis generationAxis = new NumberAxis();

	XYChart.Series<Number, Number> chartBestLine = new XYChart.Series<>();
	XYChart.Series<Number, Number> chartAverageLine = new XYChart.Series<>();
	XYChart.Series<Number, Number> chartWorstLine = new XYChart.Series<>();

	LineChart<Number, Number> lineChart = new LineChart<Number, Number>(lenghtAxis, generationAxis);

	public StatisticsWindow(Main mainWindow) {
		this.mainWindow = mainWindow;

	}

	public Scene getStatisticsScene() {
		setTopBarPane();
		setSolutionParametersPane();
		setChartPane();
		wrapperChartPane.getChildren().add(chartPane);
		spliterPane.getChildren().addAll(solutionParametersPane, wrapperChartPane);
		root.getChildren().addAll(topBarPane, spliterPane);
		root.setPadding(new Insets(5));
		Scene statisticsScene = new Scene(root, 820, 500);
		statisticsScene.getStylesheets().add(getClass().getResource("statisticsWindow.css").toExternalForm());
		return statisticsScene;

	}

	private void setTopBarPane() {
		topBarPane.setId("topBarPane");
		Button backButton = new Button("Back");
		backButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				mainWindow.primaryStage.setScene(mainWindow.getMainScene());
				;
			}
		});
		
		Button showBestRouteButton = new Button("Show Best Route");
		showBestRouteButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				InfoWindow.displayBestRoute(mainWindow.getSolutionStats().getBestRoute(), mainWindow.getSolutionStats().getBestLenght());
				
			}
		});
		
		Label solutionStoppedAfterLabel = new Label("Solution finished after "
				+ mainWindow.getSolutionInfo().getStopAfterSameRoutesNumber() + " same best route at "
				+ mainWindow.getSolutionStats().getNumberOfTotalGenerations() + " generations");
		
		topBarPane.getChildren().addAll(backButton, showBestRouteButton);
		
		if (mainWindow.getSolutionInfo().getStopAfterSameRoutes() && mainWindow.getSolutionInfo()
				.getNumberOfGenerations() > mainWindow.getSolutionStats().getNumberOfTotalGenerations()) {
			topBarPane.getChildren().add(solutionStoppedAfterLabel);
		}
	}

	private void setSolutionParametersPane() {

		solutionParametersPane.setHgap(30);
		solutionParametersPane.setVgap(10);

		Label solutionParametersLabel = new Label("Solution Parameters");
		Label problemSizeLabel = new Label("Problem Size: ");
		Label problemSizeValueLabel = new Label("" + mainWindow.getSolutionInfo().getProblemSize());
		Label numberOfRoutesLabel = new Label("Number of routes: ");
		Label numberOfRoutesValueLabel = new Label("" + mainWindow.getSolutionInfo().getNumberOfRoutes());
		Label numberOfGenerationsLabel = new Label("Number of generations: ");
		Label numberOfGenerationsValueLabel = new Label("" + mainWindow.getSolutionInfo().getNumberOfGenerations());
		Label selectionTypeLabel = new Label("Selection Type: ");
		Label selectionTypeValueLabel = new Label("" + mainWindow.getSolutionInfo().getSelectionType());
		Label crossoverTypeLabel = new Label("Crossover Type");
		Label crossoverTypeValueLabel = new Label("" + mainWindow.getSolutionInfo().getCrossoverType());
		Label crossoverRateLabel = new Label("Crossover rate: ");
		Label crossovrRateValueLabel = new Label("" + Math.round(mainWindow.getSolutionInfo().getCrossoverRate()) + "%");
		Label mutationTypeLabel = new Label("Mutation Type: ");
		Label mutationTypeValueLabel = new Label("" + mainWindow.getSolutionInfo().getMutationType());
		Label mutationRateLabel = new Label("Mutation Rate: ");
		Label mutationRateValueLabel = new Label("" + Math.round(mainWindow.getSolutionInfo().getMutationRate()) + "%");
		
		solutionParametersPane.setMargin(solutionParametersLabel, new Insets(40,0,0,0));

		solutionParametersPane.add(solutionParametersLabel, 0, 0);
		solutionParametersPane.add(problemSizeLabel, 0, 1);
		solutionParametersPane.add(problemSizeValueLabel, 1, 1);
		solutionParametersPane.add(numberOfRoutesLabel, 0, 2);
		solutionParametersPane.add(numberOfRoutesValueLabel, 1, 2);
		solutionParametersPane.add(numberOfGenerationsLabel, 0, 3);
		solutionParametersPane.add(numberOfGenerationsValueLabel, 1, 3);
		solutionParametersPane.add(selectionTypeLabel, 0, 4);
		solutionParametersPane.add(selectionTypeValueLabel, 1, 4);
		solutionParametersPane.add(crossoverTypeLabel, 0, 5);
		solutionParametersPane.add(crossoverTypeValueLabel, 1, 5);
		solutionParametersPane.add(crossoverRateLabel, 0, 6);
		solutionParametersPane.add(crossovrRateValueLabel, 1, 6);
		solutionParametersPane.add(mutationTypeLabel, 0, 7);
		solutionParametersPane.add(mutationTypeValueLabel, 1, 7);
		solutionParametersPane.add(mutationRateLabel, 0, 8);
		solutionParametersPane.add(mutationRateValueLabel, 1, 8);

		solutionParametersPane.setPadding(new Insets(5));
	}

	private void setChartPane() {
		chartBestLine.setName("Best lenght");
		chartAverageLine.setName("Average lenght");
		chartWorstLine.setName("Worst lenght");
		chartPane.setId("chart");
		chartPane.getChildren().add(lineChart);
		getDataForChartLines();
		lenghtAxis.setLabel("Lenght");
		generationAxis.setLabel("Generation");
		lineChart.setTitle("Lenghs per generation");
		lineChart.getData().addAll(chartBestLine, chartAverageLine, chartWorstLine);
		lineChart.setCreateSymbols(false);
		chartPane.setPadding(new Insets(5));
	}


	private void getDataForChartLines() {

		int size = mainWindow.getSolutionStats().getNumberOfTotalGenerations();
		int point = size /20;
		int counter = 0;

		for (GenerationStats generationStats : mainWindow.getSolutionStats().getGenerationsStats()) {
			if (counter >= point || generationStats.getGenerationNumber() == 0 || generationStats.getGenerationNumber() == 
					mainWindow.getSolutionStats().getNumberOfTotalGenerations()-1) {
				chartBestLine.getData().add(new Data<Number, Number>(generationStats.getGenerationNumber(),
						generationStats.getBestLenght()));

				chartAverageLine.getData()
						.add(new Data<Number, Number>(Math.round(generationStats.getGenerationNumber()),
								Math.round(generationStats.getAverageLenght())));

				chartWorstLine.getData().add(new Data<Number, Number>(generationStats.getGenerationNumber(),
						generationStats.getWorstLenght()));

				counter = 0;
			}

			else {
				counter++;
			}
		}
	}

}
