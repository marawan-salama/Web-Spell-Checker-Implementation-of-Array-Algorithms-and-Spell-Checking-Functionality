
package edu.uwm.cs351.util;

import java.util.Comparator;

/**
 * An class with utility code for sorting and using sorted arrays.
 * 
 * @param K type of keys
 */
public class SortUtils<T> {
	private Comparator<T> comparator;

	/**
	 * Create an instance using the given comparator. If it is null, use natural
	 * ordering assuming the type is comparable (`compareTo`).
	 * 
	 * @param c comparator, or if null, use natural ordering
	 */
	@SuppressWarnings("unchecked")
	public SortUtils(Comparator<T> c) {
		if (c == null) {
			c = (o1, o2) -> ((Comparable<T>) o1).compareTo(o2);
		}
		comparator = c;
	}

	/**
	 * merge sort the elements of the array
	 * 
	 * @param array must not be null
	 */
	public void mergeSort(T[] array) {
		if (array == null)
			return;
		mergeSortKeep(0, array.length, array, array.clone());
	}

	/**
	 * Merge sort "in" array between lo and hi and put results in "out". The "in"
	 * array will be used for scratch in the same range.
	 * 
	 * @param lo  >= 0
	 * @param hi  >= lo
	 * @param in  must not be null
	 * @param out must not be null or same as in
	 */
	public void mergeSortMove(int lo, int hi, T[] in, T[] out) {
		if (lo < 0 || lo > hi)
			return;
		if (in == null || out == null)
			return;
		if (hi == lo)
			return;
		if (hi - lo > 1) {
			int mid = (lo + hi) / 2;
			mergeSortKeep(lo, mid, in, out);
			mergeSortKeep(mid, hi, in, out);
			merge(lo, mid, hi, in, out);
		} else {
			out[lo] = in[lo];
		}
	}

	/**
	 * merge sort "in" array between lo and hi in place using temp array for scratch
	 * 
	 * @param lo   >= 0
	 * @param hi   >= lo
	 * @param data must not be null, length >= hi
	 * @param temp must not be null or same as data, length >= hi
	 */
	public void mergeSortKeep(int lo, int hi, T[] data, T[] temp) {
		if (lo < 0 || hi < lo)
			return;
		if (data == null || data.length < hi || temp == null || temp.length < hi)
			return;
		if (hi - lo > 1) {
			int mid = lo + (hi - lo) / 2;
			mergeSortKeep(lo, mid, data, temp);
			mergeSortKeep(mid, hi, data, temp);
			merge(lo, mid, hi, data, temp);
			System.arraycopy(temp, lo, data, lo, hi - lo);
		}
	}

	/**
	 * merge sorted lists in [lo,mid) and [mid,hi) in "in" into [lo,hi) in "out".
	 * 
	 * @param lo  >= 0
	 * @param mid >= lo
	 * @param hi  >= mid
	 * @param in  must not be null, length >= hi
	 * @param out must not be null or same as in, length >= hi
	 */
	public void merge(int lo, int mid, int hi, T[] in, T[] out) {
		if (lo < 0 || mid < lo || hi < mid)
			return;
		if (in == null || in.length < hi || out == null || out.length < hi)
			return;
		int i = lo, x = mid, y = lo;
		while (i < mid && x < hi) {
			if (comparator.compare(in[i], in[x]) <= 0) {
				out[y] = in[i];
				i++;
			} else {
				out[y] = in[x];
				x++;
			}
			y++;
		}
		while (i < mid) {
			out[y] = in[i];
			i++;
			y++;
		}
		while (x < hi) {
			out[y] = in[x];
			x++;
			y++;
		}

	}

	/**
	 * Write elements from sorted array in range [lo1,hi1) into out [lo1,...] as
	 * long as they don't occur in sorted array rem in range [lo2,hi2). The result
	 * (out) will also be sorted.
	 * 
	 * @param lo1 >= 0
	 * @param hi1 >= lo1
	 * @param lo2 >= 0
	 * @param hi2 >= lo2
	 * @param in  must not be null, length >= hi1
	 * @param rem must not be null, length >= hi2
	 * @param out must not be null, length >= hi1. The array out may be the same as
	 *            the in, but not the same as rem.
	 * @return the index after the last element added into out.
	 */
	public int difference(int lo1, int hi1, int lo2, int hi2, T[] in, T[] rem, T[] out) {
		int i = lo1, x = lo2, y = lo1;
		while (i < hi1 && x < hi2) {
			if (comparator.compare(in[i], rem[x]) > 0) {
				x++;
			} else if (comparator.compare(in[i], rem[x]) < 0) {
				out[y] = in[i];
				i++;
				y++;
			} else {
				i++;
			}
		}
		while (i < hi1) {
			out[y] = in[i];
			i++;
			y++;
		}
		return y;
	}

	/**
	 * Remove duplicate elements (ones with 0 comparison) from a sorted array. If
	 * the array is not sorted, it won't necessarily work correctly.
	 * 
	 * @param array must not be null
	 * @return number of unique elements.
	 */
	public int uniq(T[] array) {
		if (array == null) {
			throw new IllegalArgumentException("array cannot be null");
		}
		if (array.length == 0) {
			return 0;
		}
		int x = 1;
		for (int i = 1; i < array.length; i++) {
			if (!array[i].equals(array[i - 1])) {
				x++;
			}
		}
		return x;
	}

	/**
	 * Remove duplicate elements (one with 0 comparison) from a sorted array range
	 * [lo,hi), writing the unique elements to the second array [lo,...).
	 * 
	 * @param lo  >= 0
	 * @param hi  >= lo
	 * @param in  must not be null
	 * @param out must not be null. This array may be the same as in without
	 *            problems.
	 * @return index after unique elements
	 */
	public int uniq(int lo, int hi, T[] in, T[] out) {
		if (in == null || out == null) {
			throw new IllegalArgumentException("in and out arrays cannot be null");
		}
		if (hi <= lo) {
			return lo;
		}
		int x = 1;
		out[lo] = in[lo];
		for (int i = lo + 1; i < hi; i++) {
			if (comparator.compare(in[i], in[i - 1]) != 0) {
				out[lo + x] = in[i];
				x++;
			}
		}
		return lo + x;
	}

}