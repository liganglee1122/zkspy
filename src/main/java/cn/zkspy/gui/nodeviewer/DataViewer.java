package cn.zkspy.gui.nodeviewer;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class DataViewer extends AbstractNodeViewer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6046382935191206613L;
	private JTextArea dataArea;

	public DataViewer(String title) {
		this.setLayout(new BorderLayout());
		this.title = title;
		this.dataArea = new JTextArea();
		this.dataArea.setLineWrap(true);
		this.dataArea.setEditable(false);
		JScrollPane scroller = new JScrollPane(this.dataArea);
		scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.add(scroller, BorderLayout.CENTER);
	}

	@Override
	protected void handleNodeData() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void changeViewer() {
		// TODO Auto-generated method stub
		this.dataArea.setText(this.nodeData.getData());
	}

	@Override
	protected void refreshNodeViewer() {
		// TODO Auto-generated method stub

	}

}
