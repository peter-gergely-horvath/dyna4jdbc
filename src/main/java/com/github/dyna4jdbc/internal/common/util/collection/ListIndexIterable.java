/*
 * Copyright (c) 2016 Peter G. Horvath, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.dyna4jdbc.internal.common.util.collection;

import java.util.Iterator;
import java.util.List;

public class ListIndexIterable<E> implements Iterable<E> {

	private final List<List<E>> delegate;
	private final int index;


	public ListIndexIterable(List<List<E>> delegate, int index) {
		if(delegate == null) throw new NullPointerException("argument delegate cannot be null");
        if(index < 0) throw new IllegalArgumentException("index cannot be negative");

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
