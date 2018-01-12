package yuudaari.soulus.common.util;

import java.util.Random;
import yuudaari.soulus.common.config_old.Serializer;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;

@Serializable
public class Range {

	@Serialized public Double min;
	@Serialized public Double max;

	public Range () {}

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

	public static final Serializer<Range> serializer = new Serializer<>(Range.class, "min", "max");

	@Override
	public String toString () {
		return "{Range: " + min + " - " + max + "}";
	}
}
