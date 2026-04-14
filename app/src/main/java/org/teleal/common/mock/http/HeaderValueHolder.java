package org.teleal.common.mock.http;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
class HeaderValueHolder {
	private final List values = new LinkedList();

	HeaderValueHolder() {
	}

	public void setValue(Object obj) {
		this.values.clear();
		this.values.add(obj);
	}

	public void addValue(Object obj) {
		this.values.add(obj);
	}

	public void addValues(Collection collection) {
		this.values.addAll(collection);
	}

	public void addValueArray(Object obj) {
		this.values.addAll(Arrays.asList(toObjectArray(obj)));
	}

	public List getValues() {
		return Collections.unmodifiableList(this.values);
	}

	public Object getValue() {
		if (this.values.isEmpty()) {
			return null;
		}
		return this.values.get(0);
	}

	public static HeaderValueHolder getByName(Map map, String str) {
		for (Object key : map.keySet()) {
                String str2 = (String) key;
			if (str2.equalsIgnoreCase(str)) {
				return (HeaderValueHolder) map.get(str2);
			}
		}
		return null;
	}

	public static Object[] toObjectArray(Object obj) {
		if (obj instanceof Object[]) {
			return (Object[]) obj;
		}
		if (obj == null) {
			return new Object[0];
		}
		if (!obj.getClass().isArray()) {
			throw new IllegalArgumentException("Source is not an array: " + obj);
		}
		int length = Array.getLength(obj);
		if (length == 0) {
			return new Object[0];
		}
		Object[] objArr = (Object[]) Array.newInstance(Array.get(obj, 0).getClass(), length);
		for (int i = 0; i < length; i++) {
			objArr[i] = Array.get(obj, i);
		}
		return objArr;
	}
}

