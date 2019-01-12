package com.purpleshine.general.core.interfaces;

@FunctionalInterface
public interface ArgsAction {
	void invoke(Object... args);
	
	@FunctionalInterface
	public interface Action {
		void invoke();
	}
}

