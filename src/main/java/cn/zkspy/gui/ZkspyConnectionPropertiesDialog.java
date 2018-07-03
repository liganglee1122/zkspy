package cn.zkspy.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cn.zkspy.curator.client.ZookeeperClient;

public class ZkspyConnectionPropertiesDialog extends JDialog {

    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = 1192981657031422073L;

    /**
     * connText , user input<br>
     */
    private JTextField connText;

    /**
     * errCautionLabel <br>
     */
    private JLabel errCautionLabel;

    public ZkspyConnectionPropertiesDialog(Properties lastConnectionProps, final ZkspyMainFrame zooInspectorPanel) {
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

                try {
                    String connectString = ZkspyConnectionPropertiesDialog.this.connText.getText();

                    if (null == connectString || "".equals(connectString.trim())
                        || !checkConnectString(connectString)) {
                        ZkspyConnectionPropertiesDialog.this.errCautionLabel.setText("Please check the input!");
                        return;
                    }

                    if (ZookeeperClient.reBuildClient(connectString)) {
                        ZkspyConnectionPropertiesDialog.this.dispose();
                        zooInspectorPanel.reInitializeViewer();
                    }
                    else {
                        ZkspyConnectionPropertiesDialog.this.errCautionLabel
                            .setText("unable to connect zookeeper, please retry or check zk status.");
                    }
                }
                catch (Exception e1) {
                    e1.printStackTrace();
                    ZkspyConnectionPropertiesDialog.this.errCautionLabel
                        .setText("unable to connect zookeeper, please retry or check zk status.");
                }
            }
        });

        buttonsPanel.add(okButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                ZkspyConnectionPropertiesDialog.this.dispose();
            }
        });

        buttonsPanel.add(cancelButton);

        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        this.add(options);
        this.add(buttonsPanel);

        errCautionLabel = new JLabel("e.g. : 127.0.0.1:2181");
        errCautionLabel.setForeground(Color.RED);

        setLayout(new BorderLayout());
        getContentPane().add(options, BorderLayout.NORTH);
        getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
        getContentPane().add(errCautionLabel);
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

    /**
     * Description: <br>
     * 
     * @author XXX<br>
     * @taskId <br>
     * @param addr
     * @return <br>
     */
    private boolean isIP(String addr) {
        if (addr.length() < 7 || addr.length() > 15 || "".equals(addr)) {
            return false;
        }

        String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";

        Pattern pat = Pattern.compile(rexp);

        Matcher mat = pat.matcher(addr);

        boolean ipAddress = mat.find();

        return ipAddress;
    }

    /**
     * Description: port should between 1 to 65535
     * 
     * @author XXX<br>
     * @taskId <br>
     * @param port
     * @return <br>
     */
    private boolean isPortCorrect(String port) {

        int intPort;
        try {

            intPort = Integer.valueOf(port);
        }
        catch (Exception e) {
            return false;
        }

        if (intPort < 0 || intPort > 65535) {
            return false;
        }

        return true;
    }

}
