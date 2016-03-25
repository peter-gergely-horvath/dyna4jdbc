package com.github.dyna4jdbc.internal.common.util.collection;

import java.util.Iterator;
import java.util.List;

public class ListIndexIterable<E> implements Iterable<E> {

	private final List<List<E>> delegate;
	private final int index;


	public ListIndexIterable(List<List<E>> delegate, int index) {
		this.delegate = delegate;
		this.index = index;
	}
	
	
	@Override
	public Iterator<E> iterator() {
		
		Iterator<List<E>> iterator = delegate.iterator();
		
		
		return new Iterator<E>() {

			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			@Override
			public E next() {
				List<E> nextList = iterator.next();
				E resultObject = null;
				
				if(index < nextList.size()) {
					resultObject = nextList.get(index);
				}
				
				return resultObject;
			}
		};
	}

}
