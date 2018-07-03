package cn.zkspy.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
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

import cn.zkspy.curator.client.Constant;
import cn.zkspy.curator.client.NodeData;
import cn.zkspy.curator.client.ZookeeperClient;
import cn.zkspy.gui.nodeviewer.AbstractNodeViewer;
import cn.zkspy.gui.nodeviewer.AclViewer;
import cn.zkspy.gui.nodeviewer.DataViewer;
import cn.zkspy.gui.nodeviewer.MetaDataViewer;

public class ZkspyNodeViewerPanel extends JPanel implements TreeSelectionListener {

    /**
     * 
     */
    private static final long serialVersionUID = -4968817346770582479L;

    List<AbstractNodeViewer> nodeViewerList = new ArrayList<AbstractNodeViewer>(3);

    private String curNodePath = null;

    private JTextPane curPathTextPane;

    public ZkspyNodeViewerPanel() {

        nodeViewerList.add(new DataViewer("Node Data"));
        nodeViewerList.add(new MetaDataViewer("Node MetaData"));
        nodeViewerList.add(new AclViewer("Node ACL"));

        this.setLayout(new BorderLayout(1, 2));
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout(1, 2));

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Icon refreshBtnIcon = new ImageIcon(loader.getResource(Constant.IconPath.REFRESH));
        JButton refreshBtn = new JButton(refreshBtnIcon);
        refreshBtn.setToolTipText("Refresh");
        refreshBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ZkspyNodeViewerPanel.this.executeWork();
            }

        });

        JToolBar bar = new JToolBar();
        bar.add(refreshBtn);
        bar.setSize(50, 50);
        bar.setFloatable(false);
        topPanel.add(bar, BorderLayout.NORTH);

        curPathTextPane = new JTextPane();
        topPanel.add(curPathTextPane);

        JPanel bottomPanel = new JPanel();
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
    }

    public void valueChanged(TreeSelectionEvent e) {
        TreePath[] paths = e.getPaths();

        TreePath curPath = paths[0];
        StringBuilder sb = new StringBuilder();
        Object[] pathArray = curPath.getPath();
        for (Object path : pathArray) {
            if (path != null) {
                String nodeName = path.toString();
                if (null != nodeName) {
                    if (nodeName.length() > 0) {
                        sb.append("/");
                        sb.append(path.toString());
                    }
                }
            }
        }

        this.curNodePath = sb.toString();
        this.curPathTextPane.setText(curNodePath);

        this.executeWork();
    }

    private void changeNodeViewer(NodeData nodeData) {
        for (AbstractNodeViewer viewer : nodeViewerList) {
            viewer.refreshViewer(nodeData);
        }
    }

    private void executeWork() {
        SwingWorker<NodeData, Void> worker = new SwingWorker<NodeData, Void>() {
            @Override
            protected NodeData doInBackground() throws Exception {
                return ZookeeperClient.getAllData(ZkspyNodeViewerPanel.this.curNodePath);
            }

            @Override
            protected void done() {
                try {
                    NodeData curNodeData = get();
                    ZkspyNodeViewerPanel.this.changeNodeViewer(curNodeData);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        if (null != curNodePath && !"".equals(curNodePath.trim())) {
            worker.execute();
        }
    }

    public void cleanNodeViewer()
    {
        for (AbstractNodeViewer viewer : nodeViewerList) {
            viewer.cleanViewer();
        }
    }
}
