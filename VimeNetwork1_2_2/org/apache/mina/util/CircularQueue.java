/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.util;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Queue;

public class CircularQueue<E>
extends AbstractList<E>
implements Queue<E>,
Serializable {
    private static final long serialVersionUID = 3993421269224511264L;
    private static final int DEFAULT_CAPACITY = 4;
    private final int initialCapacity;
    private volatile Object[] items;
    private int mask;
    private int first = 0;
    private int last = 0;
    private boolean full;
    private int shrinkThreshold;

    public CircularQueue() {
        this(4);
    }

    public CircularQueue(int initialCapacity) {
        int actualCapacity = CircularQueue.normalizeCapacity(initialCapacity);
        this.items = new Object[actualCapacity];
        this.mask = actualCapacity - 1;
        this.initialCapacity = actualCapacity;
        this.shrinkThreshold = 0;
    }

    private static int normalizeCapacity(int initialCapacity) {
        int actualCapacity = 1;
        while (actualCapacity < initialCapacity) {
            if ((actualCapacity <<= 1) >= 0) continue;
            actualCapacity = 0x40000000;
            break;
        }
        return actualCapacity;
    }

    public int capacity() {
        return this.items.length;
    }

    @Override
    public void clear() {
        if (!this.isEmpty()) {
            Arrays.fill(this.items, null);
            this.first = 0;
            this.last = 0;
            this.full = false;
            this.shrinkIfNeeded();
        }
    }

    @Override
    public E poll() {
        if (this.isEmpty()) {
            return null;
        }
        Object ret = this.items[this.first];
        this.items[this.first] = null;
        this.decreaseSize();
        if (this.first == this.last) {
            this.last = 0;
            this.first = 0;
        }
        this.shrinkIfNeeded();
        return (E)ret;
    }

    @Override
    public boolean offer(E item) {
        if (item == null) {
            throw new IllegalArgumentException("item");
        }
        this.expandIfNeeded();
        this.items[this.last] = item;
        this.increaseSize();
        return true;
    }

    @Override
    public E peek() {
        if (this.isEmpty()) {
            return null;
        }
        return (E)this.items[this.first];
    }

    @Override
    public E get(int idx) {
        this.checkIndex(idx);
        return (E)this.items[this.getRealIndex(idx)];
    }

    @Override
    public boolean isEmpty() {
        return this.first == this.last && !this.full;
    }

    @Override
    public int size() {
        if (this.full) {
            return this.capacity();
        }
        if (this.last >= this.first) {
            return this.last - this.first;
        }
        return this.last - this.first + this.capacity();
    }

    @Override
    public String toString() {
        return "first=" + this.first + ", last=" + this.last + ", size=" + this.size() + ", mask = " + this.mask;
    }

    private void checkIndex(int idx) {
        if (idx < 0 || idx >= this.size()) {
            throw new IndexOutOfBoundsException(String.valueOf(idx));
        }
    }

    private int getRealIndex(int idx) {
        return this.first + idx & this.mask;
    }

    private void increaseSize() {
        this.last = this.last + 1 & this.mask;
        this.full = this.first == this.last;
    }

    private void decreaseSize() {
        this.first = this.first + 1 & this.mask;
        this.full = false;
    }

    private void expandIfNeeded() {
        if (this.full) {
            int oldLen = this.items.length;
            int newLen = oldLen << 1;
            Object[] tmp = new Object[newLen];
            if (this.first < this.last) {
                System.arraycopy(this.items, this.first, tmp, 0, this.last - this.first);
            } else {
                System.arraycopy(this.items, this.first, tmp, 0, oldLen - this.first);
                System.arraycopy(this.items, 0, tmp, oldLen - this.first, this.last);
            }
            this.first = 0;
            this.last = oldLen;
            this.items = tmp;
            this.mask = tmp.length - 1;
            if (newLen >>> 3 > this.initialCapacity) {
                this.shrinkThreshold = newLen >>> 3;
            }
        }
    }

    private void shrinkIfNeeded() {
        int size = this.size();
        if (size <= this.shrinkThreshold) {
            int oldLen = this.items.length;
            int newLen = CircularQueue.normalizeCapacity(size);
            if (size == newLen) {
                newLen <<= 1;
            }
            if (newLen >= oldLen) {
                return;
            }
            if (newLen < this.initialCapacity) {
                if (oldLen == this.initialCapacity) {
                    return;
                }
                newLen = this.initialCapacity;
            }
            Object[] tmp = new Object[newLen];
            if (size > 0) {
                if (this.first < this.last) {
                    System.arraycopy(this.items, this.first, tmp, 0, this.last - this.first);
                } else {
                    System.arraycopy(this.items, this.first, tmp, 0, oldLen - this.first);
                    System.arraycopy(this.items, 0, tmp, oldLen - this.first, this.last);
                }
            }
            this.first = 0;
            this.last = size;
            this.items = tmp;
            this.mask = tmp.length - 1;
            this.shrinkThreshold = 0;
        }
    }

    @Override
    public boolean add(E o) {
        return this.offer(o);
    }

    @Override
    public E set(int idx, E o) {
        this.checkIndex(idx);
        int realIdx = this.getRealIndex(idx);
        Object old = this.items[realIdx];
        this.items[realIdx] = o;
        return (E)old;
    }

    @Override
    public void add(int idx, E o) {
        if (idx == this.size()) {
            this.offer(o);
            return;
        }
        this.checkIndex(idx);
        this.expandIfNeeded();
        int realIdx = this.getRealIndex(idx);
        if (this.first < this.last) {
            System.arraycopy(this.items, realIdx, this.items, realIdx + 1, this.last - realIdx);
        } else if (realIdx >= this.first) {
            System.arraycopy(this.items, 0, this.items, 1, this.last);
            this.items[0] = this.items[this.items.length - 1];
            System.arraycopy(this.items, realIdx, this.items, realIdx + 1, this.items.length - realIdx - 1);
        } else {
            System.arraycopy(this.items, realIdx, this.items, realIdx + 1, this.last - realIdx);
        }
        this.items[realIdx] = o;
        this.increaseSize();
    }

    @Override
    public E remove(int idx) {
        if (idx == 0) {
            return this.poll();
        }
        this.checkIndex(idx);
        int realIdx = this.getRealIndex(idx);
        Object removed = this.items[realIdx];
        if (this.first < this.last) {
            System.arraycopy(this.items, this.first, this.items, this.first + 1, realIdx - this.first);
        } else if (realIdx >= this.first) {
            System.arraycopy(this.items, this.first, this.items, this.first + 1, realIdx - this.first);
        } else {
            System.arraycopy(this.items, 0, this.items, 1, realIdx);
            this.items[0] = this.items[this.items.length - 1];
            System.arraycopy(this.items, this.first, this.items, this.first + 1, this.items.length - this.first - 1);
        }
        this.items[this.first] = null;
        this.decreaseSize();
        this.shrinkIfNeeded();
        return (E)removed;
    }

    @Override
    public E remove() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        }
        return this.poll();
    }

    @Override
    public E element() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        }
        return this.peek();
    }
}

