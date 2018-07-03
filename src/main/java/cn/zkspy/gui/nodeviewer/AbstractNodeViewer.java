package cn.zkspy.gui.nodeviewer;

import javax.swing.JPanel;

import cn.zkspy.curator.client.NodeData;

public abstract class AbstractNodeViewer extends JPanel {

    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = -3909851700296346236L;

    protected NodeData nodeData;

    protected String title;

    public void refreshViewer(NodeData nodeData) {
        this.nodeData = nodeData;
        changeViewer();
    }
    
    public abstract void cleanViewer();

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

}
