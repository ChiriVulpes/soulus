package yuudaari.soulus.common.util;

import java.util.Random;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;

@Serializable
public class Range {

	@Serialized public Double min;
	@Serialized public Double max;

	public Range () {
	}

	public Range (Number min, Number max) {
		this.min = min.doubleValue();
		this.max = max.doubleValue();
	}

	public double get (Random random) {
		return random.nextDouble() * (max - min) + min;
	}

	public int getInt (Random random) {
		return (int) Math.floor(get(random));
	}

	public double get (double percent) {
		return min + (max - min) * percent;
	}

	public int getInt (double percent) {
		return (int) Math.floor(get(percent));
	}

	@Override
	public boolean equals (final Object obj) {
		if (!(obj instanceof Range)) return false;
		final Range compare = (Range) obj;
		return Math.abs(min - compare.min) < Math.ulp(1.0) && Math.abs(max - compare.max) < Math.ulp(1.0);
	}

	@Override
	public String toString () {
		return "{Range: " + min + " - " + max + "}";
	}
}
