package algorithm;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import algorithmTypes.CrossoverTypes;
import algorithmTypes.MutationTypes;
import javafx.scene.Parent;

public class Route implements Comparable<Route> {

	SolutionInfo solutionInfo;
	ArrayList<Integer> citiesOrder = new ArrayList<>();
	int lenght;

	public Route(Route oldRoute, SolutionInfo solutionInfo) {
		this.solutionInfo = solutionInfo;
		this.citiesOrder = new ArrayList<>(oldRoute.citiesOrder);
		this.lenght = oldRoute.lenght;
	}

	public Route(ArrayList<Integer> tempArray, SolutionInfo solutionInfo) {
		this.solutionInfo = solutionInfo;
		Collections.shuffle(tempArray);
		this.citiesOrder = new ArrayList<>(tempArray);
		this.calculateLenght();
	}

	public int getLenght() {
		return this.lenght;
	}

	@Override
	public String toString() {
		return "" + citiesOrder + "";
	}

	private void calculateLenght() {
		int finalLenght = 0;
		int[][] distances = solutionInfo.getFileDistances();

		for (int a = 0; a < solutionInfo.getProblemSize(); a++) {
			if (a == solutionInfo.getProblemSize() - 1)
				finalLenght += distances[this.citiesOrder.get(a)][this.citiesOrder.get(0)];
			else
				finalLenght += distances[this.citiesOrder.get(a)][this.citiesOrder.get(a + 1)];
		}

		this.lenght = finalLenght;
	}

	public void crossing(Route partner) {

		if (solutionInfo.getCrossoverType() == CrossoverTypes.PARTIALLY_MAPPED_CROSSOVER) {
			pmxCross(partner);

		} else if (solutionInfo.getCrossoverType() == CrossoverTypes.ORDERED_CROSSOVER) {
			oxCross(partner);

		} else {
			cxCross(partner);
		}

	}

	private void pmxCross(Route partner) {

		int size = solutionInfo.getProblemSize();
		Random rand = new Random();
		int firstIndex = 0;
		int secondIndex = 0;

		while (firstIndex == secondIndex || (firstIndex - secondIndex < 3 && firstIndex - secondIndex > -3)) {
			firstIndex = rand.nextInt(size);
			secondIndex = rand.nextInt(size);
		}

		int crossStartIndex = Math.min(firstIndex, secondIndex);
		int crossEndIndex = Math.max(firstIndex, secondIndex);

		for (int index = crossStartIndex; index <= crossEndIndex; index++) {
			int temp = this.citiesOrder.get(index);
			this.citiesOrder.set(index, partner.citiesOrder.get(index));
			partner.citiesOrder.set(index, temp);
		}

		ArrayList<Integer> citiesOrderCopyThis = new ArrayList<>(this.citiesOrder);
		ArrayList<Integer> citiesOrderCopyPartner = new ArrayList<>(partner.citiesOrder);

		int checkerThis = 0;
		int checkerPartner = 0;

		for (int index = 0; index < size; index++) {
			if (index < crossStartIndex || index > crossEndIndex) {
				checkerThis = checkIndex(index, this);

				while (checkerThis != index) {
					this.citiesOrder.set(index, citiesOrderCopyPartner.get(checkerThis));
					checkerThis = checkIndex(index, this);
				}

				checkerPartner = checkIndex(index, partner);
				while (checkerPartner != index) {
					partner.citiesOrder.set(index, citiesOrderCopyThis.get(checkerPartner));
					checkerPartner = checkIndex(index, partner);
				}
			}
		}

		this.calculateLenght();
		partner.calculateLenght();
	}

	private int checkIndex(int indexToCheck, Route routeToCheck) {
		for (int index = 0; index < solutionInfo.getProblemSize(); index++) {

			if ((routeToCheck.citiesOrder.get(index) == routeToCheck.citiesOrder.get(indexToCheck))
					&& index != indexToCheck) {

				return index;
			}
		}

		return indexToCheck;
	}

	private void oxCross(Route partner) {
		Random rand = new Random();
		int size = solutionInfo.getProblemSize();
		int firstIndex = rand.nextInt(size);
		int secondIndex = rand.nextInt(size);
		int crossStartIndex = Math.min(firstIndex, secondIndex);
		int crossEndIndex = Math.max(firstIndex, secondIndex);

		int firstChildCurrentIndex = 0;
		int secondChildCurrentIndex = 0;

		ArrayList<Integer> fixedPartThis = new ArrayList<>(
				this.citiesOrder.subList(crossStartIndex, crossEndIndex + 1));
		ArrayList<Integer> fixedPartPartner = new ArrayList<>(
				partner.citiesOrder.subList(crossStartIndex, crossEndIndex + 1));

		ArrayList<Integer> firstChildRoute = new ArrayList<>(size);
		ArrayList<Integer> secondChildRoute = new ArrayList<>(size);

		for (int index = 0; index < size; index++) {
			if (index < crossStartIndex || index > crossEndIndex) {
				while (fixedPartThis.contains(partner.citiesOrder.get(firstChildCurrentIndex))) {
					firstChildCurrentIndex++;
				}

				while (fixedPartPartner.contains(this.citiesOrder.get(secondChildCurrentIndex))) {
					secondChildCurrentIndex++;
				}

				firstChildRoute.add(partner.citiesOrder.get(firstChildCurrentIndex));
				secondChildRoute.add(this.citiesOrder.get(secondChildCurrentIndex));
				firstChildCurrentIndex++;
				secondChildCurrentIndex++;
			} else {

				firstChildRoute.add(this.citiesOrder.get(index));
				secondChildRoute.add(partner.citiesOrder.get(index));
			}
		}
		
		this.citiesOrder = firstChildRoute;
		partner.citiesOrder = secondChildRoute;

		this.calculateLenght();
		partner.calculateLenght();
	}

	private void cxCross(Route partner) {

		Random rand = new Random();
		int startIndex = rand.nextInt(solutionInfo.getProblemSize() - 1);
		int startValue = this.citiesOrder.get(startIndex);
		int currentValue = startValue;

		ArrayList<Integer> cycleIndexes = new ArrayList<>();
		cycleIndexes.add(startIndex);
		int currentIndex = 0;
		int nextValue = 0;
		int nextIndex = 0;

		while (currentValue != startValue || cycleIndexes.size() <= 1) {
			currentIndex = this.citiesOrder.indexOf(currentValue);
			nextValue = partner.citiesOrder.get(currentIndex);
			nextIndex = this.citiesOrder.indexOf(nextValue);
			currentValue = this.citiesOrder.get(nextIndex);
			cycleIndexes.add(nextIndex);
		}

		ArrayList<Integer> citiesOrderThisCopy = new ArrayList<>(this.citiesOrder);
		ArrayList<Integer> citiesOrderPartnerCopy = new ArrayList<>(partner.citiesOrder);

		for (int index = 0; index < solutionInfo.getProblemSize(); index++) {
			if (!cycleIndexes.contains(index)) {
				this.citiesOrder.set(index, citiesOrderPartnerCopy.get(index));
				partner.citiesOrder.set(index, citiesOrderThisCopy.get(index));
			}
		}

		this.calculateLenght();
		partner.calculateLenght();
	}

	public void mutate() {
		if (solutionInfo.getMutationType() == MutationTypes.SWAP)
			swapMutation();
		else
			inversionMutation();
	}

	private void inversionMutation() {
		Random rand = new Random();
		int firstindex = rand.nextInt(solutionInfo.getProblemSize() - 1);
		int secondIndex = rand.nextInt(solutionInfo.getProblemSize() - 1);
		int startIndex = Math.min(firstindex, secondIndex);
		int endIndex = Math.max(firstindex, secondIndex);
		Collections.reverse(this.citiesOrder.subList(startIndex, endIndex));
		this.calculateLenght();
	}

	private void swapMutation() {
		Random rand = new Random();
		int firstIndex = 0;
		int secondIndex = 0;
		while (firstIndex == secondIndex) {
			firstIndex = rand.nextInt(solutionInfo.getProblemSize() - 1);
			secondIndex = rand.nextInt(solutionInfo.getProblemSize() - 1);
		}

		Collections.swap(this.citiesOrder, firstIndex, secondIndex);
		this.calculateLenght();
	}
	
	
	@Override
	public int compareTo(Route otherRoute) {

		if (this.lenght < otherRoute.lenght)
			return -1;
		if (this.lenght > otherRoute.lenght)
			return 1;
		return 0;
	}

}
