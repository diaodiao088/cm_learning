package core.assist.deque;

import java.util.Iterator;
import java.util.Queue;

/**
 * Created by zhangdan on 2017/9/19.
 *
 * comments:
 */

public interface Deque<E> extends Queue<E> {

    void addFirst(E e);

    void addLast(E e);

    boolean offerFirst(E e);

    boolean offerLast(E e);

    E removeFirst();

    E removeLast();

    E pollFirst();

    E pollLast();

    E getFirst();

    E getLast();

    E peekFirst();

    E peekLast();

    boolean removeFirstRemoveOccurace();

    boolean add(E e);

    boolean offer(E e);

    E remove();

    E poll();

    E element();

    E peek();

    void push(E e);

    E pop();

    boolean remove(Object o);

    boolean contains(Object o);

    int size();

    Iterator<E> descendingIterator();

}
