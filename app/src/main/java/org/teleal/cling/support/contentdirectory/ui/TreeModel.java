package org.teleal.cling.support.contentdirectory.ui;

public class TreeModel {
	private TreeNode root;
	private TreeModelListener listener;

	public interface TreeModelListener {
		void nodeStructureChanged(TreeNode node);
		void nodesWereInserted(TreeNode node, int[] indices);
		void nodesWereRemoved(TreeNode node, int[] indices, Object[] children);
	}

	public TreeModel(TreeNode root) {
		this.root = root;
	}

	public TreeNode getRoot() {
		return root;
	}

	public void setTreeModelListener(TreeModelListener listener) {
		this.listener = listener;
	}

	public void insertNodeInto(TreeNode newChild, TreeNode parent, int index) {
		if (index >= 0 && index <= parent.getChildCount()) {
			parent.addChild(newChild);
			if (listener != null) {
				int[] indices = {index};
				listener.nodesWereInserted(parent, indices);
			}
		}
	}

	public void removeNodeFromParent(TreeNode node) {
		TreeNode parent = node.getParent();
		if (parent != null) {
			int index = parent.getIndex(node);
			parent.removeChild(node);
			if (listener != null) {
				int[] indices = {index};
				Object[] removedChildren = {node};
				listener.nodesWereRemoved(parent, indices, removedChildren);
			}
		}
	}

	public void nodeStructureChanged(TreeNode node) {
		if (listener != null) {
			listener.nodeStructureChanged(node);
		}
	}
}
