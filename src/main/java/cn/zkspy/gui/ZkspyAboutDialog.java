/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.zkspy.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;

/**
 * The About Dialog for the application
 */
public class ZkspyAboutDialog extends JDialog {
    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = 7246317006450057793L;

    public ZkspyAboutDialog(Frame frame) {
        super(frame);
        this.setLayout(new BorderLayout());
        // this.setIconImage(ZkspyAboutDialog.getInformationIcon()
        // .getImage());
        this.setTitle("About ZooInspector");
        this.setModal(true);
        this.setAlwaysOnTop(true);
        this.setResizable(false);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        JEditorPane aboutPane = new JEditorPane();
        aboutPane.setEditable(false);
        aboutPane.setOpaque(false);
        java.net.URL aboutURL = ZkspyAboutDialog.class.getClassLoader().getResource("about.html");
        try {
            aboutPane.setPage(aboutURL);
        }
        catch (IOException e) {
        }
        panel.add(aboutPane, BorderLayout.CENTER);
        panel.setPreferredSize(new Dimension(600, 200));
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ZkspyAboutDialog.this.dispose();
            }
        });
        buttonsPanel.add(okButton);
        this.add(panel, BorderLayout.CENTER);
        this.add(buttonsPanel, BorderLayout.SOUTH);
        this.pack();
    }
}
