package algorithm;

import java.util.ArrayList;

public class GenerationStats {
	
	private int generationNumber;
	private int bestLenght;
	private int worstLenght;
	private double averageLenght;
	ArrayList<Integer> bestRoute;
	
	public GenerationStats(int generationNumber, int bestLenght, int worstLenght, double averageLenght,
			ArrayList<Integer> bestRoute) {
		
		this.bestRoute = bestRoute;
		this.generationNumber = generationNumber;
		this.bestLenght = bestLenght;
		this.worstLenght = worstLenght;
		this.averageLenght = averageLenght;
	}
	
	public int getGenerationNumber() {
		return this.generationNumber;
	}
	
	public int getBestLenght() {
		return this.bestLenght;
	}
	
	public int getWorstLenght() {
		return this.worstLenght;
	}
	
	public double getAverageLenght() {
		return this.averageLenght;
	}
	
	public ArrayList<Integer> getBestRoute(){
		return this.bestRoute;
	}
}
