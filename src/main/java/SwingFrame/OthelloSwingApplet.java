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
        getContentPane().setLayout(new GridLayout(1, 1));
        JButton button = new JButton("Start Othello");

        getContentPane().add(button);

        button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                buttonPushed(evt);
            }
        });
    }


    private void buttonPushed(java.awt.event.ActionEvent evt)
    {
        OthelloSwingFrame f = new OthelloSwingFrame(true);
        f.setVisible(true);
    }
}