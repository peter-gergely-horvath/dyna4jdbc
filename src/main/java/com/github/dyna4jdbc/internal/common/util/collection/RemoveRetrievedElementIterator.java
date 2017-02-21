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

/**
 * @author Peter G. Horvath
 */
public final class RemoveRetrievedElementIterator<T> implements Iterator<T> {

    private final Iterator<T> delegate;

    public RemoveRetrievedElementIterator(Iterator<T> iterator) {
        if (iterator == null) {
            throw new NullPointerException("argument iterator cannot be null");
        }
        this.delegate = iterator;
    }

    @Override
    public boolean hasNext() {
        return delegate.hasNext();
    }

    @Override
    public T next() {
        T retrievedElement = delegate.next();

        delegate.remove();

        return retrievedElement;
    }
}
