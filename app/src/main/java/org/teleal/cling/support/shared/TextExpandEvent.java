package org.teleal.cling.support.shared;

import org.teleal.common.androidfwk.DefaultEvent;

public class TextExpandEvent extends DefaultEvent<String> {
    public TextExpandEvent(String str) {
        super(str);
    }
}

