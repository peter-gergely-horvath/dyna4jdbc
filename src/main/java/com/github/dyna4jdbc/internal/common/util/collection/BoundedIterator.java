package com.github.dyna4jdbc.internal.common.util.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author Peter Horvath
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
