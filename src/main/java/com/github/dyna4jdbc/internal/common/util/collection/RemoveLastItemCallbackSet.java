package com.github.dyna4jdbc.internal.common.util.collection;

import java.util.Collection;
import java.util.Set;

public final class RemoveLastItemCallbackSet<E> extends SetProxy<E> {

    private final Runnable onLastItemRemovedCallback;

    public RemoveLastItemCallbackSet(Set<E> delegate, Runnable runnable) {
        super(delegate);
        this.onLastItemRemovedCallback = runnable;
    }
    
    @Override
    public boolean remove(Object o) {
        boolean returnValue = super.remove(o);
        if (isEmpty()) {
            onLastItemRemovedCallback.run();
        }
        return returnValue;
    }
    
    public boolean retainAll(Collection<?> c) {
        boolean returnValue = super.retainAll(c);
        if (isEmpty()) {
            onLastItemRemovedCallback.run();
        }
        return returnValue;
    }

    public boolean removeAll(Collection<?> c) {
        boolean returnValue = super.removeAll(c);
        if (isEmpty()) {
            onLastItemRemovedCallback.run();
        }
        return returnValue;
    } 
}
