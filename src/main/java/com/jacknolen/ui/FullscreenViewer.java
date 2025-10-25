package com.jacknolen.ui;


import com.jacknolen.cam.Camera;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class FullscreenViewer extends JFrame {
    private final JLabel videoLabel = new JLabel();

    public FullscreenViewer(Camera camera) {
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());
        add(videoLabel, BorderLayout.CENTER);

        camera.addFrameListener(this::updateFrame);

        addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE) {
                    dispose();
                }
            }
        });

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                camera.removeFrameListener(FullscreenViewer.this::updateFrame);
            }
        });
    }

    private void updateFrame(BufferedImage frame) {
        if (frame == null) return;
        SwingUtilities.invokeLater(() -> {
            Dimension size = getSize();
            Image scaled = frame.getScaledInstance(size.width, size.height, Image.SCALE_SMOOTH);
            videoLabel.setIcon(new ImageIcon(scaled));
        });
    }
}