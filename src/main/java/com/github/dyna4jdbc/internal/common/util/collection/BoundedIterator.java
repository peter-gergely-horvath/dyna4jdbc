/*
 * Copyright (c) 2016, 2017 Peter G. Horvath, All Rights Reserved.
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
import java.util.NoSuchElementException;

/**
 * @author Peter G. Horvath
 */
public final class BoundedIterator<T> implements Iterator<T> {

    private final Iterator<T> delegate;
    private final int maxRows;
    private int currentRow = 0;

    public BoundedIterator(Iterator<T> delegate, int maxRows) {
        this.delegate = delegate;
        this.maxRows = maxRows;
    }

    @Override
    public boolean hasNext() {
        return currentRow < maxRows
                && delegate.hasNext();
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        ++currentRow;


        return delegate.next();
    }

    public Iterator<T> getDelegate() {
        return delegate;
    }
}
