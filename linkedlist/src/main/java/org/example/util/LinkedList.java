package org.example.util;

public interface LinkedList<E> {
    int size();

    void addFirst(E el);

    void addLast(E el);

    void add(int index, E el);

    E getFirst();

    E getLast();

    E get(int index);

    E removeFirst();

    E removeLast();

    E remove(int index);
}
