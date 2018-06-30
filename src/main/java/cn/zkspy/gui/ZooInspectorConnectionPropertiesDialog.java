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
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cn.zkspy.manager.client.ZookeeperClient;

/**
 * The connection properties dialog. This is used to determine the settings for
 * connecting to a zookeeper instance
 */
public class ZooInspectorConnectionPropertiesDialog extends JDialog {

	private JTextField connText;

	private JLabel errCaution;

	/**
	 * @param lastConnectionProps
	 *            - the last connection properties used. if this is the first
	 *            conneciton since starting the applications this will be the
	 *            default settings
	 * @param connectionPropertiesTemplateAndLabels
	 *            - the connection properties and labels to show in this dialog
	 * @param zooInspectorPanel
	 *            - the {@link ZooInspectorPanel} linked to this dialog
	 */
	public ZooInspectorConnectionPropertiesDialog(Properties lastConnectionProps,
			final ZkspyMainFrame zooInspectorPanel) {
		this.setLayout(new GridLayout(2, 1));
		this.setTitle("Connection Settings");
		this.setModal(true);
		this.setAlwaysOnTop(true);
		this.setResizable(false);
		final JPanel options = new JPanel();
		this.setSize(400, 200);
		options.setLayout(new GridLayout(1, 2));

		JLabel connLable = new JLabel("connectString:");

		connText = new JTextField();
		connText.setColumns(16);
		connText.setText("127.0.0.1:2181");
		options.add(connLable);
		options.add(connText);

		options.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

		JPanel buttonsPanel = new JPanel();

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO false 則彈窗提示未連接
				try {
					String connectString = ZooInspectorConnectionPropertiesDialog.this.connText.getText();

					if (null == connectString || "".equals(connectString.trim())) {
						ZooInspectorConnectionPropertiesDialog.this.errCaution.setText("请输入正确的连接串!");
						return;
					}

					if (!checkConnectString(connectString)) {
						ZooInspectorConnectionPropertiesDialog.this.errCaution.setText("请输入正确的连接串!");
						return;
					}

					if (ZookeeperClient.reBuildClient(connectString)) {
						ZooInspectorConnectionPropertiesDialog.this.dispose();
						zooInspectorPanel.reInitializeViewer();
					} else {
						ZooInspectorConnectionPropertiesDialog.this.errCaution.setText("无法连接，请重试或检查连接串及zk状态！");
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		buttonsPanel.add(okButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				ZooInspectorConnectionPropertiesDialog.this.dispose();
			}
		});

		buttonsPanel.add(cancelButton);

		buttonsPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

		this.add(options);
		this.add(buttonsPanel);

		errCaution = new JLabel("e.g. : 127.0.0.1:2181");
		errCaution.setForeground(Color.RED);

		// 构造主框架
		setLayout(new BorderLayout());
		getContentPane().add(options, BorderLayout.NORTH);
		getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
		getContentPane().add(errCaution);
		setLocationRelativeTo(options);
		this.pack();
	}

	private boolean checkConnectString(String connStr) {
		int splitIndex = connStr.indexOf(":");
		if (-1 == splitIndex) {
			return false;
		}

		String ip = connStr.substring(0, splitIndex);
		String port = connStr.substring(splitIndex + 1);
		if (null == ip || "".equals(ip.toString()) || null == port || "".equals(port.trim())) {
			return false;
		}
		if (!isIP(ip)) {
			return false;
		}
		if (!isPortCorrect(port)) {
			return false;
		}
		return true;
	}

	public boolean isIP(String addr) {
		if (addr.length() < 7 || addr.length() > 15 || "".equals(addr)) {
			return false;
		}
		/**
		 * 判断IP格式和范围
		 */
		String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";

		Pattern pat = Pattern.compile(rexp);

		Matcher mat = pat.matcher(addr);

		boolean ipAddress = mat.find();

		return ipAddress;
	}

	private boolean isPortCorrect(String port) {

		int intPort;
		try {

			intPort = Integer.valueOf(port);
		} catch (Exception e) {
			return false;
		}

		if (intPort < 0 || intPort > 65535) {
			return false;
		}

		return true;
	}

}
