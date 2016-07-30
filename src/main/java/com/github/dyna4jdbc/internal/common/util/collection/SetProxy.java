package com.github.dyna4jdbc.internal.common.util.collection;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class SetProxy<E> implements Set<E> {
    
    private final Set<E> delegate;
    
    public SetProxy(Set<E> delegate) {
        this.delegate = delegate;
    }
    
    //CHECKSTYLE.OFF: DesignForExtension
    public void forEach(Consumer<? super E> action) {
        delegate.forEach(action);
    }

    public int size() {
        return delegate.size();
    }

    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    public boolean contains(Object o) {
        return delegate.contains(o);
    }

    public Iterator<E> iterator() {
        return delegate.iterator();
    }

    public Object[] toArray() {
        return delegate.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return delegate.toArray(a);
    }

    public boolean add(E e) {
        return delegate.add(e);
    }

    public boolean remove(Object o) {
        return delegate.remove(o);
    }

    public boolean containsAll(Collection<?> c) {
        return delegate.containsAll(c);
    }

    public boolean addAll(Collection<? extends E> c) {
        return delegate.addAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return delegate.retainAll(c);
    }

    public boolean removeAll(Collection<?> c) {
        return delegate.removeAll(c);
    }

    public void clear() {
        delegate.clear();
    }

    public Spliterator<E> spliterator() {
        return delegate.spliterator();
    }

    public boolean removeIf(Predicate<? super E> filter) {
        return delegate.removeIf(filter);
    }

    public Stream<E> stream() {
        return delegate.stream();
    }

    public Stream<E> parallelStream() {
        return delegate.parallelStream();
    }
    
    //CHECKSTYLE.ON: DesignForExtension
}
