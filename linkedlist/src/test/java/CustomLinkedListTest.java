import org.example.util.CustomLinkedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CustomLinkedListTest {
    private CustomLinkedList<Integer> list;

    @BeforeEach
    void setUp() {
        list = new CustomLinkedList<>();
    }

    @Test
    void testAddFirst(){
        list.addFirst(123);
        assertEquals(list.getFirst(), 123);
        assertEquals(list.getLast(), 123);
        list.addFirst(124);
        assertEquals(list.getFirst(), 124);
        assertEquals(list.getLast(), 123);
        assertEquals(2, list.size());
    }

    @Test
    void testAddLast(){
        list.addLast(123);
        assertEquals(list.getFirst(), 123);
        assertEquals(list.getLast(), 123);
        list.addLast(124);
        assertEquals(list.getFirst(), 123);
        assertEquals(list.getLast(), 124);
        assertEquals(2, list.size());
    }

    @Test
    void testAddByIndex() {
        list.add(0, 123);
        list.add(1, 125);
        list.add(1, 124);
        assertEquals(123, list.get(0));
        assertEquals(124, list.get(1));
        assertEquals(125, list.get(2));
        assertEquals(3, list.size());
        list.add(3, 126);
        assertEquals(126, list.getLast());
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(5, 128));
        assertEquals(124, list.get(1));
    }

    @Test
    void testRemoveFirst(){
        list.addFirst(124);
        list.addFirst(123);
        list.removeFirst();
        assertEquals(1, list.size());
        assertEquals(124, list.getFirst());
        list.removeFirst();
        assertEquals(0, list.size());
        assertThrows(NoSuchElementException.class, list::removeFirst);
    }

    @Test
    void testRemoveLast(){
        list.addLast(124);
        list.addLast(123);
        list.removeLast();
        assertEquals(1, list.size());
        assertEquals(124, list.getLast());
        list.removeLast();
        assertEquals(0, list.size());
        assertThrows(NoSuchElementException.class, list::removeLast);
    }

    @Test
    void testRemoveByIndex(){
        list.addLast(123);
        list.addLast(124);
        list.addLast(125);
        list.addLast(126);
        list.remove(0);
        assertEquals(124, list.getFirst());
        list.remove(1);
        assertEquals(126, list.get(1));
        list.remove(1);
        assertEquals(124, list.getLast());
    }

    @Test
    void testEmptyListThrows() {
        assertThrows(NoSuchElementException.class, list::getFirst);
        assertThrows(NoSuchElementException.class, list::getLast);
        assertThrows(NoSuchElementException.class, list::removeFirst);
        assertThrows(NoSuchElementException.class, list::removeLast);
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(0));
    }
}
