package com.falseapple.general.core;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public class Event <T extends Event.EventArgs> {
	private final ConcurrentHashMap<Object, BiConsumer<Object, T>> listeners = new ConcurrentHashMap<>();
	private final Object owner;
	
	public Event(Object owner) {
		this.owner = owner;
	}
	
	public void add(Object listener, BiConsumer<Object, T> event) {
		listeners.put(listener, event);
	}
	
	public boolean remove(Object listener) {
		return Objects.nonNull(listeners.remove(listener));
	}
	
	public void invoke(T args) {
		listeners.values().forEach(i -> i.accept(owner, args));
	}
	
	public void dispose() {
		listeners.clear();
	}
	
	static public class EventArgs {
		//
	}
}


