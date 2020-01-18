package yuudaari.soulus.common.util.unused;

import java.util.Collection;
import java.util.Iterator;

public class MutableSingleton<T> implements Collection<T> {

	private T value = null;

	@Override
	public int size () {
		return value == null ? 0 : 1;
	}

	public boolean has () {
		return value != null;
	}

	public T get () {
		return value;
	}

	public boolean set (final T newValue) {
		if (value == null ? newValue == null : newValue.equals(value))
			return false;

		value = newValue;
		return true;
	}

	@Override
	public boolean add (final T newValue) {
		if (value == newValue)
			return false;

		if (value != null)
			throw new IndexOutOfBoundsException("Can't add more than one item to MutableSingleton");

		value = newValue;
		return true;
	}

	@Override
	public boolean remove (final Object removeValue) {
		if (removeValue == null)
			return false;

		if (!removeValue.equals(value))
			return false;

		value = null;
		return true;
	}

	@Override
	public void clear () {
		value = null;
	}

	@Override
	public boolean contains (final Object checkValue) {
		return checkValue == null ? value == null : checkValue.equals(value);
	}

	@Override
	public boolean isEmpty () {
		return value == null;
	}

	@Override
	public Object[] toArray () {
		if (value == null)
			return new Object[0];

		return new Object[] {
			value
		};
	}

	@Override
	@SuppressWarnings("unchecked")
	public <N> N[] toArray (N[] arr) {
		if (value == null)
			return arr;

		if (arr.length >= 1) {
			arr[0] = (N) value;
			return arr;
		}

		return (N[]) new Object[] {
			value
		};
	}

	@Override
	public Iterator<T> iterator () {
		return new Iterator<T>() {

			private boolean hasNext = value != null;

			@Override
			public boolean hasNext () {
				return hasNext;
			}

			@Override
			public T next () {
				if (!hasNext)
					return null;

				hasNext = false;
				return value;
			}
		};
	}

	@Override
	public boolean retainAll (final Collection<?> c) {
		if (value == null)
			return false;

		if (c.contains(value))
			return false;

		value = null;
		return true;
	}

	@Override
	public boolean removeAll (final Collection<?> c) {
		if (value == null)
			return false;

		if (!c.contains(value))
			return false;

		value = null;
		return true;
	}

	@Override
	public boolean addAll (Collection<? extends T> c) {
		boolean modified = false;
		for (final T o : c)
			modified = add(o) || modified;

		return modified;
	}

	@Override
	public boolean containsAll (Collection<?> c) {
		if (value == null)
			return c.size() == 0;

		return c.size() == 1 && c.contains(value);
	}
}
