package org.teleal.common.swingfwk;

/**
 * Stub class for Android compatibility
 * Original library not available
 */
public abstract class AbstractController<V> {
	protected V view;

	public AbstractController(V view) {
		this.view = view;
	}
}
