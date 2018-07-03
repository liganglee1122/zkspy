package cn.zkspy.gui.nodeviewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class AclViewer extends AbstractNodeViewer {
    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = 2456035172197350963L;

    private final JPanel aclDataPanel;

    public AclViewer(String title) {
        this.title = title;
        this.setLayout(new BorderLayout());
        this.aclDataPanel = new JPanel();
        this.aclDataPanel.setBackground(Color.WHITE);
        JScrollPane scroller = new JScrollPane(this.aclDataPanel);
        this.add(scroller, BorderLayout.CENTER);
    }

    @Override
    protected void changeViewer() {
        // Remove All component
        this.aclDataPanel.removeAll();
        List<Map<String, String>> acls = this.nodeData.getAclData();

        aclDataPanel.setLayout(new GridBagLayout());
        int j = 0;
        for (Map<String, String> acl : acls) {
            int rowPos = 2 * j + 1;
            JPanel aclPanel = new JPanel();
            aclPanel.setBackground(Color.WHITE);
            aclPanel.setLayout(new GridBagLayout());
            int i = 0;
            for (Map.Entry<String, String> entry : acl.entrySet()) {
                int rowPosACL = 2 * i + 1;
                JLabel label = new JLabel(entry.getKey());
                JTextField text = new JTextField(entry.getValue());
                text.setEditable(false);
                GridBagConstraints c1 = new GridBagConstraints();
                c1.gridx = 1;
                c1.gridy = rowPosACL;
                c1.gridwidth = 1;
                c1.gridheight = 1;
                c1.weightx = 0;
                c1.weighty = 0;
                c1.anchor = GridBagConstraints.NORTHWEST;
                c1.fill = GridBagConstraints.BOTH;
                c1.insets = new Insets(5, 5, 5, 5);
                c1.ipadx = 0;
                c1.ipady = 0;
                aclPanel.add(label, c1);
                GridBagConstraints c2 = new GridBagConstraints();
                c2.gridx = 3;
                c2.gridy = rowPosACL;
                c2.gridwidth = 1;
                c2.gridheight = 1;
                c2.weightx = 0;
                c2.weighty = 0;
                c2.anchor = GridBagConstraints.NORTHWEST;
                c2.fill = GridBagConstraints.BOTH;
                c2.insets = new Insets(5, 5, 5, 5);
                c2.ipadx = 0;
                c2.ipady = 0;
                aclPanel.add(text, c2);
                i++;
            }
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 1;
            c.gridy = rowPos;
            c.gridwidth = 1;
            c.gridheight = 1;
            c.weightx = 1;
            c.weighty = 1;
            c.anchor = GridBagConstraints.NORTHWEST;
            c.fill = GridBagConstraints.NONE;
            c.insets = new Insets(5, 5, 5, 5);
            c.ipadx = 0;
            c.ipady = 0;
            aclDataPanel.add(aclPanel, c);
        }
        this.aclDataPanel.validate();
        this.aclDataPanel.repaint();
    }

    @Override
    public void cleanViewer() {
        this.aclDataPanel.removeAll();
        this.aclDataPanel.validate();
        this.aclDataPanel.repaint();
    }
}
