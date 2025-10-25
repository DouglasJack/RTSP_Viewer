package com.jacknolen.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.jacknolen.cam.CameraConfigStore;
import com.jacknolen.cam.Camera;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class UI extends JFrame {
    private static JPanel mainCameraPane;
    private JPanel buttonDashboard;
    private JPanel informationDashboard;
    public UI() throws IOException {
        init();
    }

    public static JPanel getCameraPane(){
        return mainCameraPane;
    }

    private JPanel mainPanel() {
        JPanel root = new JPanel();
        root.setLayout(new MigLayout("", "[120px][grow]", "[grow][30px]"));

        mainCameraPane = new JPanel();
        mainCameraPane.setLayout(new MigLayout("insets 10, wrap 3", "[240px]", "[240px]"));
        mainCameraPane.putClientProperty(FlatClientProperties.STYLE,
                "arc:10;"+
                        "[light]background:darken(@background,5%);" +
                        "[dark]background:lighten(@background,5%)");

        buttonDashboard = new JPanel();
        buttonDashboard.setLayout(new BorderLayout());

        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new MigLayout("wrap", "fill", "fill"));
        buttonPane.putClientProperty(FlatClientProperties.STYLE,
                "arc:10;"+
                        "[light]background:darken(@background,3%);" +
                        "[dark]background:lighten(@background,3%)");
        buttonPane.setMaximumSize(new Dimension(150, Integer.MAX_VALUE));

        // Lower information panel
        informationDashboard = new JPanel();
        informationDashboard.setLayout(new BorderLayout());

        JPanel infoPane = new JPanel();
        infoPane.setLayout(new MigLayout("wrap", "fill", "fill"));
        infoPane.putClientProperty(FlatClientProperties.STYLE,
                "arc:10;"+
                        "[light]background:darken(@background,3%);" +
                        "[dark]background:lighten(@background,3%)");
        infoPane.setMaximumSize(new Dimension(150, Integer.MAX_VALUE));

        // Info text
        JLabel branding = new JLabel();
        branding.setText("RTSP Viewer - AimIT");
        infoPane.add(branding);

        JScrollPane central = new JScrollPane(mainCameraPane);
        central.getVerticalScrollBar().setUnitIncrement(16);

        // Used to add cameras here...

        JToggleButton overview = new JToggleButton();
        overview.setText("Overview");
        overview.addActionListener(e -> {
            if (overview.isSelected()){
                overview.setText("Config View");
            }else{
                overview.setText("Overview");
            }
        });
        buttonPane.add(overview);


        JButton addCamera = new JButton();
        addCamera.setText("Add camera stream");
        addCamera.addActionListener(e -> {
            CameraConfig dialog = new CameraConfig(UI.this);
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                String name = dialog.getCameraName();
                String rtsp = dialog.buildRtspUrl();

                try {
                    Camera cam = new Camera(name, rtsp);
                    cam.startVideoCapture();
                    getCameraPane().add(cam.getCameraUI(), "grow");
                    getCameraPane().revalidate();
                    getCameraPane().repaint();

                    // Save to config
                    List<CameraConfigStore.CameraEntry> entries = CameraConfigStore.load();
                    CameraConfigStore.CameraEntry entry = new CameraConfigStore.CameraEntry();
                    entry.name = name;
                    entry.url = rtsp;
                    entries.add(entry);
                    CameraConfigStore.save(entries);

                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(UI.this, "Failed to add camera: " + ex.getMessage());
                }
            }
        });

        buttonPane.add(addCamera,"span,grow");




        informationDashboard.add(infoPane, BorderLayout.CENTER);
        buttonDashboard.add(buttonPane,BorderLayout.CENTER);
        root.add(buttonDashboard,"grow, push");
        root.add(central,"grow,wrap");
        root.add(informationDashboard,"span, grow");

        List<CameraConfigStore.CameraEntry> entries = CameraConfigStore.load();
        for (CameraConfigStore.CameraEntry entry : entries) {
            try {
                Camera cam = new Camera(entry.name, entry.url);
                cam.startVideoCapture();
                mainCameraPane.add(cam.getCameraUI(), "grow");
            } catch (IOException ex) {
                System.err.println("Could not load camera " + entry.name);
            }
        }


        return root;
    }

    private void init() {
        setTitle("RTSP Viewer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(new Dimension(1498,920));
        setMinimumSize(new Dimension(1498,920));
        setResizable(true);
        setLocationRelativeTo(null);
        JPanel mP = mainPanel();
        setContentPane(mP);
    }
}
