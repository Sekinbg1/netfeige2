package org.teleal.common.androidfwk;

/**
 * Stub class for Android compatibility
 * Original library not available
 */
public class DefaultEvent<T> {
	private T data;
	
	public DefaultEvent(T data) {
		this.data = data;
	}
	
	public T getData() {
		return data;
	}
}
