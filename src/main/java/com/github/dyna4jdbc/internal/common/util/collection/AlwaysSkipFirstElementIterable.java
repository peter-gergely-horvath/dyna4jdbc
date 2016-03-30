package com.github.dyna4jdbc.internal.common.util.collection;

import java.util.Iterator;

public class AlwaysSkipFirstElementIterable<E> implements Iterable<E> {

	private final Iterable<E> delegate;

	private AlwaysSkipFirstElementIterable(Iterable<E> iterable) {
		this.delegate = iterable;
	}

	public static <E> AlwaysSkipFirstElementIterable<E> newInstance(Iterable<E> delegate) {
		return new AlwaysSkipFirstElementIterable<E>(delegate);
	}

	public Iterator<E> iterator() {

		Iterator<E> delegateIterator = delegate.iterator();

		if (delegateIterator.hasNext()) {
			// skip first element
			delegateIterator.next();
		}

		return new Iterator<E>() {

			@Override
			public boolean hasNext() {
				return delegateIterator.hasNext();

			}

			@Override
			public E next() {
				return delegateIterator.next();
			}
		};
	}

}
