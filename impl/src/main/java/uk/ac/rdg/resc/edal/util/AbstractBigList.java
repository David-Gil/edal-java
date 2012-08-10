/*******************************************************************************
 * Copyright (c) 2012 The University of Reading
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the University of Reading, nor the names of the
 *    authors or contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/

package uk.ac.rdg.resc.edal.util;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>
 * Skeletal class providing a partial implementation of {@link BigList}.
 * </p>
 * 
 * @param <E>
 *            The type of the elements in the list
 * @author Jon
 */
public abstract class AbstractBigList<E> extends AbstractList<E> implements BigList<E> {

    @Override
    public final E get(int index) {
        return this.get((long) index);
    }

    /**
     * {@inheritDoc}
     * <p>
     * The iterator returned by this method simply wraps the {@link #get(long)
     * get()} method. The performance of the iteration may be very poor if get()
     * opens and closes a file with each invocation. For disk-backed BigLists,
     * some buffering could be performed in subclasses.
     * </p>
     * 
     * @return
     */
    @Override
    public Iterator<E> iterator() {
        return new Itr();
    }

    /**
     * {@inheritDoc}
     * <p>
     * This default implementation simply calls {@link #get(long)} for each
     * index in the provided List. Subclasses may wish to provide a more
     * efficient implementation.
     * </p>
     */
    @Override
    public List<E> getAll(List<Long> indices) {
        List<E> all = new ArrayList<E>(indices.size());
        for (long index : indices) {
            all.add(this.get(index));
        }
        return all;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This default implementation simply calls {@link #get(long)} for each
     * index between fromIndex and toIndex Subclasses may wish to provide a more
     * efficient implementation.
     * </p>
     */
    @Override
    public List<E> getAll(long fromIndex, long toIndex) {
        if (fromIndex > toIndex)
            throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex
                    + ")");
        if (fromIndex < 0L)
            throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
        if (toIndex > this.sizeAsLong())
            throw new IndexOutOfBoundsException("toIndex = " + toIndex);

        long size = toIndex - fromIndex;
        if (size > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Can't create List of " + size + " elements");
        }

        List<E> all = new ArrayList<E>((int) size);
        for (long i = fromIndex; i < toIndex; i++) {
            all.add(this.get(i));
        }
        return all;
    }

    @Override
    public final int size() {
        // This follows the specification of java.util.List
        long size = this.sizeAsLong();
        if (size < Integer.MAX_VALUE)
            return (int) size;
        return Integer.MAX_VALUE;
    }

    /**
     * We need to define our own iterator class that handles long integer
     * cursors
     */
    private final class Itr implements Iterator<E> {

        private long cursor = 0L;

        @Override
        public boolean hasNext() {
            return cursor < sizeAsLong();
        }

        @Override
        public E next() {
            E next = get(cursor);
            cursor++;
            return next;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }
    }

}