package com.wolf_pan;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JTextArea;

public class HintTextArea extends JTextArea {
    public HintTextArea(String hint, int rows) {
        super(hint, rows, 0);
        super.setForeground(Color.GRAY);
        super.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (getForeground() == Color.GRAY && hint.equals(getText())) {
                    setText(null);
                    setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (getText().equals("")) {
                    setText(hint);
                    setForeground(Color.GRAY);
                }
            }
        });
    }
}