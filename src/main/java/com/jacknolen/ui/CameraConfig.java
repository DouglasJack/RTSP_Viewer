package com.jacknolen.ui;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;

public class CameraConfig extends JDialog {
    private final JTextField nameField;
    private final JTextField ipField;
    private final JTextField portField;
    private final JTextField endpointField;
    private final JTextField userField;
    private final JPasswordField passField;
    private boolean confirmed = false;

    public CameraConfig(JFrame parent) {
        super(parent, "Add Camera Stream", true);
        setLayout(new MigLayout("wrap 2", "[][grow]"));
        setSize(400, 250);
        setLocationRelativeTo(parent);

        nameField = new JTextField("Camera 1");
        ipField = new JTextField("192.168.0.20");
        portField = new JTextField("554");
        endpointField = new JTextField("/cam/realmonitor?channel=1&subtype=1");
        userField = new JTextField("admin");
        passField = new JPasswordField();

        add(new JLabel("Name:")); add(nameField);
        add(new JLabel("IP Address:")); add(ipField);
        add(new JLabel("Port:")); add(portField);
        add(new JLabel("Endpoint:")); add(endpointField);
        add(new JLabel("Username:")); add(userField);
        add(new JLabel("Password:")); add(passField);

        JButton ok = new JButton("Add");
        JButton cancel = new JButton("Cancel");

        ok.addActionListener(e -> {
            confirmed = true;
            dispose();
        });
        cancel.addActionListener(e -> dispose());

        add(ok, "span, split 2, center");
        add(cancel);

        getRootPane().setDefaultButton(ok);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public String getCameraName() {
        return nameField.getText().trim();
    }

    public String buildRtspUrl() {
        String user = userField.getText().trim();
        String pass = new String(passField.getPassword());
        String ip = ipField.getText().trim();
        String port = portField.getText().trim();
        String endpoint = endpointField.getText().trim();
        return "rtsp://" + user + ":" + pass + "@" + ip + ":" + port + endpoint;
    }
}