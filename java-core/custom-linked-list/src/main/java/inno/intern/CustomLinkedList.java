package inno.intern;

public class CustomLinkedList<T>{

    private static class Node<T>{
        T data;
        Node<T> next;

        Node(T data, Node<T> next){
            this.data = data;
            this.next = next;
        }

        Node(T data){
            this.data = data;
            this.next = null;
        }
    }

    private Node<T> head = null;
    private Node<T> tail = null;
    private int size = 0;

    public int size(){
        return size;
    }

    public void addFirst(T element){
        Node<T> newNode = new Node<>(element, head);
        head = newNode;
        if(tail == null){
            tail = newNode;
        }
        size++;
    }

    public void addLast(T element){
        Node<T> newNode = new Node<>(element);
        if(head == null){
            head = newNode;
        }
        else{
            tail.next = newNode;
        }
        tail = newNode;
        size++;
    }

    public void add(int index, T element){
        if (index < 0  || index > size){
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        if(index == 0){
            addFirst(element);
            return;
        }

        if(index == size){
            addLast(element);
            return;
        }

        Node<T> prev = head;
        for(int i = 0; i < index - 1; i++){
            prev = prev.next;
        }
        Node<T> newNode = new Node<>(element, prev.next);
        prev.next = newNode;
        size++;
    }

    public T getFirst(){
        if(head == null){
            return null;
        }
        return head.data;
    }

    public T getLast(){
        if(tail == null){
            return null;
        }
        return tail.data;
    }

    public T get(int index){
        if(index < 0 ||  index >= size){
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        Node<T> temp = head;
        for(int i = 0; i < index; i++){
            temp = temp.next;
        }
        return temp.data;
    }

    public T removeFirst(){
        if(head == null){
            return null;
        }

        T data = head.data;
        head = head.next;
        size--;

        if (size == 0) {
            tail = null;
        }
        return data;
    }

    public T removeLast(){
        if(head == null){
            return null;
        }

        T data = tail.data;

        if (size == 1) {
            head = null;
            tail = null;
        } else {
            Node<T> current = head;
            while(current.next != tail){
                current = current.next;
            }
            current.next = null;
            tail = current;
        }
        size--;
        return data;
    }

    public T remove(int index){
        if(index < 0 || index >= size){
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        if(index == 0){
            return removeFirst();
        }

        if(index == size - 1){
            return removeLast();
        }

        Node<T> prev = head;
        for(int i = 0; i < index - 1; i++){
            prev = prev.next;
        }

        T data = prev.next.data;

        prev.next = prev.next.next;
        size--;
        return data;
    }
}