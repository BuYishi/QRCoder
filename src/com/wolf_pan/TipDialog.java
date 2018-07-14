package com.wolf_pan;

import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class TipDialog extends JDialog {

    public TipDialog(JFrame owner, String tip, int width, int height) {
        super.setUndecorated(true);
        super.setSize(width, height);
        super.setLocationRelativeTo(owner);
        super.add(new JLabel("<html><font color=\"red\">" + tip + "</font></html>", JLabel.CENTER));
    }

    public void show(long duration) {
        setVisible(true);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                dispose();
            }
        }, duration);
    }
}
