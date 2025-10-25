package com.jacknolen;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.jacknolen.ui.UI;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
                FlatRobotoFont.install();
        FlatLaf.registerCustomDefaultsSource("themes");
        UIManager.put("defaultFont",new Font(FlatRobotoFont.FAMILY, Font.PLAIN,13));
        FlatMacDarkLaf.setup();
        EventQueue.invokeLater(() -> {
            try {
                new UI().setVisible(true);
                System.out.println("Interface setup");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        UIManager.put("Table.alternateRowColor", new Color(128, 128, 128,30));
    }
}