package inno.intern;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CustomLinkedListTest {
    private CustomLinkedList<Integer> customLinkedList;

    @BeforeEach
    public void setUp() {
        customLinkedList = new CustomLinkedList<>();
    }

    @Test
    void sizeIsZero(){
        assertEquals(0, customLinkedList.size());
    }

    @Test
    void addFirst(){
        customLinkedList.addFirst(1);
        assertEquals(1, customLinkedList.size());
    }

    @Test
    void getFirst() {
        customLinkedList.addFirst(1);
        assertEquals(1, customLinkedList.getFirst());
    }

    @Test
    void addLast(){
        customLinkedList.addLast(1);
        assertEquals(1, customLinkedList.size());
    }

    @Test
    void getLast() {
        customLinkedList.addLast(1);
        assertEquals(1, customLinkedList.getLast());
    }

    @Test
    void addAtIndex(){
        customLinkedList.addLast(1);
        customLinkedList.addLast(2);
        customLinkedList.addLast(3);
        assertEquals(3, customLinkedList.size());
        customLinkedList.add(1, 4);
        assertEquals(4, customLinkedList.size());
        assertEquals(4, customLinkedList.get(1));
    }

    @Test
    void getAtIndex() {
        customLinkedList.addLast(1);
        customLinkedList.addLast(2);
        customLinkedList.addLast(3);
        customLinkedList.addLast(4);
        assertEquals(4, customLinkedList.size());
        assertEquals(4, customLinkedList.get(3));
    }

    @Test
    void removeFirst() {
        customLinkedList.addLast(1);
        customLinkedList.addLast(2);
        customLinkedList.addLast(3);
        assertEquals(3, customLinkedList.size());
        assertEquals(1, customLinkedList.removeFirst());
        assertEquals(2, customLinkedList.size());
        assertEquals(2, customLinkedList.getFirst());
    }

    @Test
    void removeLast() {
        customLinkedList.addLast(1);
        customLinkedList.addLast(2);
        customLinkedList.addLast(3);
        assertEquals(3, customLinkedList.size());
        assertEquals(3, customLinkedList.removeLast());
        assertEquals(2, customLinkedList.size());
        assertEquals(2, customLinkedList.getLast());
    }

    @Test
    void removeAtIndex() {
        customLinkedList.addLast(1);
        customLinkedList.addLast(2);
        customLinkedList.addLast(3);
        assertEquals(3, customLinkedList.size());
        assertEquals(2, customLinkedList.remove(1));
        assertEquals(2, customLinkedList.size());
        assertEquals(3, customLinkedList.get(1));
    }

}