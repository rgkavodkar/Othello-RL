package SwingFrame;

/**
 * Created by Rakesh on 06-12-2015.
 */

import GameEngine.Interfaces.Commands;
import GameEngine.Score;

import java.awt.*;

/**
 ****************************************************************
 ****************************************************************
 * The component in which the board is drawn.
 * @author  Mats Luthman
 ****************************************************************
 ****************************************************************/

class BoardDrawingArea extends javax.swing.JComponent
{
    private Commands m_CommandInterface;


    /**
     ****************************************************************
     * Constructor.
     * @author  Mats Luthman
     ****************************************************************/

    BoardDrawingArea(Commands commandInterface)
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
