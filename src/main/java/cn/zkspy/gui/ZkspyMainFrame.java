package cn.zkspy.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import cn.zkspy.curator.client.Constant;
import cn.zkspy.curator.client.ZookeeperClient;

public class ZkspyMainFrame extends JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = -3505236978967010917L;

    private ZkspyTreeViewer tree;

    private ZkspyNodeViewerPanel viewerPanel;

    private JButton launchBtn;

    private JButton stopBtn;

    private JButton refreshBtn;

    private JButton aboutBtn;

    public ZkspyMainFrame(String name) {
        super(name);
        viewerPanel = new ZkspyNodeViewerPanel();
    }

    public void initialize() {
        try {
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    super.windowClosed(e);
                    ZookeeperClient.closeClient();
                }
            });

            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            Icon launchBtnIcon = new ImageIcon(loader.getResource(Constant.IconPath.CONNECT));
            launchBtn = new JButton(launchBtnIcon);
            launchBtn.setToolTipText("Connect");
            launchBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    (new ZkspyConnectionPropertiesDialog(null, ZkspyMainFrame.this)).setVisible(true);
                }

            });
            Icon stopBtnIcon = new ImageIcon(loader.getResource(Constant.IconPath.DISCONNECT));
            stopBtn = new JButton(stopBtnIcon);
            stopBtn.setToolTipText("Disconnect");
            stopBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    ZookeeperClient.closeClient();
                    ZkspyMainFrame.this.launchBtn.setEnabled(true);
                    ZkspyMainFrame.this.stopBtn.setEnabled(false);
                    ZkspyMainFrame.this.refreshBtn.setEnabled(false);
                    ZkspyMainFrame.this.cleanNodeViewer();
                }
            });
            stopBtn.setEnabled(false);

            Icon refreshBtnIcon = new ImageIcon(loader.getResource(Constant.IconPath.REFRESH));
            refreshBtn = new JButton(refreshBtnIcon);
            refreshBtn.setToolTipText("刷新");
            refreshBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    ZkspyMainFrame.this.refreshTreeView();
                    ZkspyMainFrame.this.cleanNodeViewer();
                }
            });
            refreshBtn.setEnabled(false);

            Icon aboutBtnIcon = new ImageIcon(loader.getResource(Constant.IconPath.ABOUT));
            aboutBtn = new JButton(aboutBtnIcon);
            aboutBtn.setToolTipText("About");
            aboutBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    ZkspyAboutDialog zkSpyad = new ZkspyAboutDialog(JOptionPane.getRootFrame());
                    zkSpyad.setVisible(true);
                }
            });

            JToolBar bar = new JToolBar();
            bar.add(launchBtn);
            bar.addSeparator();
            bar.add(stopBtn);
            bar.addSeparator();
            bar.add(refreshBtn);
            bar.addSeparator();
            bar.add(aboutBtn);
            bar.setFloatable(false);

            this.getContentPane().add(bar, BorderLayout.NORTH);
            tree = new ZkspyTreeViewer(new DefaultMutableTreeNode());
            tree.getSelectionModel().addTreeSelectionListener(viewerPanel);
            JScrollPane treeScroller = new JScrollPane(tree);
            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScroller, viewerPanel);
            splitPane.setResizeWeight(0.4);
            this.add(splitPane, BorderLayout.CENTER);

            this.setSize(1024, 768);
            this.setVisible(true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshTreeView() {
        final Set<TreePath> expandedNodes = new LinkedHashSet<TreePath>();
        int rowCount = tree.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            TreePath path = tree.getPathForRow(i);
            if (tree.isExpanded(path)) {
                expandedNodes.add(path);
            }
        }
        final TreePath[] selectedNodes = tree.getSelectionPaths();
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {

            @Override
            protected Boolean doInBackground() throws Exception {
                tree.setModel(new DefaultTreeModel(new ZkspyTreeNode("/", null)));
                return true;
            }

            @Override
            protected void done() {
                for (TreePath path : expandedNodes) {
                    tree.expandPath(path);
                }
                tree.getSelectionModel().setSelectionPaths(selectedNodes);
            }
        };
        worker.execute();
    }

    private class ZkspyTreeNode implements TreeNode {
        private final String nodePath;

        private final String nodeName;

        private final ZkspyTreeNode parent;

        public ZkspyTreeNode(String nodePath, ZkspyTreeNode parent) {
            this.parent = parent;
            this.nodePath = nodePath;
            int index = nodePath.lastIndexOf("/");
            if (index == -1) {
                throw new IllegalArgumentException("Invalid node path" + nodePath);
            }
            this.nodeName = nodePath.substring(index + 1);
        }

        public Enumeration children() {
            List<String> children;
            try {
                children = ZookeeperClient.getClient().getChildren().forPath(this.nodePath);
                Collections.sort(children);
                List<TreeNode> returnChildren = new ArrayList<TreeNode>();
                for (String child : children) {
                    returnChildren
                        .add(new ZkspyTreeNode((this.nodePath.equals("/") ? "" : this.nodePath) + "/" + child, this));
                }
                return Collections.enumeration(returnChildren);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        public boolean getAllowsChildren() {
            return false;
        }

        public TreeNode getChildAt(int i) {
            String child;
            try {
                child = ZookeeperClient.getClient().getChildren().forPath(this.nodePath).get(i);
                if (child != null) {
                    return new ZkspyTreeNode((this.nodePath.equals("/") ? "" : this.nodePath) + "/" + child, this);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        public int getChildCount() {
            try {
                return ZookeeperClient.getClient().getChildren().forPath(this.nodePath).size();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        }

        public int getIndex(TreeNode treenode) {
            try {
                int index = nodePath.lastIndexOf("/");
                if (index == -1 || (!nodePath.equals("/") && nodePath.charAt(nodePath.length() - 1) == '/')) {
                    throw new IllegalArgumentException("Invalid node path: " + nodePath);
                }
                String parentPath = nodePath.substring(0, index);
                String child = nodePath.substring(index + 1);
                if (parentPath != null && parentPath.length() > 0) {
                    List<String> children = ZookeeperClient.getClient().getChildren().forPath(parentPath);
                    if (children != null) {
                        return children.indexOf(child);
                    }
                }
            }
            catch (Exception e) {

            }

            return -1;
        }

        public TreeNode getParent() {
            return this.parent;
        }

        public boolean isLeaf() {
            try {
                return ZookeeperClient.getClient().getChildren().forPath(this.nodePath).isEmpty();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        public String toString() {
            return this.nodeName;
        }
    }

    public void reInitializeViewer() {
        this.launchBtn.setEnabled(false);
        this.stopBtn.setEnabled(true);
        this.refreshBtn.setEnabled(true);
        this.refreshTreeView();
    }

    private void cleanNodeViewer() {
        this.viewerPanel.cleanNodeViewer();
    }
}
