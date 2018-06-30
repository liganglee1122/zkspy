package cn.zkspy.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.SwingWorker;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import cn.zkspy.gui.nodeviewer.AbstractNodeViewer;
import cn.zkspy.gui.nodeviewer.AclViewer;
import cn.zkspy.gui.nodeviewer.DataViewer;
import cn.zkspy.gui.nodeviewer.MetaDataViewer;
import cn.zkspy.manager.client.ZookeeperClient;

public class ZkspyNodeViewerPanel extends JPanel implements TreeSelectionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4968817346770582479L;

	List<AbstractNodeViewer> nodeViewerList = new ArrayList<AbstractNodeViewer>(3);

	private String curNodePath = null;
	private JTextPane curPathTextPane;

	public ZkspyNodeViewerPanel() {

		nodeViewerList.add(new DataViewer("DataViewer"));
		nodeViewerList.add(new MetaDataViewer("MetaDataViewer"));
		nodeViewerList.add(new AclViewer("AclViewer"));

		this.setLayout(new BorderLayout(1, 2));
		// 构造右侧展示面板\
		JPanel topPanel = new JPanel();
		// topPanel.setMaximumSize(new Dimension(50, 50));
		topPanel.setLayout(new BorderLayout(1, 2));

		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		Icon refreshBtnIcon = new ImageIcon(loader.getResource(Constant.IconPath.REFRESH));
		JButton refreshBtn = new JButton(refreshBtnIcon);
		refreshBtn.setToolTipText("舒心数据");
		refreshBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

			}

		});

		JToolBar bar = new JToolBar();
		bar.add(refreshBtn);
		bar.setSize(50, 50);
		bar.setFloatable(false);
		topPanel.add(bar, BorderLayout.NORTH);

		curPathTextPane = new JTextPane();
		topPanel.add(curPathTextPane);
		topPanel.setBackground(new Color(200));

		JPanel bottomPanel = new JPanel();
		bottomPanel.setBackground(new Color(100));
		bottomPanel.setLayout(new BorderLayout(1, 1));

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
		bottomPanel.add(tabbedPane, BorderLayout.CENTER);
		for (AbstractNodeViewer viewer : nodeViewerList) {
			tabbedPane.addTab(viewer.getTitle(), viewer);
		}

		JScrollPane treeScroller = new JScrollPane(topPanel);
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, treeScroller, bottomPanel);
		splitPane.setResizeWeight(0.05);
		this.add(splitPane, BorderLayout.CENTER);
		// this.add(topPanel, BorderLayout.NORTH);
		// this.add(bottomPanel, BorderLayout.SOUTH);
	}

	public void valueChanged(TreeSelectionEvent e) {

		TreePath[] paths = e.getPaths();

		TreePath curPath = paths[0];
		StringBuilder sb = new StringBuilder();
		Object[] pathArray = curPath.getPath();
		for (Object o : pathArray) {
			if (o != null) {
				String nodeName = o.toString();
				if (nodeName != null) {
					if (nodeName.length() > 0) {
						sb.append("/"); //$NON-NLS-1$
						sb.append(o.toString());
					}
				}
			}
		}

		this.curNodePath = sb.toString();
		this.curPathTextPane.setText(curNodePath);

		SwingWorker<NodeData, Void> worker = new SwingWorker<NodeData, Void>() {

			@Override
			protected NodeData doInBackground() throws Exception {
				byte[] bytes = ZookeeperClient.getClient().getData().forPath(ZkspyNodeViewerPanel.this.curNodePath);
				NodeData curNodeData = new NodeData();
				curNodeData.setCurPath(ZkspyNodeViewerPanel.this.curNodePath);
				if (null != bytes) {
					String curData = new String(bytes);
					curNodeData.setData(curData);
				}
				return curNodeData;
			}

			@Override
			protected void done() {
				try {

					NodeData curNodeData = get();
					ZkspyNodeViewerPanel.this.changeNodeViewer(curNodeData);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		if (null != curNodePath && !"".equals(curNodePath.trim())) {
			worker.execute();
		}

	}

	private void changeNodeViewer(NodeData nodeData) {
		for (AbstractNodeViewer viewer : nodeViewerList) {
			viewer.refreshViewer(nodeData);
		}
	}

}
