/*
 * Copyright (C) 2013 Cumulocity GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of 
 * this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.cumulocity.sdk.agent.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Queue;

/**
 * A queue wrapper that ensures that only unique elements are present in the queue at a time.
 * @param <E> type of queue elements.
 */
public class UniqueElementsQueueWrapper<E> implements Queue<E>, Serializable {

	private static final long serialVersionUID = 7071179561098052849L;

	private Queue<E> wrappedQueue;
	private Comparator<E> itentityComparator;
	
	/**
	 * @param wrappedQueue the queue to wrap.
	 * @param itentityComparator the comaprator that ensures elements uniqueness.
	 */
	public UniqueElementsQueueWrapper(Queue<E> wrappedQueue, Comparator<E> itentityComparator) {
		this.wrappedQueue = wrappedQueue;
		this.itentityComparator = itentityComparator;
	}
	
	@Override
	public boolean offer(E e) {
		return contains(e) ? false : wrappedQueue.offer(e);
	}
	
	@Override
	public boolean add(E e) {
		return contains(e) ? false : wrappedQueue.add(e);
	}
	
	@Override
	public boolean addAll(Collection<? extends E> c) {
		boolean result = true;
		for (E e : c) {
			result = (result && add(e));
		}
		return result;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean contains(Object o) {
		if (o == null) {
			return false;
		}
		Iterator<E> i = iterator();
		while (i.hasNext()) {
			if (itentityComparator.compare(i.next(), (E) o) == 0) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean containsAll(Collection<?> c) {
		return wrappedQueue.containsAll(c);
	}

	@Override
	public int size() {
		return wrappedQueue.size();
	}

	@Override
	public boolean isEmpty() {
		return wrappedQueue.isEmpty();
	}

	@Override
	public Iterator<E> iterator() {
		return wrappedQueue.iterator();
	}

	@Override
	public Object[] toArray() {
		return wrappedQueue.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return wrappedQueue.toArray(a);
	}

	@Override
	public boolean remove(Object o) {
		return wrappedQueue.remove(o);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return wrappedQueue.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return wrappedQueue.retainAll(c);
	}

	@Override
	public void clear() {
		wrappedQueue.clear();
	}

	@Override
	public E remove() {
		return wrappedQueue.remove();
	}

	@Override
	public E poll() {
		return wrappedQueue.poll();
	}

	@Override
	public E element() {
		return wrappedQueue.element();
	}

	@Override
	public E peek() {
		return wrappedQueue.peek();
	}
}
