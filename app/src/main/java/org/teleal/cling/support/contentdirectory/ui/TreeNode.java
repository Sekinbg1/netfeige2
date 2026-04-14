package org.teleal.cling.support.contentdirectory.ui;

import java.util.ArrayList;
import java.util.List;

public class TreeNode {
	private Object userObject;
	private TreeNode parent;
	private List<TreeNode> children;

	public TreeNode(Object userObject) {
		this.userObject = userObject;
		this.children = new ArrayList<>();
	}

	public Object getUserObject() {
		return userObject;
	}

	public void setUserObject(Object userObject) {
		this.userObject = userObject;
	}

	public TreeNode getParent() {
		return parent;
	}

	public void setParent(TreeNode parent) {
		this.parent = parent;
	}

	public List<TreeNode> getChildren() {
		return children;
	}

	public void addChild(TreeNode child) {
		child.setParent(this);
		this.children.add(child);
	}

	public void removeChild(TreeNode child) {
		child.setParent(null);
		this.children.remove(child);
	}

	public void removeAllChildren() {
		for (TreeNode child : children) {
			child.setParent(null);
		}
		this.children.clear();
	}

	public int getChildCount() {
		return children.size();
	}

	public TreeNode getChildAt(int index) {
		if (index >= 0 && index < children.size()) {
			return children.get(index);
		}
		return null;
	}

	public boolean isLeaf() {
		return children.isEmpty();
	}

	public int getIndex(TreeNode node) {
		return children.indexOf(node);
	}
}
