package com.purpleshine.general.helpers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;

public final class CollectionUtil {
	
    /**
     * 合併兩個array
     * @param arrays
     * @return
     */
    public static <T> T[] mergeArray(T[] array1, T[] array2)
    {
        return ArrayUtils.addAll(array1, array2);
    }
    
    /**
     * 合併多個array
     * @param arrays
     * @return
     */
    @SuppressWarnings("unchecked")
    static public <T> T[] mergeArray(T[]... arrays) {
        return (T[]) Stream.of(arrays)
                .filter(Objects::nonNull)
                .flatMap(Stream::of)
                .toArray(Object[]::new);
    }
    
    /**
     * 將陣列轉LIST
     * @param array - 陣列
     * @return - LIST
     */
    static public <T> ArrayList<T> arrayToList(T[] array) {
        ArrayList<T> list = new ArrayList<>();
        for (T t : array) {
            list.add(t);
        }
        return list;
    }
    
	/**
	 * 將key轉換成list, 型態是T
	 * @param entrys
	 * @return
	 */
	static public <T> Collection<T> getEntryKeys(final Collection<? extends Entry<T, ?>> entrys) {
		return entrys.stream()
				.map(Entry::getKey)
				.collect(Collectors.toList());
	}
	
	/**
	 * 將key轉換成list, 型態是T
	 * @param entrys
	 * @return
	 */
	public static Collection<Object> getObjectKeys(Collection<? extends Entry<?, ?>> entrys) {
		return entrys.stream()
				.map(Entry::getKey)
				.collect(Collectors.toList());
	}
	
	/**
	 * 將value轉換成list, 型態是Object
	 * @param entrys
	 * @return
	 */
	static public <T> Collection<T> getEntryValues(final Collection<? extends Entry<?, T>> entrys) {
		return entrys.stream()
				.map(Entry::getValue)
				.collect(Collectors.toList());
	}
	
	/**
	 * 將value轉換成list, 型態是Object
	 * @param entrys
	 * @return
	 */
	static public Collection<Object> getObjectValues(final Collection<? extends Entry<?, ?>> entrys) {
		return entrys.stream()
				.map(Entry::getValue)
				.collect(Collectors.toList());
	}
	
	/**
	 * 取得兩個的相交
	 * @param ls
	 * @param ls2
	 * @return
	 */
	static public <T> List<T> intersect(List<T> ls, List<T> ls2) { 
        List<T> list = new ArrayList<T>(ls); 
        list.retainAll(ls2);      
        return list; 
    } 
	
	/**
	 * 取得兩個的集合
	 * @param ls
	 * @param ls2
	 * @return
	 */
	static public <T> List<T> union(List<T> ls, List<T> ls2) { 
        List<T> list = new ArrayList<T>(ls); 
        list.addAll(ls2);     
        return list; 
    } 
    
	/**
	 * 取得兩個的差集
	 * @param ls
	 * @param ls2
	 * @return
	 */
    static public <T> List<T> diff(List<T> ls, List<T> ls2) { 
        List<T> list = new ArrayList<T>(ls); 
        list.removeAll(ls2);         
        return list; 
    } 
    
    /**
     * 將Map重新排序依照key
     * @param map
     * @return
     */
    public static <K extends Comparable<? super K>, V> Map<K, V> sortByKey(Map<K, V> map) {
        List<Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort((o1, o2) -> o1.getKey().compareTo(o2.getKey()));

        Map<K, V> result = new LinkedHashMap<>();
        for (Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
    
    /**
     * 將Map重新排序依照value
     * @param map
     * @return
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort((o1, o2) -> o1.getValue().compareTo(o2.getValue()));

        Map<K, V> result = new LinkedHashMap<>();
        for (Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
}
