package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;

public class ventanaArkanoid extends JFrame {

    private JuegoPanel panel;

    public ventanaArkanoid() {
        setTitle("Arkanoid");
        setSize(600, 500);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        panel = new JuegoPanel();
        add(panel);
        addKeyListener(panel); // panel maneja las teclas
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ventanaArkanoid());
    }
}
