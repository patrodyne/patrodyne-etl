// PatroDyne: Patron Supported Dynamic Executables, http://patrodyne.org
// Released under LGPL license. See terms at http://www.gnu.org.
package org.patrodyne.etl.transformio.gui;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.swing.AbstractListModel;

/**
 * A list model that delegates to the underlying list.
 * 
 * @author Rick O'Sullivan
 */
@SuppressWarnings("serial")
public class DelegatingListModel<E>
	extends AbstractListModel<E>
	implements List<E>
{
	private List<E> delegate;
	
	/**
	 * Construct with a list delegate.
	 * 
	 * @param delegate The underlying list to delegate model actions. 
	 */
	public DelegatingListModel(List<E> delegate)
	{
		this.delegate = delegate;
	}
	
	/** @see javax.swing.ListModel#getSize() */
	@Override
	public int getSize()
	{
		return delegate.size();
	}

	/** @see javax.swing.ListModel#getElementAt(int) */
	@Override
	public E getElementAt(int index)
	{
		return delegate.get(index);
	}

	/** @see java.util.List#size() */
	@Override
	public int size()
	{
		return delegate.size();
	}

	/** @see java.util.List#isEmpty() */
	@Override
	public boolean isEmpty()
	{
		return delegate.isEmpty();
	}

	/** @see java.util.List#contains(java.lang.Object) */
	@Override
	public boolean contains(Object o)
	{
		return delegate.contains(o);
	}

	/** @see java.util.List#iterator() */
	@Override
	public Iterator<E> iterator()
	{
		return delegate.iterator();
	}

	/** @see java.util.List#toArray() */
	@Override
	public Object[] toArray()
	{
		return delegate.toArray();
	}

	/** @see java.util.List#toArray(T[]) */
	@Override
	public <T> T[] toArray(T[] a)
	{
		return delegate.toArray(a);
	}

	/** @see java.util.List#add(java.lang.Object) */
	@Override
	public boolean add(E e)
	{
		int index = delegate.size();
		boolean result = delegate.add(e);
		fireIntervalAdded(this, index, index);
		return result;
	}

	/** @see java.util.List#remove(java.lang.Object) */
	@Override
	public boolean remove(Object o)
	{
		int index = indexOf(o);
		boolean result = delegate.remove(o);
		if (index >= 0)
			fireIntervalRemoved(this, index, index);
		return result;
	}

	/** @see java.util.List#containsAll(java.util.Collection) */
	@Override
	public boolean containsAll(Collection<?> c)
	{
		return delegate.containsAll(c);
	}

	/** @see java.util.List#addAll(java.util.Collection) */
	@Override
	public boolean addAll(Collection<? extends E> c)
	{
		return addAll(delegate.size(), c);
	}

	/** @see java.util.List#addAll(int, java.util.Collection) */
	@Override
	public boolean addAll(int index0, Collection<? extends E> c)
	{
		boolean result = delegate.addAll(index0, c);
		int index1 = index0 + c.size();
		if ( index1 > index0)
			fireIntervalAdded(this, index0, index1-1);
		return result;
	}

	/** @see java.util.List#removeAll(java.util.Collection) */
	@Override
	public boolean removeAll(Collection<?> c)
	{
		int index1 = delegate.size();
		boolean result = delegate.removeAll(c);
		if (index1 > 0)
			fireIntervalRemoved(this, 0, index1-1);
		return result;
	}

	/** @see java.util.List#retainAll(java.util.Collection) */
	@Override
	public boolean retainAll(Collection<?> c)
	{
		throw new UnsupportedOperationException("Non-interval operation.");
	}

	/** @see java.util.List#clear() */
	@Override
	public void clear()
	{
		int index1 = delegate.size();
		delegate.clear();
		if (index1 > 0)
			fireIntervalRemoved(this, 0, index1-1);
	}

	/** @see java.util.List#get(int) */
	@Override
	public E get(int index)
	{
		return delegate.get(index);
	}

	/** @see java.util.List#set(int, java.lang.Object) */
	@Override
	public E set(int index, E element)
	{
		E previous = delegate.get(index);
		delegate.set(index, element);
		fireContentsChanged(this, index, index);
		return previous;
	}

	/** @see java.util.List#add(int, java.lang.Object) */
	@Override
	public void add(int index, E element)
	{
		delegate.add(index, element);
		fireIntervalAdded(this, index, index);		
	}

	/** @see java.util.List#remove(int) */
	@Override
	public E remove(int index)
	{
        E removed = delegate.get(index);
        delegate.remove(index);
        fireIntervalRemoved(this, index, index);
        return removed;
	}

	/** @see java.util.List#indexOf(java.lang.Object) */
	@Override
	public int indexOf(Object o)
	{
		return delegate.indexOf(o);
	}

	/** @see java.util.List#lastIndexOf(java.lang.Object) */
	@Override
	public int lastIndexOf(Object o)
	{
		return delegate.lastIndexOf(o);
	}

	/** @see java.util.List#listIterator() */
	@Override
	public ListIterator<E> listIterator()
	{
		return delegate.listIterator();
	}

	/** @see java.util.List#listIterator(int) */
	@Override
	public ListIterator<E> listIterator(int index)
	{
		return delegate.listIterator(index);
	}

	/** @see java.util.List#subList(int, int) */
	@Override
	public List<E> subList(int fromIndex, int toIndex)
	{
		return delegate.subList(fromIndex, toIndex);
	}
}

// vi:set tabstop=4 hardtabs=4 shiftwidth=4:
