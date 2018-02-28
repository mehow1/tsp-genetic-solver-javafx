package algorithm;

import java.util.ArrayList;

import application.Main;

public class SolutionStats {

	SolutionInfo solutionInfo;
	int bestLenght;
	ArrayList<GenerationStats> generationsStats;
	ArrayList<Integer> bestRoute;
	double solutionExecutionDuration;

	public SolutionStats(SolutionInfo solutionInfo, Main main) {

		this.solutionInfo = solutionInfo;
		bestLenght = Integer.MAX_VALUE;
		generationsStats = new ArrayList<>(solutionInfo.getNumberOfGenerations());

	}

	public void update(GenerationStats generationStats) {

			if (generationStats.getBestLenght() < bestLenght) {
				bestLenght = generationStats.getBestLenght();
				bestRoute = generationStats.getBestRoute();
			}

			generationsStats.add(generationStats);
	}

	public int getBestLenght() {
		return bestLenght;
	}
	
	public ArrayList<Integer> getBestRoute(){
		return bestRoute;
	}
	
	public int getNumberOfTotalGenerations() {
		return generationsStats.size();
	}
	
	public ArrayList<GenerationStats> getGenerationsStats(){
		return this.generationsStats;
	}

}
