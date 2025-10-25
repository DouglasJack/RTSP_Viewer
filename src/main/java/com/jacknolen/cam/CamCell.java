package com.jacknolen.cam;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.UIScale;
import com.jacknolen.ui.FullscreenViewer;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class CamCell extends JPanel {
    private JLabel cameraPanel;
    private JPanel infoPanel;
    private Camera datacam;

    public CamCell() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("fill, ins 0", "[grow]", "[grow][pref]"));
        setBorder(BorderFactory.createMatteBorder(0, 0, UIScale.scale(1), 0, new Color(210, 14, 15)));
        putClientProperty(FlatClientProperties.STYLE, "arc:10;");

        // === Frame for video ===
        cameraPanel = new JLabel();
        cameraPanel.setOpaque(true);
        cameraPanel.setBackground(Color.BLACK);
        cameraPanel.setPreferredSize(new Dimension(392, 220));
        cameraPanel.putClientProperty(FlatClientProperties.STYLE,
                "[light]background:darken(@background,15%);" +
                        "[dark]background:lighten(@background,15%)");

        // === Info panel ===
        infoPanel = new JPanel(new MigLayout("ins 5", "[grow]", "[]"));
        infoPanel.putClientProperty(FlatClientProperties.STYLE,
                "arc:10;" +
                        "[light]background:darken(@background,5%);" +
                        "[dark]background:lighten(@background,5%)");

        // === Add both panels ===
        add(cameraPanel, "grow, wrap");
        add(infoPanel, "growx, pushx");

        // === Popup menu ===
        JPopupMenu menu = new JPopupMenu();
        JMenuItem fullscreen = new JMenuItem("Fullscreen");
        JMenuItem delete = new JMenuItem("Delete");

        fullscreen.addActionListener(e -> {
            if (datacam != null) {
                FullscreenViewer viewer = new FullscreenViewer(datacam);
                viewer.setVisible(true);
            }
        });

        delete.addActionListener(e -> {
            Container parent = getParent();
            if (datacam != null){
                datacam.removeFrameListener(CamCell.this::updateFrame);
                CameraConfigStore.delete(datacam.getCameraName(), datacam.getStreamurl());
            }

            if (parent != null) {
                parent.remove(this);
                parent.revalidate();
                parent.repaint();
            }
        });

        menu.add(fullscreen);
        menu.add(delete);

        // Attach popup to video area instead of the parent
        cameraPanel.setComponentPopupMenu(menu);
    }

    public JLabel getCameraPanel() {
        return cameraPanel;
    }

    private void updateFrame(BufferedImage frame){
        if (frame == null) return;
            SwingUtilities.invokeLater(() -> {
            Image scaled = frame.getScaledInstance(
                    cameraPanel.getWidth(), cameraPanel.getHeight(), Image.SCALE_SMOOTH);
            cameraPanel.setIcon(new ImageIcon(scaled));
        });
    }

    public void setCameraData(Camera data) {
        datacam = data;

        JLabel camName = new JLabel("Camera " + data.getCameraName());
        infoPanel.add(camName);

        datacam.addFrameListener(this::updateFrame);
    }
}