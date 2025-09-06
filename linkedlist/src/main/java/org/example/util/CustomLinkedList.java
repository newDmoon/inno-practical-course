package org.example.util;

import java.util.NoSuchElementException;

/**
 * Doubly-linked list implementation of the LinkedList interface.
 * Implements custom operations, and permits all
 * elements (including null).
 *
 * <p>All of the operations perform as could be expected for a doubly-linked
 * list.  Operations that index into the list will traverse the list from
 * the beginning or the end, whichever is closer to the specified index.
 *
 * <p><strong>Note that this implementation is not synchronized.</strong>
 *
 * @param <E> the type of elements held in this collection
 * @author Novogrodsky Dmitry
 * @see LinkedList
 */
public class CustomLinkedList<E> implements LinkedList<E> {
    private int size = 0;
    /**
     * Pointer to the first node.
     */
    private Node<E> first;
    /**
     * Pointer to the last node.
     */
    private Node<E> last;

    /**
     * Constructs an empty list.
     */
    public CustomLinkedList() {
    }

    /**
     * Returns the size of the list.
     *
     * @return the size of the list
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Adds the element in the beginning of the list.
     *
     * @param el the element to add
     */
    @Override
    public void addFirst(E el) {
        final Node<E> firstNode = first;
        final Node<E> newNode = new Node<>(null, el, firstNode);
        first = newNode;
        if (firstNode == null) {
            last = newNode;
        } else {
            firstNode.prev = newNode;
        }
        size++;
    }

    /**
     * Adds the element in the end of the list.
     *
     * @param el the element to add
     */
    @Override
    public void addLast(E el) {
        final Node<E> lastNode = last;
        final Node<E> newNode = new Node<>(lastNode, el, null);
        last = newNode;
        if (lastNode == null) {
            first = newNode;
        } else {
            lastNode.next = newNode;
        }
        size++;
    }

    /**
     * Adds the element in the list by index.
     *
     * @param index index of the list to add
     * @param el element to add
     * @throws IndexOutOfBoundsException if index is out of bounds
     */
    @Override
    public void add(int index, E el) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException(index);
        }

        if (index == 0){
            addFirst(el);
        } else if (index == size) {
            addLast(el);
        } else {
            final Node<E> insertionNode = findNode(index);
            final Node<E> prev = insertionNode.prev;
            final Node<E> newNode = new Node<>(prev, el, insertionNode);
            prev.next = newNode;
            insertionNode.prev = newNode;
            size++;
        }
    }

    /**
     * Returns the first element of the list.
     *
     * @return the first element of the list
     * @throws NoSuchElementException if this list is empty
     */
    @Override
    public E getFirst() {
        final Node<E> firstNode = first;
        if (firstNode == null) {
            throw new NoSuchElementException();
        }
        return firstNode.item;
    }

    /**
     * Returns the last element of the list.
     *
     * @return the last element of the list
     * @throws NoSuchElementException if this list is empty
     */
    @Override
    public E getLast() {
        final Node<E> lastNode = last;
        if (lastNode == null) {
            throw new NoSuchElementException();
        }
        return lastNode.item;
    }

    /**
     * Returns the element by index in this list.
     *
     * @param index index of the element to get
     * @return the element by index in this list
     * @throws IndexOutOfBoundsException if index is out of bounds
     */
    @Override
    public E get(int index) {
        if (!isIndexExists(index)) {
            throw new IndexOutOfBoundsException(index);
        }
        return findNode(index).item;
    }

    /**
     * Retrieve and remove the first element of the list.
     *
     * @return removed element
     * @throws NoSuchElementException if this list is empty
     */
    @Override
    public E removeFirst() {
        final Node<E> firstNode = first;
        if (firstNode == null) {
            throw new NoSuchElementException();
        }
        return unlinkFirst(firstNode);
    }

    /**
     * Retrieve and remove the last element of the list.
     *
     * @return removed element
     * @throws NoSuchElementException if this list is empty
     */
    @Override
    public E removeLast() {
        final Node<E> lastNode = last;
        if (lastNode == null) {
            throw new NoSuchElementException();
        }
        return unlinkLast(lastNode);
    }

    /**
     * Retrieve and remove the element of the list by index.
     *
     * @param index index of the element to remove
     * @return removed element
     * @throws IndexOutOfBoundsException if index is out of bounds
     */
    @Override
    public E remove(int index) {
        if (!isIndexExists(index)) {
            throw new IndexOutOfBoundsException(index);
        }

        Node<E> nodeToRemove = findNode(index);
        if (nodeToRemove == first) {
            return unlinkFirst(first);
        } else if (nodeToRemove == last) {
            return unlinkLast(last);
        } else {
            final var prev = nodeToRemove.prev;
            final E removedElement = nodeToRemove.item;
            final var next = nodeToRemove.next;
            prev.next = next;
            next.prev = prev;
            nodeToRemove.item = null;
            nodeToRemove.prev = null;
            nodeToRemove.next = null;
            size--;
            return removedElement;
        }
    }

    private Node<E> findNode(int index) {
        if (index < size / 2) {
            Node<E> iteratorNode = first;
            for (int i = 0; i < index; i++) {
                iteratorNode = iteratorNode.next;
            }
            return iteratorNode;
        } else {
            Node<E> iteratorNode = last;
            for (int i = size - 1; i > index; i--) {
                iteratorNode = iteratorNode.prev;
            }
            return iteratorNode;
        }
    }

    private boolean isIndexExists(int index) {
        return index >= 0 && index < size;
    }

    /**
     * Unlinks the first node and updates pointers.
     */
    private E unlinkFirst(Node<E> first) {
        final E removedElement = first.item;
        final Node<E> nextNode = first.next;
        first.item = null;
        first.next = null;
        this.first = nextNode;
        if (nextNode == null) {
            last = null;
        } else {
            nextNode.prev = null;
        }
        size--;
        return removedElement;
    }

    /**
     * Unlinks the last node and updates pointers.
     */
    private E unlinkLast(Node<E> last) {
        final E removedElement = last.item;
        final Node<E> prevNode = last.prev;
        last.item = null;
        last.prev = null;
        this.last = prevNode;
        if (this.last == null) {
            first = null;
        } else {
            this.last.next = null;
        }
        size--;
        return removedElement;
    }

    /**
     * Doubly-linked node structure used internally by the list.
     */
    private static class Node<E> {
        E item;
        Node<E> next;
        Node<E> prev;

        public Node(Node<E> prev, E item, Node<E> next) {
            this.item = item;
            this.next = next;
            this.prev = prev;
        }
    }
}
