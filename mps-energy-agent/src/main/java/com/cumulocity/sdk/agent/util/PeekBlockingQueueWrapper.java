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
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.Semaphore;

/**
 * A queue wrapper that blocks on {@link #peek()} and {@link #element()} until the {@link #peek()} or {@link #remove()}.
 * @param <E> type of queue element. 
 */
public class PeekBlockingQueueWrapper<E> implements Queue<E>, Serializable {
	
	private static final long serialVersionUID = -37536519365737162L;
	
	private Queue<E> wrappedQueue;
	private Semaphore semaphore;
	
	public PeekBlockingQueueWrapper(Queue<E> wrappedQueue) {
		this.wrappedQueue = wrappedQueue;
		this.semaphore = new Semaphore(1);
	}
	
	@Override
	public boolean add(E e) {
		return wrappedQueue.add(e);
	}

	@Override
	public boolean offer(E e) {
		return wrappedQueue.offer(e);
	}

	@Override
	public E remove() {
		E e = wrappedQueue.remove();
		semaphore.release();
		return e;
	}

	@Override
	public E poll() {
		E e = wrappedQueue.poll();
		semaphore.release();
		return e;
	}

	@Override
	public E element() {
		if (semaphore.tryAcquire()) {
			try {
				return wrappedQueue.element();
			} catch (NoSuchElementException nse) {
				semaphore.release();
				throw nse;
			}
		} else {
			throw new NoSuchElementException("Queue is currently blocked!");
		}
	}

	@Override
	public E peek() {
		if (semaphore.tryAcquire()) {
			E e = wrappedQueue.peek();
			if (e == null) {
				semaphore.release();
			}
			return e;
		} else {
			return null;
		}
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
	public boolean contains(Object o) {
		return wrappedQueue.contains(o);
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
	public boolean containsAll(Collection<?> c) {
		return wrappedQueue.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		return wrappedQueue.addAll(c);
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
}
