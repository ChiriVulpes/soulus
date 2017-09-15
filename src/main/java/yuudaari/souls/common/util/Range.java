package yuudaari.souls.common.util;

import java.util.Random;

public class Range <T> {

	private final T min;
	public T getMin () {
		return this.min;
	}

	private final T max;
	public T getMax () {
		return this.max;
	}


	public Range (T min, T max) {
		this.min = min;
		this.max = max;
	}

	@SuppressWarnings ("unchecked")
	public T get (Random random) {
		if (this.max instanceof Integer) {
			return (T) (Integer) (int) Math.floor(random.nextFloat() * ((Integer) this.max - (Integer) this.min) + (Integer) this.min);
		} else if (this.max instanceof Float) {
			return (T) (Float) (random.nextFloat() * ((Float) this.max - (Float) this.min) + (Float) this.min);
		} else if (this.max instanceof Double) {
			return (T) (Double) (random.nextDouble() * ((Double) this.max - (Double) this.min) + (Double) this.min);
		}
		throw new RuntimeException("Unsupported range type");
	}
}
