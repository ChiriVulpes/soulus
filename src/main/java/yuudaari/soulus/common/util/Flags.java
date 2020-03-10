package yuudaari.soulus.common.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Flags<E extends Enum<E>> {

	@SuppressWarnings("unchecked")
	public static <E extends Enum<E>> Flags<E> get (final Class<E> clz, final int flags) {
		EnumSet<E> set = EnumSet.noneOf(clz);
		try {
			set = Arrays.stream((E[]) clz.getMethod("values").invoke(null))
				.filter(option -> (option.ordinal() & flags) == option.ordinal())
				.collect(Collectors.toCollection( () -> EnumSet.noneOf(clz)));
		} catch (final InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
			Logger.error(e);
		}
		return new Flags<E>(set);
	}

	@SuppressWarnings("unchecked")
	public static <E extends Enum<E>> Flags<E> get (final E... enums) {
		return new Flags<E>(enums.length == 0 ? null : Arrays.stream(enums).collect(Collectors.toCollection( () -> EnumSet.noneOf((Class<E>) enums[0].getClass()))));
	}

	private final EnumSet<E> set;

	private Flags (final EnumSet<E> set) {
		this.set = set;
	}

	public <R> Stream<R> map (final Function<E, R> mapper) {
		return set == null ? Stream.empty() : set.stream().map(mapper);
	}

	public void forEach (final Consumer<E> action) {
		if (set != null) set.forEach(action);
	}

	public boolean has (final E e) {
		return set != null && set.contains(e);
	}

	public EnumSet<E> toSet () {
		return set;
	}

	public int toInt () {
		return toFlags(set);
	}

	@SuppressWarnings("unchecked")
	public static <E extends Enum<E>> int toFlags (final E... enums) {
		return toFlags(Arrays.stream(enums));
	}

	@SuppressWarnings("unchecked")
	public static int toFlags (final EnumSet<?> set) {
		return set == null ? 0 : toFlags((Stream<Enum<?>>) set.stream());
	}

	public static int toFlags (final Stream<Enum<?>> enumStream) {
		return enumStream
			.map(Enum::ordinal)
			.reduce( (a, b) -> a | b)
			.orElse(0);
	}

}
