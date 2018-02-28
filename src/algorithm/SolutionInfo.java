package algorithm;

import algorithmTypes.*;

public class SolutionInfo {

	private int problemSize;
	private int[][] fileDistances;
	private	int numberOfRoutes;
	private	int numberOfGenerations;
	private	SelectionTypes selectionType;
	private CrossoverTypes crossoverType;
	private MutationTypes mutationType;
	private double crossoverRate;
	private double mutationRate;
	private	boolean stopAfterSameRoutes;
	private	int stopAfterSameRoutesNumber;
	private	int numberOfCrosses;
	private	int numberOfMutation;
	
	public int getProblemSize() {
		return this.problemSize;
	}
	
	public int[][] getFileDistances(){
		return this.fileDistances;
	}
	
	public int getNumberOfRoutes() {
		return this.numberOfRoutes;
	}
	
	public int getNumberOfGenerations() {
		return this.numberOfGenerations;
	}
	
	public SelectionTypes getSelectionType(){
		return this.selectionType;
	}
	
	public CrossoverTypes getCrossoverType(){
		return this.crossoverType;
	}
	
	public MutationTypes getMutationType() {
		return this.mutationType;
	}
	
	public double getCrossoverRate() {
		return this.crossoverRate;
	}
	
	public double getMutationRate() {
		return this.mutationRate;
	}
	
	public boolean getStopAfterSameRoutes() {
		return this.stopAfterSameRoutes;
	}
	
	public int getStopAfterSameRoutesNumber() {
		return this.stopAfterSameRoutesNumber;
	}
	
	public int getNumberOfCrosses() {
		return this.numberOfCrosses;
	}
	
	public int getNumberOfMutations() {
		return this.numberOfMutation;
	}
	
	
	private SolutionInfo(SolutionInfoBuilder solutionInfoBuilder) {
		this.fileDistances = solutionInfoBuilder.fileDistances;
		this.problemSize = solutionInfoBuilder.problemSize;
		this.numberOfRoutes = solutionInfoBuilder.numberOfRoutes;
		this.numberOfGenerations = solutionInfoBuilder.numberOfGenerations;
		this.selectionType = solutionInfoBuilder.selectionType;
		this.crossoverType = solutionInfoBuilder.crossoverType;
		this.mutationType = solutionInfoBuilder.mutationType;
		this.crossoverRate = solutionInfoBuilder.crossoverRate;
		this.mutationRate = solutionInfoBuilder.mutationRate;
		this.stopAfterSameRoutes = solutionInfoBuilder.stopAfterSameRoutes;
		this.stopAfterSameRoutesNumber = solutionInfoBuilder.stopAfterSameRoutesNumber;
		
		this.numberOfMutation = (int) (this.numberOfRoutes * this.mutationRate)/100;
		double numberOfCrossedRoutes = this.numberOfRoutes * this.crossoverRate/100;
		this.numberOfCrosses = (int) (numberOfCrossedRoutes/2);	
	}

	public static class SolutionInfoBuilder {

		int problemSize;
		int[][] fileDistances;
		int numberOfRoutes;
		int numberOfGenerations;
		SelectionTypes selectionType;
		CrossoverTypes crossoverType;
		MutationTypes mutationType;
		double crossoverRate;
		double mutationRate;
		boolean stopAfterSameRoutes;
		int stopAfterSameRoutesNumber;

		public SolutionInfoBuilder(int[][] fileDistances, int problemSize) {
			this.fileDistances = fileDistances;
			this.problemSize = problemSize;
		}

		public SolutionInfoBuilder setNumberOfRoutes(int numberOfRoutes) {
			this.numberOfRoutes = numberOfRoutes;
			return this;
		}

		public SolutionInfoBuilder setNumberOfGeneration(int numberOfGenerations) {
			this.numberOfGenerations = numberOfGenerations;
			return this;
		}

		public SolutionInfoBuilder setSelectionType(SelectionTypes selectionType) {
			this.selectionType = selectionType;
			return this;
		}

		public SolutionInfoBuilder setCrossoverType(CrossoverTypes crossoverType) {
			this.crossoverType = crossoverType;
			return this;
		}

		public SolutionInfoBuilder setMutationType(MutationTypes mutationType) {
			this.mutationType = mutationType;
			return this;
		}

		public SolutionInfoBuilder setCrossoverRate(double crossoverRate) {
			this.crossoverRate = crossoverRate;
			return this;
		}

		public SolutionInfoBuilder setMutationRate(double mutationRate) {
			this.mutationRate = mutationRate;
			return this;
		}

		public SolutionInfoBuilder setStopAfterSameRoutes(boolean stopAfterSameRoutes) {
			this.stopAfterSameRoutes = stopAfterSameRoutes;
			return this;
		}
		
		public SolutionInfoBuilder setStopAfterSameRoutesNumber(int stopAfterSameRoutesNumber) {
			this.stopAfterSameRoutesNumber = stopAfterSameRoutesNumber;
			return this;
		}
		
		public SolutionInfo build() {
			return new SolutionInfo(this);
		}

	}

}
