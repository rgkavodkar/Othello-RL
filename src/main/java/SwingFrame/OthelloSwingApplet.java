/*
 * OthelloSwingApplet.java
 *
 */

package SwingFrame;

import java.applet.*;
import java.awt.*;
import javax.swing.*;


/**
 * This is just an applet that creates an OthelloFrame object.
 */

public class OthelloSwingApplet extends JApplet
{
    public OthelloSwingApplet()
    {
    }


    public String getAppletInfo() { return "Name: Othello"; }


    public String[][] getParameterInfo()
    {
        String[][] info =
                {
                };
        return info;
    }


    public void init()
    {
        getContentPane().setLayout(new GridLayout(3, 1));
        JButton minMaxButton = new JButton("Play Othello vs Minimax Player");
        JButton positionalButton = new JButton("Play Othello vs Position Player");
        JButton nnButton = new JButton("Play Othello vs NeuralNet Player");

        getContentPane().add(minMaxButton);
        getContentPane().add(positionalButton);
        getContentPane().add(nnButton);

        minMaxButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                buttonPushed(evt, 1);
            }
        });

        positionalButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                buttonPushed(evt, 2);
            }
        });

        nnButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                buttonPushed(evt, 3);
            }
        });
    }


    private void buttonPushed(java.awt.event.ActionEvent evt, int mode)
    {
        OthelloSwingFrame f = null;
        if(mode == 1) {
            // Minimax player
            f = new OthelloSwingFrame(true, 1);
        } else if(mode == 2) {
            // Positional player
            f = new OthelloSwingFrame(true, 2);
        } else if(mode == 3) {
            // NeuralNet player
            f = new OthelloSwingFrame(true, 3);
        }
        f.setVisible(true);
    }
}