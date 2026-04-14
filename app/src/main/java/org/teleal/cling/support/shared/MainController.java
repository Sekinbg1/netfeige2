package org.teleal.cling.support.shared;

import android.view.View;
import org.teleal.common.swingfwk.AbstractController;
import org.teleal.common.swingfwk.Controller;

public class MainController<V extends View> extends AbstractController<V> {

	public MainController(V view) {
		super(view);
		initialize();
	}

	public MainController(V view, Controller parent) {
		super(view, parent);
		initialize();
	}

	private void initialize() {
	}
}
