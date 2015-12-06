/*
 * OthelloSwingFrame.java
 *
 */

package SwingFrame;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.plaf.metal.*;
import javax.swing.plaf.metal.MetalLookAndFeel;

import Engine.*;

/**
 ****************************************************************
 ****************************************************************
 * A Metal Look and Feel theme that sets the font size for all Look and
 * Feel fonts.
 * @author  Mats Luthman
 ****************************************************************
 ****************************************************************/

class LargeFontsTheme extends DefaultMetalTheme
{
    int m_fontSize;


    public LargeFontsTheme(int fontSize)
    {
        m_fontSize = fontSize;

        m_ControlTextFont =
                new FontUIResource("Dialog", Font.PLAIN, m_fontSize);
        m_MenuTextFont =
                new FontUIResource("Dialog", Font.PLAIN, m_fontSize);
        m_SubTextFont =
                new FontUIResource("Dialog", Font.PLAIN, m_fontSize);
        m_SystemTextFont =
                new FontUIResource("Dialog", Font.PLAIN, m_fontSize);
        m_UserTextFont =
                new FontUIResource("Dialog", Font.PLAIN, m_fontSize);
        m_WindowTextFont =
                new FontUIResource("Dialog", Font.PLAIN, m_fontSize);
    }


    public String getName()
    {
        return "LargeFontsTheme";
    }

    public FontUIResource getControlTextFont()
    {
        return m_ControlTextFont;
    }


    public FontUIResource getMenuTextFont()
    {
        return m_MenuTextFont;
    }


    public FontUIResource getSubTextFont()
    {
        return m_SubTextFont;
    }


    public FontUIResource getSystemTextFont()
    {
        return m_SystemTextFont;
    }


    public FontUIResource getUserTextFont()
    {
        return m_UserTextFont;
    }


    public FontUIResource getWindowTextFont()
    {
        return m_WindowTextFont;
    }


    private FontUIResource m_ControlTextFont;
    private FontUIResource m_MenuTextFont;
    private FontUIResource m_SubTextFont;
    private FontUIResource m_SystemTextFont;
    private FontUIResource m_UserTextFont;
    private FontUIResource m_WindowTextFont;
}


/**
 ****************************************************************
 ****************************************************************
 * The component in which the board is drawn.
 * @author  Mats Luthman
 ****************************************************************
 ****************************************************************/

class BoardDrawingArea extends javax.swing.JComponent
{
    private CommandInterface m_CommandInterface;


    /**
     ****************************************************************
     * Constructor.
     * @author  Mats Luthman
     ****************************************************************/

    BoardDrawingArea(CommandInterface commandInterface)
    {
        m_CommandInterface = commandInterface;
    }


    /**
     ****************************************************************
     * Returns the size of a square on the board.
     * @author  Mats Luthman
     ****************************************************************/

    public int getSquareSize()
    {
        int componentSize = getHeight();
        if (getWidth() < componentSize) componentSize = getWidth();

        return componentSize / 10;
    }


    /**
     ****************************************************************
     * Returns the square x coordinate of the square that contains the
     * point given by the parameters. If the point is not in a square
     * on the board, zero is returned.
     * @author  Mats Luthman
     ****************************************************************/

    public int getSquareXFromPosition(int x, int y)
    {
        int xSquare = x / getSquareSize();

        if (xSquare < 1 || xSquare > 8) xSquare = 0;

        return xSquare;
    }


    /**
     ****************************************************************
     * Returns the square y coordinate of the square that contains the
     * point given by the parameters. If the point is not in a square
     * on the board, zero is returned.
     * @author  Mats Luthman
     ****************************************************************/

    public int getSquareYFromPosition(int x, int y)
    {
        int ySquare = y / getSquareSize();

        if (ySquare < 1 || ySquare > 8) ySquare = 0;

        return ySquare;
    }


    /**
     ****************************************************************
     * Draws the Othello board and the pieces on it.
     * @author  Mats Luthman
     ****************************************************************/

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        Rectangle clipRect = new Rectangle();
        g.getClipBounds(clipRect);

        g.setColor(Color.green);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.black);

        int squareSize = getSquareSize();

        paintSquare(g, 0, 0, m_CommandInterface.GetWhoseTurn(), squareSize/2);

        for (int x=1; x < 9; x++)
            for (int y=1; y < 9; y++)
                paintSquare(g, x, y, m_CommandInterface.GetSquare(x, y), squareSize);

        for (int i=0; i < 9; i++)
        {
            g.drawLine(squareSize * (i + 1), squareSize,
                    squareSize * (i + 1), squareSize * 9);

            g.drawLine(squareSize, squareSize * (i + 1),
                    squareSize * 9, squareSize * (i + 1));
        }
    }


    /**
     ****************************************************************
     * Draws the piece, if any, on the square given by the coordinate
     * parameters.
     * @param g The grapics context used for drawing.
     * @param x The x coordinate of the upper left corner of the square to paint.
     * @param y The y coordinate of the upper left corner of the square to paint.
     * @param player 1 means a white disc, 2 means a black disc and 0 an empty
     * square.
     * @param squareSize The size of the square to paint.
     * @author  Mats Luthman
     ****************************************************************/

    private void paintSquare(Graphics g, int x, int y, int player, int squareSize)
    {
        int boardX = (int)(squareSize * (x + 0.05) + 0.5);
        int boardY = (int)(squareSize * (y + 0.05) + 0.5);
        int size = (int) (squareSize * 0.9 + 0.5);

        if (x == 0) boardX += squareSize / 2;
        if (y == 0) boardY += squareSize / 2;

        switch (player)
        {
            case Score.WHITE:
                g.setColor(Color.white);
                g.fillOval(boardX, boardY, size, size);
                g.setColor(Color.black);
                g.drawOval(boardX, boardY, size, size);
                break;

            case Score.BLACK:
                g.setColor(Color.black);
                g.fillOval(boardX - 1, boardY - 1, size + 3, size + 3);
                break;

            default:
        }

        g.setColor(Color.black);
    }
}


/**
 ****************************************************************
 ****************************************************************
 * The frame containing the GUI of the Othello program.
 * @author  Mats Luthman
 ****************************************************************
 ****************************************************************/

public class OthelloSwingFrame extends javax.swing.JFrame
        implements CommandInterfaceListener, Runnable
{

    /**
     ****************************************************************
     * Creates new form OthelloSwingFrame
     ****************************************************************/

    public OthelloSwingFrame(boolean createdByApplet, int mode)
    {
        m_createdByApplet = createdByApplet;

        if (createdByApplet)
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        else
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        m_ai_player = mode;

        // Set the kind of AI player
        m_CommandInterface = new CommandInterface(m_ai_player);

        try
        {
            getResources();
        }
        catch(Exception e)
        {
            // Default values are set

            // Should maybe set locale according to default values (english):
            // setLocale(new Locale("en"));
            // If so, it should also be done for dialogs et.c.
        }

        initComponents();

        jLabelLevel.setText(m_sLevelLabel + " " + m_CommandInterface.GetLevel());

        int boardSize = getIntProperty("Othello.BoardSize", 425);

        m_boardDrawingArea = new BoardDrawingArea(m_CommandInterface);
        m_boardDrawingArea.setMinimumSize(new java.awt.Dimension(300, 300));
        m_boardDrawingArea.
                setPreferredSize(new java.awt.Dimension(boardSize, boardSize));
        getContentPane().add(m_boardDrawingArea, java.awt.BorderLayout.CENTER);

        m_boardDrawingArea.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                mousePressedEvent(evt);
            }
        });

        setTitle(m_sTitle);

        InstructionsFrame.pack();

        pack();
    }


    /**
     ****************************************************************
     * Called when a move computation is finished.
     ****************************************************************/

    public void ComputationFinished(Move m)
    {
        if (m != null && m.GetPlayer() == m_CommandInterface.GetWhoseTurn())
        {
            EventQueue.invokeLater(this);

            m_CommandInterface.ComputeMove(this);
        }
        else
        {
            m_computing = false;

            EventQueue.invokeLater(this);
        }
    }


    /**
     ****************************************************************
     * Calls UpdateAll(). Must be used when updates are made from the
     * move caclulation thread.
     ****************************************************************/

    public void run()
    {
        if (! m_computing)
            m_boardDrawingArea.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        TextPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        jPanelCalculating.setVisible(false);

        UpdateAll();
    }


    /**
     ****************************************************************
     * Updates board and textpanel and calls repaint for these.
     ****************************************************************/

    public void UpdateAll()
    {
        jLabelWhiteScore.setText(String.valueOf(m_CommandInterface.GetScoreWhite()));
        jLabelBlackScore.setText(String.valueOf(m_CommandInterface.GetScoreBlack()));

        if (m_CommandInterface.GetLastMove() != null)
            jLabelLastMove.setText(m_sLastMove + " " +
                    m_CommandInterface.GetLastMove());
        else
            jLabelLastMove.setText(" ");

        jLabelLevel.setText(m_sLevelLabel + " " + m_CommandInterface.GetLevel());

        m_boardDrawingArea.repaint();
        TextPanel.repaint();
    }


    /**
     ****************************************************************
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     ****************************************************************/

    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        InstructionsFrame = new javax.swing.JFrame();
        InstructionsScrollPane = new javax.swing.JScrollPane();
        InstructionsTextArea = new javax.swing.JTextArea();
        OKButtonPanel = new javax.swing.JPanel();
        CloseButton = new javax.swing.JButton();
        AboutPanel = new javax.swing.JPanel();
        AboutText = new javax.swing.JLabel();
        AboutVersion = new javax.swing.JLabel();
        AboutAuthor = new javax.swing.JLabel();
        TextPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabelBlack = new javax.swing.JLabel();
        jLabelWhite = new javax.swing.JLabel();
        jLabelBlackScore = new javax.swing.JLabel();
        jLabelWhiteScore = new javax.swing.JLabel();
        jPanelPadding1 = new javax.swing.JPanel();
        jLabelPadding1 = new javax.swing.JLabel();
        jPanelPadding2 = new javax.swing.JPanel();
        jLabelPadding2 = new javax.swing.JLabel();
        jLabelPadding3 = new javax.swing.JLabel();
        jLabelLastMove = new javax.swing.JLabel();
        jLabelPadding4 = new javax.swing.JLabel();
        jLabelLevel = new javax.swing.JLabel();
        jPanelCalculating = new javax.swing.JPanel();
        jLabelCalculating = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        CommandsMenu = new javax.swing.JMenu();
        ChangeSides = new javax.swing.JMenuItem();
        Undo = new javax.swing.JMenuItem();
        TakeBack = new javax.swing.JMenuItem();
        ChangeLevel = new javax.swing.JMenuItem();
        NewGame = new javax.swing.JMenuItem();
        InterruptComputation = new javax.swing.JMenuItem();
        Quit = new javax.swing.JMenuItem();
        HelpMenu = new javax.swing.JMenu();
        Instructions = new javax.swing.JMenuItem();
        About = new javax.swing.JMenuItem();

        InstructionsFrame.setTitle(m_sInstructionsDialogTitle);
        InstructionsTextArea.setColumns(65);
        InstructionsTextArea.setEditable(false);
        InstructionsTextArea.setLineWrap(true);
        InstructionsTextArea.setRows(30);
        InstructionsTextArea.setWrapStyleWord(true);
        InstructionsTextArea.setText(m_sInstructionsText);
        InstructionsScrollPane.setViewportView(InstructionsTextArea);

        InstructionsFrame.getContentPane().add(InstructionsScrollPane, java.awt.BorderLayout.CENTER);

        OKButtonPanel.setLayout(new java.awt.GridBagLayout());

        CloseButton.setText("jButton1");
        CloseButton.setText(m_sClose);
        CloseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                InstructionsCloseActionPerformed(evt);
            }
        });

        OKButtonPanel.add(CloseButton, new java.awt.GridBagConstraints());

        InstructionsFrame.getContentPane().add(OKButtonPanel, java.awt.BorderLayout.SOUTH);

        AboutPanel.setLayout(new java.awt.GridBagLayout());

        AboutText.setText("jLabel1");
        AboutText.setText(m_sAboutText);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        AboutPanel.add(AboutText, gridBagConstraints);

        AboutVersion.setText("jLabel2");
        AboutVersion.setText(m_sAboutVersion);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        AboutPanel.add(AboutVersion, gridBagConstraints);

        AboutAuthor.setText("jLabel3");
        AboutAuthor.setText(m_sAboutAuthor);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        AboutPanel.add(AboutAuthor, gridBagConstraints);

        setTitle("Othello");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        TextPanel.setLayout(new java.awt.BorderLayout());

        TextPanel.setBorder(new javax.swing.border.EtchedBorder());
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabelBlack.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelBlack.setText("Black:");
        jLabelBlack.setText(m_sBlack);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        jPanel1.add(jLabelBlack, gridBagConstraints);

        jLabelWhite.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelWhite.setText("White:");
        jLabelWhite.setText(m_sWhite);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        jPanel1.add(jLabelWhite, gridBagConstraints);

        jLabelBlackScore.setText("2");
        jLabelBlackScore.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        jPanel1.add(jLabelBlackScore, gridBagConstraints);

        jLabelWhiteScore.setText("2");
        jLabelWhiteScore.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        jPanel1.add(jLabelWhiteScore, gridBagConstraints);

        jPanelPadding1.setLayout(new java.awt.GridBagLayout());

        jPanelPadding1.setPreferredSize(new Dimension(jLabelBlackScore.getPreferredSize().width * 12, jLabelBlackScore.getPreferredSize().height));
        jLabelPadding1.setText("                   ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        jPanelPadding1.add(jLabelPadding1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        jPanel1.add(jPanelPadding1, gridBagConstraints);

        jPanelPadding2.setLayout(new java.awt.GridBagLayout());

        jPanelPadding2.setPreferredSize(new Dimension(jLabelBlackScore.getPreferredSize().width * 12, jLabelBlackScore.getPreferredSize().height));
        jLabelPadding2.setText("                   ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        jPanelPadding2.add(jLabelPadding2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        jPanel1.add(jPanelPadding2, gridBagConstraints);

        jLabelPadding3.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        jPanel1.add(jLabelPadding3, gridBagConstraints);

        jLabelLastMove.setText(" ");
        jLabelLastMove.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        jPanel1.add(jLabelLastMove, gridBagConstraints);

        jLabelPadding4.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        jPanel1.add(jLabelPadding4, gridBagConstraints);

        jLabelLevel.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        jPanel1.add(jLabelLevel, gridBagConstraints);

        TextPanel.add(jPanel1, java.awt.BorderLayout.NORTH);

        jPanelCalculating.setBackground(new java.awt.Color(255, 0, 0));
        jPanelCalculating.setVisible(false);
        jLabelCalculating.setText("Calculating");
        jLabelCalculating.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabelCalculating.setText(m_sComputing);
        jPanelCalculating.add(jLabelCalculating);

        TextPanel.add(jPanelCalculating, java.awt.BorderLayout.SOUTH);

        getContentPane().add(TextPanel, java.awt.BorderLayout.EAST);

        CommandsMenu.setText("Menu");
        CommandsMenu.setText(m_sCommands);
        CommandsMenu.setMnemonic(m_cCommandsMnemonic);
        CommandsMenu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                MenuSelected(evt);
            }
        });

        ChangeSides.setText("Item");
        ChangeSides.setText(m_sChangeSides);
        ChangeSides.setAccelerator(KeyStroke.getKeyStroke('S', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        ChangeSides.setMnemonic(m_cChangeSidesMnemonic);
        if (m_sChangeSidesToolTip.length() > 0) ChangeSides.setToolTipText(m_sChangeSidesToolTip);

        ChangeSides.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChangeSidesActionPerformed(evt);
            }
        });

        CommandsMenu.add(ChangeSides);

        Undo.setText("Item");
        Undo.setText(m_sUndo);
        Undo.setAccelerator(KeyStroke.getKeyStroke('Z', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        Undo.setMnemonic(m_cUndoMnemonic);
        if (m_sUndoToolTip.length() > 0) Undo.setToolTipText(m_sUndoToolTip);
        Undo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UndoActionPerformed(evt);
            }
        });

        CommandsMenu.add(Undo);

        TakeBack.setText("Item");
        TakeBack.setText(m_sTakeBack);
        TakeBack.setAccelerator(KeyStroke.getKeyStroke('B', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        TakeBack.setMnemonic(m_cTakeBackMnemonic);
        if (m_sTakeBackToolTip.length() > 0) TakeBack.setToolTipText(m_sTakeBackToolTip);

        TakeBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TakeBackActionPerformed(evt);
            }
        });

        CommandsMenu.add(TakeBack);

        ChangeLevel.setText("Item");
        ChangeLevel.setText(m_sChangeLevel);
        ChangeLevel.setAccelerator(KeyStroke.getKeyStroke('L', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        ChangeLevel.setMnemonic(m_cChangeLevelMnemonic);
        if (m_sChangeLevelToolTip.length() > 0) ChangeLevel.setToolTipText(m_sChangeLevelToolTip);

        ChangeLevel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChangeLevelActionPerformed(evt);
            }
        });

        CommandsMenu.add(ChangeLevel);

        NewGame.setText("Item");
        NewGame.setText(m_sNewGame);
        NewGame.setAccelerator(KeyStroke.getKeyStroke('G', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        NewGame.setMnemonic(m_cNewGameMnemonic);
        if (m_sNewGameToolTip.length() > 0) NewGame.setToolTipText(m_sNewGameToolTip);

        NewGame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NewGameActionPerfomed(evt);
            }
        });

        CommandsMenu.add(NewGame);

        InterruptComputation.setText("Item");
        InterruptComputation.setText(m_sInterruptComputation);
        InterruptComputation.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
        InterruptComputation.setMnemonic(m_cInterruptComputationMnemonic);
        if (m_sInterruptComputationToolTip.length() > 0) InterruptComputation.setToolTipText(m_sInterruptComputationToolTip);

        InterruptComputation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                InterruptComputationActionPerformed(evt);
            }
        });

        CommandsMenu.add(InterruptComputation);

        Quit.setText("Item");
        Quit.setText(m_sQuit);
        Quit.setAccelerator(KeyStroke.getKeyStroke('Q', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        Quit.setMnemonic(m_cQuitMnemonic);
        if (m_sQuitToolTip.length() > 0) Quit.setToolTipText(m_sQuitToolTip);

        Quit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                QuitHandler(evt);
            }
        });

        CommandsMenu.add(Quit);

        jMenuBar1.add(CommandsMenu);

        HelpMenu.setText("Menu");
        HelpMenu.setText(m_sHelp);
        HelpMenu.setMnemonic(m_cHelpMnemonic);
        Instructions.setText("Item");
        Instructions.setText(m_sInstructions);
        Instructions.setMnemonic(m_cInstructionsMnemonic);
        Instructions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                InstructionsActionPerformed(evt);
            }
        });

        HelpMenu.add(Instructions);

        About.setText("Item");
        About.setText(m_sAbout);
        About.setMnemonic(m_cAboutMnemonic);
        About.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AboutActionPerformed(evt);
            }
        });

        HelpMenu.add(About);

        jMenuBar1.add(HelpMenu);

        setJMenuBar(jMenuBar1);

        pack();
    }
    // </editor-fold>//GEN-END:initComponents


    /**
     ****************************************************************
     * Called when the about menu item is selected.
     * @author  Mats Luthman
     ****************************************************************/

    private void AboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AboutActionPerformed
        JOptionPane.showMessageDialog(this, AboutPanel, m_sAboutDialogTitle,
                JOptionPane.PLAIN_MESSAGE, null);

    }//GEN-LAST:event_AboutActionPerformed

    /**
     ****************************************************************
     * Called when the close button in the instructions windows is pressed.
     * @author  Mats Luthman
     ****************************************************************/

    private void InstructionsCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InstructionsCloseActionPerformed
        InstructionsFrame.setVisible(false);
    }//GEN-LAST:event_InstructionsCloseActionPerformed


    /**
     ****************************************************************
     * Called when the instructions menu item is selected.
     * @author  Mats Luthman
     ****************************************************************/

    private void InstructionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InstructionsActionPerformed
        InstructionsFrame.setVisible(true);
    }//GEN-LAST:event_InstructionsActionPerformed


    /**
     ****************************************************************
     * Called when a mouse button is pressed in the BoardDrawingArea.
     * @author  Mats Luthman
     ****************************************************************/

    private void mousePressedEvent(java.awt.event.MouseEvent evt)
    {
        int xSquare =
                m_boardDrawingArea.getSquareXFromPosition(evt.getX(), evt.getY());

        int ySquare =
                m_boardDrawingArea.getSquareYFromPosition(evt.getY(), evt.getY());

        if (xSquare > 0 && xSquare < 9 && ySquare > 0 && ySquare < 9)
        {
            if (m_CommandInterface.MakeMoveIsPossible(xSquare, ySquare))
            {
                int player = m_CommandInterface.GetWhoseTurn();

                m_CommandInterface.MakeMove(xSquare, ySquare);

                if (m_CommandInterface.MoveIsPossible() &&
                        m_CommandInterface.GetWhoseTurn() != player)
                {
                    m_boardDrawingArea.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                    TextPanel.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                    jPanelCalculating.setVisible(true);

                    UpdateAll();

//        Not necessary when worker thread is running at low priority:
//          m_boardDrawingArea.paintImmediately(m_boardDrawingArea.getBounds());
//          TextPanel.paintImmediately(m_boardDrawingArea.getBounds());


                    m_computing = true;

                    m_CommandInterface.ComputeMove(this);
                }
                else
                    UpdateAll();
            }
        }
    }

    /**
     ****************************************************************
     * Called when the interrupt calculation menu item is selected.
     * @author  Mats Luthman
     ****************************************************************/

    private void InterruptComputationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InterruptComputationActionPerformed
        m_CommandInterface.InterruptComputation();
    }//GEN-LAST:event_InterruptComputationActionPerformed

    /**
     ****************************************************************
     * Called when the new game menu item is selected.
     * @author  Mats Luthman
     ****************************************************************/

    private void NewGameActionPerfomed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NewGameActionPerfomed
        if (m_CommandInterface.NewGameIsPossible())
        {
            m_CommandInterface.NewGame();
            UpdateAll();
        }
    }//GEN-LAST:event_NewGameActionPerfomed

    /**
     ****************************************************************
     * Called when the take back menu item is selected.
     * @author  Mats Luthman
     ****************************************************************/

    private void TakeBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TakeBackActionPerformed
        if (m_CommandInterface.TakeBackIsPossible())
        {
            m_CommandInterface.TakeBack();
            UpdateAll();
        }
    }//GEN-LAST:event_TakeBackActionPerformed

    /**
     ****************************************************************
     * Called when the undo menu item is selected.
     * @author  Mats Luthman
     ****************************************************************/

    private void UndoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UndoActionPerformed
        if (m_CommandInterface.UndoIsPossible())
        {
            m_CommandInterface.Undo();
            UpdateAll();
        }
    }//GEN-LAST:event_UndoActionPerformed

    /**
     ****************************************************************
     * Called when the menu is selected.
     * @author  Mats Luthman
     ****************************************************************/

    private void MenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_MenuSelected
        ChangeSides.setEnabled(m_CommandInterface.MoveIsPossible());
        Undo.setEnabled(m_CommandInterface.UndoIsPossible());
        TakeBack.setEnabled(m_CommandInterface.TakeBackIsPossible());
        ChangeLevel.setEnabled(m_CommandInterface.SetLevelIsPossible());
        NewGame.setEnabled(m_CommandInterface.NewGameIsPossible());
        InterruptComputation.setEnabled(m_CommandInterface.InterruptComputationIsPossible());
    }//GEN-LAST:event_MenuSelected

    /**
     ****************************************************************
     * Called when the quit  menu item is selected.
     * @author  Mats Luthman
     ****************************************************************/

    private void QuitHandler(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_QuitHandler
        if (m_createdByApplet)
            dispose();
        else
            System.exit(0);
    }//GEN-LAST:event_QuitHandler

    /**
     ****************************************************************
     * Called when the change level menu item is selected.
     * @author  Mats Luthman
     ****************************************************************/

    private void ChangeLevelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChangeLevelActionPerformed
        String[] options = {
                "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" };

        String returnValue = (String)
                JOptionPane.showInputDialog(this,
                        m_sLevel, m_sLevelDialogTitle,
                        JOptionPane.PLAIN_MESSAGE, null, options,
                        options[m_CommandInterface.GetLevel()]);

        if (returnValue != null && m_CommandInterface.SetLevelIsPossible())
        {
            m_CommandInterface.SetLevel(Integer.parseInt(returnValue));
            UpdateAll();
        }
    }//GEN-LAST:event_ChangeLevelActionPerformed

    /**
     ****************************************************************
     * Called when the change sides  menu item is selected.
     * @author  Mats Luthman
     ****************************************************************/

    private void ChangeSidesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChangeSidesActionPerformed
        if (m_CommandInterface.MoveIsPossible())
        {
            m_boardDrawingArea.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            TextPanel.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            jPanelCalculating.setVisible(true);

            UpdateAll();

            m_CommandInterface.ComputeMove(this);
        }
    }//GEN-LAST:event_ChangeSidesActionPerformed

    /**
     ****************************************************************
     * Exits the Application
     * @author  Mats Luthman
     ****************************************************************/

    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        if (m_createdByApplet)
            dispose();
        else
            System.exit(0);
    }//GEN-LAST:event_exitForm


    /**
     ****************************************************************
     * Returns a resource value converted to an integer.
     * @param name Property name
     * @param defaultValue Returned if property was not found
     * @return The property value
     * @author  Mats Luthman
     ****************************************************************/

    static int getIntProperty(String name, int defaultValue)
    {
        String property = null;

        try
        {
            property = System.getProperty(name);
        }
        catch (Exception e) { }

        if (property != null)
            try
            {
                defaultValue = Integer.parseInt(property);
            } catch(Exception e) { }

        return defaultValue;
    }


    /**
     ****************************************************************
     * Retrieves one string value from the resource bundle.
     * @author  Mats Luthman
     ****************************************************************/

    String getOneResource(ResourceBundle resourceBundle,
                          String name, String defaultValue)
    {
        String returnValue = defaultValue;

        try
        {
            returnValue = resourceBundle.getString(name);
        }
        catch(Exception e)
        {
        }

        return returnValue;
    }

    /**
     ****************************************************************
     * Retrieves string values from the Othello resource bundle.
     * @author  Mats Luthman
     ****************************************************************/

    void getResources()
    {
        ResourceBundle resourceBundle =
                ResourceBundle.getBundle("Othello.SwingFrame.Othello");

        m_sTitle = getOneResource(resourceBundle, "Title", "Othello");
        m_sCommands = getOneResource(resourceBundle, "Commands", "Commands");
        m_sChangeSides = getOneResource(resourceBundle, "ChangeSides", "Change Sides");
        m_sUndo = getOneResource(resourceBundle, "Undo", "Undo");
        m_sTakeBack = getOneResource(resourceBundle, "TakeBack", "Take Back");
        m_sChangeLevel = getOneResource(resourceBundle, "ChangeLevel", "Set Level...");
        m_sNewGame = getOneResource(resourceBundle, "NewGame", "New game");
        m_sInterruptComputation = getOneResource(resourceBundle, "InterruptComputation", "Interrupt Computation");
        m_sQuit = getOneResource(resourceBundle, "Quit", "Quit");

        m_sChangeSidesToolTip = getOneResource(resourceBundle, "ChangeSidesToolTip", "Let the program make the next move");
        m_sUndoToolTip = getOneResource(resourceBundle, "UndoToolTip", "Take back your last move");
        m_sTakeBackToolTip = getOneResource(resourceBundle, "TakeBackToolTip", "Take back the last single move");
        m_sChangeLevelToolTip = getOneResource(resourceBundle, "ChangeLevelToolTip", "");
        m_sNewGameToolTip = getOneResource(resourceBundle, "NewGameToolTip", "");
        m_sInterruptComputationToolTip = getOneResource(resourceBundle, "InterruptComputationToolTip", "");
        m_sQuitToolTip = getOneResource(resourceBundle, "QuitToolTip", "");

        String s;
        s = getOneResource(resourceBundle, "CommandsMnemonic", "C");
        m_cCommandsMnemonic = s.charAt(0);
        s = getOneResource(resourceBundle, "ChangeSidesMnemonic", "S");
        m_cChangeSidesMnemonic = s.charAt(0);
        s = getOneResource(resourceBundle, "UndoMnemonic", "U");
        m_cUndoMnemonic = s.charAt(0);
        s = getOneResource(resourceBundle, "TakeBackMnemonic", "B");
        m_cTakeBackMnemonic = s.charAt(0);
        s = getOneResource(resourceBundle, "ChangeLevelMnemonic", "L");
        m_cChangeLevelMnemonic = s.charAt(0);
        s = getOneResource(resourceBundle, "NewGameMnemonic", "G");
        m_cNewGameMnemonic = s.charAt(0);
        s = getOneResource(resourceBundle, "InterruptComputationMnemonic", "I");
        m_cInterruptComputationMnemonic = s.charAt(0);
        s = getOneResource(resourceBundle, "QuitMnemonic", "Q");
        m_cQuitMnemonic = s.charAt(0);

        m_sHelp = getOneResource(resourceBundle, "Help", "Help");
        m_sInstructions = getOneResource(resourceBundle, "Instructions", "Instructions...");
        m_sAbout = getOneResource(resourceBundle, "About", "About...");

        s = getOneResource(resourceBundle, "HelpMnemonic", "H");
        m_cHelpMnemonic = s.charAt(0);
        s = getOneResource(resourceBundle, "InstructionsMnemonic", "I");
        m_cInstructionsMnemonic = s.charAt(0);
        s = getOneResource(resourceBundle, "AboutMnemonic", "A");
        m_cAboutMnemonic = s.charAt(0);

        m_sWhite = getOneResource(resourceBundle, "White", "White:");
        m_sBlack = getOneResource(resourceBundle, "Black", "Black:");
        m_sLastMove = getOneResource(resourceBundle, "LastMove", "");
        m_sLevelLabel = getOneResource(resourceBundle, "LevelLabel", "Level:");
        m_sNone = getOneResource(resourceBundle, "None", "none");
        m_sComputing = getOneResource(resourceBundle, "Computing", "Computing");
        m_sLevel = getOneResource(resourceBundle, "Level", "Level (analysis depth):");
        m_sLevelDialogTitle = getOneResource(resourceBundle, "LevelDialogTitle", "Othello, Level");

        m_sAboutDialogTitle = getOneResource(resourceBundle, "AboutDialogTitle", "About Othello");
        m_sAboutText = getOneResource(resourceBundle, "AboutText", "Othello");
        m_sAboutVersion = getOneResource(resourceBundle, "AboutVersion", "");
        m_sAboutAuthor = getOneResource(resourceBundle, "AboutAuthor", "Mats Luthman");
        m_sInstructionsDialogTitle = getOneResource(resourceBundle, "InstructionsDialogTitle", "Othello, instructions");
        m_sInstructionsText = getOneResource(resourceBundle, "InstructionsText", "");
        m_sClose = getOneResource(resourceBundle, "Close", "Close");
    }


    /**
     ****************************************************************
     * The main program. Starts an instance of OthelloSwingFrame.
     * @param args the command line arguments
     * @author  Mats Luthman
     ****************************************************************/

    public static void main(String args[])
    {
        try
        {
//      javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
//      javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
//      javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
        }
        catch(Exception e)
        {
        }

        int fontSize = getIntProperty("Othello.FontSize", 0);

        if (fontSize != 0)
            MetalLookAndFeel.setCurrentTheme(new LargeFontsTheme(fontSize));

//    javax.swing.UIManager.put("Button.font", new Font("Dialog", Font.PLAIN, 24));
//    javax.swing.UIManager.put("RadioButton.font", new Font("Dialog", Font.PLAIN, 24));
//    javax.swing.UIManager.put("CheckBox.font", new Font("Dialog", Font.PLAIN, 24));
//    javax.swing.UIManager.put("MenuItem.font", new Font("Dialog", Font.PLAIN, 24));

        new OthelloSwingFrame(false, 1).setVisible(true);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem About;
    private javax.swing.JLabel AboutAuthor;
    private javax.swing.JPanel AboutPanel;
    private javax.swing.JLabel AboutText;
    private javax.swing.JLabel AboutVersion;
    private javax.swing.JMenuItem ChangeLevel;
    private javax.swing.JMenuItem ChangeSides;
    private javax.swing.JButton CloseButton;
    private javax.swing.JMenu CommandsMenu;
    private javax.swing.JMenu HelpMenu;
    private javax.swing.JMenuItem Instructions;
    private javax.swing.JFrame InstructionsFrame;
    private javax.swing.JScrollPane InstructionsScrollPane;
    private javax.swing.JTextArea InstructionsTextArea;
    private javax.swing.JMenuItem InterruptComputation;
    private javax.swing.JMenuItem NewGame;
    private javax.swing.JPanel OKButtonPanel;
    private javax.swing.JMenuItem Quit;
    private javax.swing.JMenuItem TakeBack;
    private javax.swing.JPanel TextPanel;
    private javax.swing.JMenuItem Undo;
    private javax.swing.JLabel jLabelBlack;
    private javax.swing.JLabel jLabelBlackScore;
    private javax.swing.JLabel jLabelCalculating;
    private javax.swing.JLabel jLabelLastMove;
    private javax.swing.JLabel jLabelLevel;
    private javax.swing.JLabel jLabelPadding1;
    private javax.swing.JLabel jLabelPadding2;
    private javax.swing.JLabel jLabelPadding3;
    private javax.swing.JLabel jLabelPadding4;
    private javax.swing.JLabel jLabelWhite;
    private javax.swing.JLabel jLabelWhiteScore;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanelCalculating;
    private javax.swing.JPanel jPanelPadding1;
    private javax.swing.JPanel jPanelPadding2;
    // End of variables declaration//GEN-END:variables

    CommandInterface m_CommandInterface; // = new CommandInterface();

    BoardDrawingArea m_boardDrawingArea;

    boolean m_computing = false;
    boolean m_createdByApplet = false;

    // String resources:

    String m_sTitle = "Othello";
    String m_sCommands = "Commands";
    String m_sChangeSides = "Change Sides";
    String m_sUndo = "Undo";
    String m_sTakeBack = "Take Back";
    String m_sChangeLevel = "Set Level...";
    String m_sNewGame = "New game";
    String m_sInterruptComputation = "Interrupt Computation";
    String m_sQuit = "Quit";
    char m_cCommandsMnemonic = 'C';
    char m_cChangeSidesMnemonic = 'S';
    char m_cUndoMnemonic = 'U';
    char m_cTakeBackMnemonic = 'B';
    char m_cChangeLevelMnemonic = 'L';
    char m_cNewGameMnemonic = 'G';
    char m_cInterruptComputationMnemonic = 'I';
    char m_cQuitMnemonic = 'Q';
    String m_sChangeSidesToolTip = "Let the program make the next move";
    String m_sUndoToolTip = "Take back your last move";
    String m_sTakeBackToolTip = "Take back the last single move";
    String m_sChangeLevelToolTip = "";
    String m_sNewGameToolTip = "";
    String m_sInterruptComputationToolTip = "";
    String m_sQuitToolTip = "";
    String m_sHelp = "Help";
    String m_sInstructions = "Instructions...";
    String m_sAbout = "About...";
    char m_cHelpMnemonic = 'H';
    char m_cInstructionsMnemonic = 'I';
    char m_cAboutMnemonic = 'A';
    String m_sWhite = "White:";
    String m_sBlack = "Black:";
    String m_sLastMove = "";
    String m_sLevelLabel = "Level:";
    String m_sNone = "none";
    String m_sComputing = "Computing";
    String m_sLevel = "Level (analysis depth):";
    String m_sLevelDialogTitle = "Othello, Level";
    String m_sAboutDialogTitle = "About Othello";
    String m_sAboutText = "Othello";
    String m_sAboutVersion = "";
    String m_sAboutAuthor = "Mats Luthman";
    String m_sInstructionsDialogTitle = "Othello, instructions";
    String m_sInstructionsText = "";
    String m_sClose = "Close";

    // AI player
    int m_ai_player = 1;
}