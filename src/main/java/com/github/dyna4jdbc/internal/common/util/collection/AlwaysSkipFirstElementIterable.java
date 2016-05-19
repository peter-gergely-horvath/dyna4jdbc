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

public class AlwaysSkipFirstElementIterable<E> implements Iterable<E> {

	private final Iterable<E> delegate;

	public AlwaysSkipFirstElementIterable(Iterable<E> iterable) {
        if(iterable == null) throw new NullPointerException("argument iterable cannot be null");
		this.delegate = iterable;
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
