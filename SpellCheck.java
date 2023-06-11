
package edu.uwm.cs351;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;

import edu.uwm.cs351.util.Element;
import edu.uwm.cs351.util.SortUtils;
import edu.uwm.cs351.util.XMLParseException;
import edu.uwm.cs351.util.XMLReader;

public class SpellCheck {

	private final String[] dictionary;

	public SpellCheck() throws IOException {
		ArrayList<String> temp = new ArrayList<String>();
		InputStream is = new FileInputStream(new File("lib", "dictionary.txt"));
		Reader r = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(r);
		String s;
		while ((s = br.readLine()) != null) {
			temp.add(s);
		}
		br.close();
		dictionary = temp.toArray(new String[temp.size()]);
	}

	/**
	 * Check the words in the given element. Return a list of all words that do not
	 * occur in the dictionary.
	 * 
	 * @param e HTML element (ignore script and style tags!)
	 * @return non-null list of all words not occurring in the dictionary.
	 */
	public List<String> check(Element e) {
		List<Object> ret = e.contentList();
		if (ret.isEmpty())
			throw new IllegalArgumentException();
		ArrayList<String> temp = new ArrayList<String>();
		ArrayList<String> list = wordsList(e, ret, temp);
		SortUtils<String> word = new SortUtils<String>(null);
		String[] arr = new String[list.size()];
		list.toArray(arr);
		word.mergeSort(arr);
		word.uniq(arr);
		int lstind = word.difference(0, arr.length, 0, dictionary.length, arr, dictionary, arr);
		return Arrays.asList(arr).subList(0, lstind);
	}

	private ArrayList<String> wordsList(Element e, List<Object> in, ArrayList<String> temp) {
		if (!e.getName().equals("style") && !e.getName().equals("script")) {
			for (Object o : in) {
				if (o instanceof Element) {
					Element ee = (Element) o;
					wordsList(ee, ee.contentList(), temp);
				} else {
					String st = ((String) o);
					st = st.replaceAll("[.?\",|:;()]", "");
					st = st.replace("\n", " ");
					String[] splt = st.split(" ");
					for (String ss : splt) {
						if (!ss.equals("")) {
							temp.add(ss);
						}
					}
				}
			}
		}
		return temp;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		SpellCheck sc = new SpellCheck();
		for (String url : args) {
			System.out.println("Checking spelling of HTML in " + url);
			try {
				Reader r = new InputStreamReader(new BufferedInputStream(new URL(url).openStream()));
				final XMLReader t = new XMLReader(r);
				// t.addCDATA("script");
				Object next = t.readElement();
				if (next instanceof Element) { // could be null
					Element contents = (Element) next;
					if (!contents.getName().equalsIgnoreCase("html")) {
						throw new XMLParseException("element must be HTML not " + contents.getName());
					}
					System.out.println("Mispelled:");
					for (String s : sc.check(contents)) {
						System.out.println("  " + s);
					}
				} else {
					throw new XMLParseException("contents must be HTML, not " + next);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
