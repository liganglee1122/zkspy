package cn.zkspy.gui.nodeviewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class MetaDataViewer extends AbstractNodeViewer {

    /**
     * 
     */
    private static final long serialVersionUID = 3272838255811168448L;

    private final JPanel metaDataPanel;

    public MetaDataViewer(String title) {
        this.title = title;
        this.setLayout(new BorderLayout());
        this.metaDataPanel = new JPanel();
        this.metaDataPanel.setBackground(Color.WHITE);
        JScrollPane scroller = new JScrollPane(this.metaDataPanel);
        this.add(scroller, BorderLayout.CENTER);
    }

    @Override
    protected void changeViewer() {
        // Remove All component
        this.metaDataPanel.removeAll();
        Map<String, String> data = this.nodeData.getMetaData();

        MetaDataViewer.this.metaDataPanel.setLayout(new GridBagLayout());
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setLayout(new GridBagLayout());
        int i = 0;
        int rowPos = 0;
        for (Map.Entry<String, String> entry : data.entrySet()) {
            rowPos = 2 * i + 1;
            JLabel label = new JLabel(entry.getKey());
            JTextField text = new JTextField(entry.getValue());
            text.setEditable(false);
            GridBagConstraints c1 = new GridBagConstraints();
            c1.gridx = 0;
            c1.gridy = rowPos;
            c1.gridwidth = 1;
            c1.gridheight = 1;
            c1.weightx = 0;
            c1.weighty = 0;
            c1.anchor = GridBagConstraints.WEST;
            c1.fill = GridBagConstraints.HORIZONTAL;
            c1.insets = new Insets(5, 5, 5, 5);
            c1.ipadx = 0;
            c1.ipady = 0;
            infoPanel.add(label, c1);
            GridBagConstraints c2 = new GridBagConstraints();
            c2.gridx = 2;
            c2.gridy = rowPos;
            c2.gridwidth = 1;
            c2.gridheight = 1;
            c2.weightx = 0;
            c2.weighty = 0;
            c2.anchor = GridBagConstraints.WEST;
            c2.fill = GridBagConstraints.HORIZONTAL;
            c2.insets = new Insets(5, 5, 5, 5);
            c2.ipadx = 0;
            c2.ipady = 0;
            infoPanel.add(text, c2);
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
        this.metaDataPanel.add(infoPanel, c);
        this.metaDataPanel.revalidate();
        this.metaDataPanel.repaint();
    }

    @Override
    public void cleanViewer() {
        this.metaDataPanel.removeAll();
        this.metaDataPanel.revalidate();
        this.metaDataPanel.repaint();
    }

}
