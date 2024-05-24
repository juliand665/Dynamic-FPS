package dynamic_fps.impl.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Allows transforming a value into steps for e.g. a UI slider and back.
 * The transformer can handle different value ranges having different step sizes.
 *
 * @author Lilly Rose Berner, Pixaurora
 */
public class VariableStepTransformer {
	private boolean unsorted;
	private final List<Step> steps;

	public VariableStepTransformer() {
		this.steps = new ArrayList<>();
	}

	/**
	 * Add a new transformation step.
	 *
	 * @param change The current step size.
	 * @param max The maximum value using this step size.
	 */
	public void addStep(int change, int max) {
		this.unsorted = true;
		this.steps.add(new Step(change, max));
	}

	/**
	 * Convert a value to its corresponding step.
	 */
	public int toStep(int value) {
		if (this.unsorted) {
			this.sortSteps();
		}

		int step = 0;

		int currentChange = 0;
		int currentValue = value;

		for (Step pair : this.steps.reversed()) {
			if (currentValue > pair.max && currentChange != 0) {
				step += Math.floorDiv(currentValue - pair.max, currentChange);
				currentValue = pair.max;
			}

			currentChange = pair.change;
		}

		step += Math.floorDiv(currentValue, currentChange);

		return step;
	}

	/**
	 * Convert a step to its corresponding value.
	 */
	public int toValue(int step) {
		if (this.unsorted) {
			this.sortSteps();
		}

		int value = 0;
		int currentStep = 0;

		for (Step pair : this.steps) {
			int stepsTaken = Math.min(Math.floorDiv((pair.max - value), pair.change), step - currentStep);

			value += stepsTaken * pair.change;
			currentStep += stepsTaken;
		}

		return value;
	}

	private void sortSteps() {
		this.unsorted = false;

		this.steps.sort(new Comparator<Step>() {
			@Override
			public int compare(Step self, Step other) {
				return Integer.compare(self.max, other.max);
			}
		});
	}

	private record Step(int change, int max) {}
}
