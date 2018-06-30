package cn.zkspy.gui.nodeviewer;

import javax.swing.JPanel;

import cn.zkspy.gui.NodeData;

public abstract class AbstractNodeViewer extends JPanel {

	protected NodeData nodeData;

	protected String title;

	public void refreshViewer(NodeData nodeData) {
		this.nodeData = nodeData;
		handleNodeData();
		changeViewer();
	}

	protected abstract void handleNodeData();

	protected abstract void changeViewer();

	public NodeData getNodeData() {
		return nodeData;
	}

	public void setNodeData(NodeData nodeData) {
		this.nodeData = nodeData;
	}

	public String getTitle() {
		return this.title;
	}
	
	protected abstract void refreshNodeViewer();

}
