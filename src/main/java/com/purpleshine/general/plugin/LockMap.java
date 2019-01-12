package com.purpleshine.general.plugin;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;

public final class LockMap<T, V> extends ConcurrentHashMap<T, V> {
	/**
	 * 
	 */
	static private final long serialVersionUID = 1L;
	
	private ReadWriteLock Lock = new ReentrantReadWriteLock();

	@Override
	public void clear() {
		Lock.writeLock().lock();
		try {
			super.clear();
		} finally {
			Lock.writeLock().unlock();
		}
	}

	@Override
	public boolean containsKey(Object key) {
		Lock.readLock().lock();
		try {
			return super.containsKey(key);
		} finally {
			Lock.readLock().unlock();
		}
	}

	@Override
	public boolean containsValue(Object value) {
		Lock.readLock().lock();
		try {
			return super.containsValue(value);
		} finally {
			Lock.readLock().unlock();
		}
	}

	@Override
	public Set<java.util.Map.Entry<T, V>> entrySet() {
		Lock.readLock().lock();
		try {
			return super.entrySet();
		} finally {
			Lock.readLock().unlock();
		}
	}

	@Override
	public void forEach(BiConsumer<? super T, ? super V> action) {
		Lock.readLock().lock();
		try {
			super.forEach(action);
		} finally {
			Lock.readLock().unlock();
		}
	}

	@Override
	public V get(Object key) {
		Lock.readLock().lock();
		try {
			return super.get(key);
		} finally {
			Lock.readLock().unlock();
		}
	}

	@Override
	public boolean isEmpty() {
		Lock.readLock().lock();
		try {
			return super.isEmpty();
		} finally {
			Lock.readLock().unlock();
		}
	}

	@Override
	public KeySetView<T, V> keySet() {
		Lock.readLock().lock();
		try {
			return super.keySet();
		} finally {
			Lock.readLock().unlock();
		}
	}

	@Override
	public KeySetView<T, V> keySet(V arg0) {
		try {
			return super.keySet(arg0);
		} finally {
			Lock.readLock().unlock();
		}
	}

	@Override
	public Enumeration<T> keys() {
		try {
			return super.keys();
		} finally {
			Lock.readLock().unlock();
		}
	}

	@Override
	public V put(T key, V value) {
		Lock.writeLock().lock();
		try {
			return super.put(key, value);
		} finally {
			Lock.writeLock().unlock();
		}
	}

	@Override
	public void putAll(Map<? extends T, ? extends V> m) {
		Lock.writeLock().lock();
		try {
			super.putAll(m);
		} finally {
			Lock.writeLock().unlock();
		}
	}

	@Override
	public V putIfAbsent(T key, V value) {
		Lock.writeLock().lock();
		try {
			return super.putIfAbsent(key, value);
		} finally {
			Lock.writeLock().unlock();
		}
	}

	@Override
	public boolean remove(Object key, Object value) {
		Lock.writeLock().lock();
		try {
			return super.remove(key, value);
		} finally {
			Lock.writeLock().unlock();
		}
	}

	@Override
	public V remove(Object key) {
		Lock.writeLock().lock();
		try {
			return super.remove(key);
		} finally {
			Lock.writeLock().unlock();
		}
	}


	@Override
	public int size() {
		Lock.readLock().lock();
		try {
			return super.size();
		} finally {
			Lock.readLock().unlock();
		}
	}

	@Override
	public Collection<V> values() {
		Lock.readLock().lock();
		try {
			return super.values();
		} finally {
			Lock.readLock().unlock();
		}
	}
	
	
}
