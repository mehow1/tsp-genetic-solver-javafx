package algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import algorithmTypes.SelectionTypes;
import application.Main;
import javafx.application.Platform;
import javafx.concurrent.Task;

public class Population extends Task {

	Main mainWindow;
	SolutionInfo solutionInfo;
	SolutionStats solutionStats;
	ArrayList<Route> routes;
	private int currentBestCounter;
	private AtomicBoolean stopTask = new AtomicBoolean(false);

	public Population(SolutionInfo solutionInfo, SolutionStats solutionStats, Main mainWindow) {

		this.mainWindow = mainWindow;
		this.solutionInfo = solutionInfo;
		this.solutionStats = solutionStats;
		this.routes = new ArrayList<>(solutionInfo.getNumberOfRoutes());
		currentBestCounter = 0;
	}

	private void generatePopulationRoutes() {

		ArrayList<Integer> tempCitiesList = new ArrayList<>(solutionInfo.getProblemSize());
		for (int city = 0; city < solutionInfo.getProblemSize(); city++) {
			tempCitiesList.add(city);
		}

		for (int routeIndex = 0; routeIndex < solutionInfo.getNumberOfRoutes(); routeIndex++) {
			Route tempRoute = new Route(tempCitiesList, solutionInfo);
			routes.add(tempRoute);
		}
	}

	private void crossingPhase() {

		int crossNumber = solutionInfo.getNumberOfCrosses();
		if (solutionInfo.getNumberOfRoutes() > 1) {
			for (int index = 0; index < crossNumber; index++)
				routes.get(index).crossing(routes.get(index + crossNumber));
		}
	}

	private void mutationPhase() {

		int mutationNumber = solutionInfo.getNumberOfMutations();
		Collections.shuffle(routes);
		for (int index = 0; index < mutationNumber; index++) {
			routes.get(index).mutate();
		}

	}

	private void selectionPhase() {

		if (solutionInfo.getSelectionType() == SelectionTypes.TOURNAMENT) {
			routes = tournamentSelection();

		} else if (solutionInfo.getSelectionType() == SelectionTypes.ROULETTE) {
			routes = rouletteSelection();

		} else {
			routes = linearRankingSelection();
		}
	}

	private ArrayList<Route> tournamentSelection() {

		ArrayList<Route> routesNewGeneration = new ArrayList<>();
		Random rand = new Random();

		for (int index = 0; index < solutionInfo.getNumberOfRoutes(); index++) {

			int firstIndex = 0, secondIndex = 0;
			while (firstIndex == secondIndex) {
				firstIndex = rand.nextInt(solutionInfo.getNumberOfRoutes());
				secondIndex = rand.nextInt(solutionInfo.getNumberOfRoutes());
			}

			if (this.routes.get(firstIndex).compareTo(this.routes.get(secondIndex)) <= 0)
				routesNewGeneration.add(new Route(this.routes.get(firstIndex), solutionInfo));
			else
				routesNewGeneration.add(new Route(this.routes.get(secondIndex), solutionInfo));
		}

		return routesNewGeneration;
	}

	private ArrayList<Route> rouletteSelection() {

		Random rand = new Random();
		Collections.sort(routes);
		ArrayList<Route> newGenerationRoutes = new ArrayList<>(solutionInfo.getNumberOfRoutes());
		ArrayList<Integer> fitRates = new ArrayList<>(solutionInfo.getProblemSize());
		setFitRatesForRouletteSelection(fitRates);

		int sumOfLenghts = 0;

		for (int index = 0; index < solutionInfo.getNumberOfRoutes(); index++)
			sumOfLenghts += fitRates.get(index);

		int number = 0;

		for (int a = 0; a < solutionInfo.getNumberOfRoutes(); a++) {
			int tempSumOfLenghts = sumOfLenghts;
			number = rand.nextInt(sumOfLenghts);
			for (int b = (solutionInfo.getNumberOfRoutes() - 1); b >= 0; b--) {
				if (tempSumOfLenghts - fitRates.get(b) < number || b == 0) {
					newGenerationRoutes.add(new Route(routes.get(b), solutionInfo));
					break;
				} else
					tempSumOfLenghts -= fitRates.get(b);
			}
		}

		return newGenerationRoutes;
	}

	private ArrayList<Route> linearRankingSelection() {

		Random rand = new Random();
		Collections.sort(routes, Collections.reverseOrder());
		List<Double> fitRates = new ArrayList<>(solutionInfo.getNumberOfRoutes());
		ArrayList<Route> newGenerationRoutes = new ArrayList<>(solutionInfo.getNumberOfRoutes());
		setFitRatesForLinearRankingSelection(fitRates);

		float number = 0, tempSumRates = 0;
		for (int a = 0; a < solutionInfo.getNumberOfRoutes(); a++) {
			number = rand.nextFloat();
			tempSumRates = 0;
			for (int b = 0; b < solutionInfo.getNumberOfRoutes(); b++) {
				if ((fitRates.get(b) + tempSumRates) > number || b == solutionInfo.getNumberOfRoutes() - 1) {
					newGenerationRoutes.add(new Route(routes.get(b), solutionInfo));
					break;
				} else
					tempSumRates += fitRates.get(b);
			}
		}

		return newGenerationRoutes;
	}

	private void setFitRatesForRouletteSelection(List<Integer> fitRates) {

		int maxLenght = routes.get(solutionInfo.getNumberOfRoutes() - 1).getLenght();

		int indexRate = 0;
		for (int index = 0; index < solutionInfo.getNumberOfRoutes(); index++) {
			indexRate = (maxLenght - routes.get(index).getLenght() + 1);
			fitRates.add(indexRate);
		}
	}

	private void setFitRatesForLinearRankingSelection(List<Double> fitRates) {

		double sumRank = 0;
		for (int index = 0; index < solutionInfo.getNumberOfRoutes(); index++)
			sumRank += (index + 1);

		for (int index = 0; index < solutionInfo.getNumberOfRoutes(); index++) {
			fitRates.add((index + 1) / sumRank);
		}
	}

	private void generationSummary(int generationNumber) {

		int generationBestLenght = routes.get(0).getLenght();
		double generationAverageLenght = 0;
		int generationWorstLenght = routes.get(0).getLenght();
		ArrayList<Integer> currentGenerationBestCitiesOrder = new ArrayList<>(solutionInfo.getProblemSize());

		for (Route route : routes) {

			generationAverageLenght += route.getLenght();

			if (route.getLenght() <= generationBestLenght) {

				generationBestLenght = route.getLenght();
				currentGenerationBestCitiesOrder = route.citiesOrder;
			}

			if (route.getLenght() >= generationWorstLenght) {
				generationWorstLenght = route.getLenght();
			}
		}
		

		generationAverageLenght = generationAverageLenght / solutionInfo.getNumberOfRoutes();
		GenerationStats generationStats = new GenerationStats(generationNumber, generationBestLenght,
				generationWorstLenght, generationAverageLenght, currentGenerationBestCitiesOrder);
		
		if(solutionStats.getBestLenght() == generationBestLenght) {
			currentBestCounter++;
		}	else {
			currentBestCounter = 0;
		}

		solutionStats.update(generationStats);

	}

	@Override
	protected Object call() throws Exception {
		generatePopulationRoutes();
		int generationCounter = 0;

		while (generationCounter < solutionInfo.getNumberOfGenerations() && !stopTask.get()) {
			if (stopTask.get()) {
				break;
			};
			selectionPhase();
			crossingPhase();
			mutationPhase();
			updateProgress(generationCounter, solutionInfo.getNumberOfGenerations() - 1);
			generationSummary(generationCounter);
			generationCounter++;
			if(currentBestCounter >= solutionInfo.getStopAfterSameRoutesNumber() && 
					solutionInfo.getStopAfterSameRoutes()) {
				updateProgress(100,100);
				break;
			}

		}
		
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				
				if (!stopTask.get()) {
					mainWindow.update(false);
				} else {
					mainWindow.update(true);
				}
				
			}
		});

		return null;
	}

	public void stopAlgorithm() {
		stopTask.set(true);
	}
	
}
