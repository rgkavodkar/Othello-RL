/*
 * OthelloSwingApplet.java
 *
 */

package SwingFrame;

import java.applet.*;
import java.awt.*;
import java.awt.event.ActionEvent;
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
        JButton minimaxVsMinimaxButton = new JButton("Minimax Bot vs Minimax Bot");

        getContentPane().add(minMaxButton);
        getContentPane().add(positionalButton);
        getContentPane().add(nnButton);
        getContentPane().add(minimaxVsMinimaxButton);

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

        minimaxVsMinimaxButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                botGameMode(evt, 1);
            }
        });
    }

    private void botGameMode(ActionEvent event, int mode) {
        OthelloSwingBotFrame botModeFrame = null;
        if(mode == 1) {
            botModeFrame = new OthelloSwingBotFrame(true, 1);
        } else if (mode == 2){
            botModeFrame = new OthelloSwingBotFrame(true, 1);
        }
        botModeFrame.setVisible(true);
    }

    private void buttonPushed(java.awt.event.ActionEvent evt, int mode)
    {
        OthelloSwingFrame humanModeFrame = null;
        if(mode == 1) {
            // Minimax player
            humanModeFrame = new OthelloSwingFrame(true, 1);
        } else if(mode == 2) {
            // Positional player
            humanModeFrame = new OthelloSwingFrame(true, 2);
        } else if(mode == 3) {
            // NeuralNet player
            humanModeFrame = new OthelloSwingFrame(true, 3);
        }
        humanModeFrame.setVisible(true);
    }
}