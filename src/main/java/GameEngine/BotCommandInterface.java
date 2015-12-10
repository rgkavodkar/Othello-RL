/*
 * CommandInterface.java
 *
 */

package GameEngine;


import AI.AIPlayer;
import AI.MinimaxPlayer;
import AI.NeuralNetPlayer;
import AI.PositionalPlayer;

/**
 * Implements an interface to Othello.MinimaxPlayer that is directed to
 * what a user interface might want to do.
 */

public class BotCommandInterface implements Runnable, Commands, BotCommands
{
    public BotCommandInterface(int mode)
    {
        m_last_entered_move_score = 0;
        m_calculating = false;
        if(mode == 1) {
            // Minimax player
            m_Engine_1  = new MinimaxPlayer();
            m_Engine_2  = new MinimaxPlayer();
        } else if(mode == 2) {
            // Positional player
            m_Engine_1  = new PositionalPlayer();
            m_Engine_2  = new MinimaxPlayer();
        } else if(mode == 3) {
            // NeuralNet player
            m_Engine_1  = new NeuralNetPlayer();
            m_Engine_2  = new MinimaxPlayer();
        }
        m_Game = new Game();
    }

    public void makeBotMove() {

        int player = m_Game.GetWhoseTurn();
        Move move = null;
        System.out.println("Player: " + player);
        if(player == 1) {
            move = m_Engine_1.ComputeMove(m_Game);
        } else if(player == 2) {
            move = m_Engine_2.ComputeMove(m_Game);
        }

        if (move != null)
            m_Game.MakeMove(move);

    }


    public void run()
    {
        int player = m_Game.GetWhoseTurn();

        Move move = m_Engine_1.ComputeMove(m_Game);

        if (move != null)
            m_Game.MakeMove(move);

        m_calculating = false;

        CommandInterfaceListener tmpListener = m_Listener;
        m_Listener = null; // ComputeMove() could be called by
        // ComputationFinished() setting m_Listener again

        tmpListener.ComputationFinished(move);
    }


    public int GetSquare(int x, int y)
    {
        return m_Game.GetSquare(x, y);
    }


    public int GetScoreWhite()
    {
        return m_Game.GetScore(Score.WHITE);
    }


    public int GetScoreBlack()
    {
        return m_Game.GetScore(Score.BLACK);
    }


    public int GetWhoseTurn()
    {
        return m_Game.GetWhoseTurn();
    }


    public String GetLastMove()
    {
        if (m_Game.GetLastMove() == null) return null;

        char[] ch = new char[1];
        ch[0] = (char) ('A' + m_Game.GetLastMove().GetX() - 1);

        String returnvalue = new String(ch);

        return returnvalue + m_Game.GetLastMove().GetY();
    }


    public boolean MoveIsPossible()
    {
        if (m_calculating) return false;

        return m_Game.GetWhoseTurn() != Score.NOBODY;
    }


    public void ComputeMove(CommandInterfaceListener Listener)
    {
        m_Listener = Listener;

        m_calculating = true;

        Thread thread = new Thread(this);
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }


    public boolean ComputeMoveIsPossible()
    {
        return ! m_calculating && m_Game.GetWhoseTurn() != Score.NOBODY;
    }


    public boolean MakeMove(int x, int y)
    {
        boolean returnvalue = false;

        if (MakeMoveIsPossible(x, y))
        {
            int player = m_Game.GetWhoseTurn();

            m_last_entered_move_score =
                    m_Game.GetScore(Score.WHITE) + m_Game.GetScore(Score.BLACK);

            Move m = new Move(x, y, player);

            if (m_Game.MoveIsLegal(m))
            {
                m_last_entered_move_score =
                        m_Game.GetScore(Score.WHITE) + m_Game.GetScore(Score.BLACK);

                m_Game.MakeMove(m);

                returnvalue = true;
            }
        }

        return returnvalue;
    }


    public boolean MakeMoveIsPossible(int x, int y)
    {
        if (m_calculating) return false;

        int player = m_Game.GetWhoseTurn();
        Move m = new Move(x, y, player);

        return m_Game.MoveIsLegal(m);
    }


    public void Undo()
    {
        if (UndoIsPossible())
        {
            if (m_last_entered_move_score >= 4)
                while (m_Game.GetScore(Score.WHITE) + m_Game.GetScore(Score.BLACK) >
                        m_last_entered_move_score)
                {
                    m_Game.TakeBackMove();
                }

            m_last_entered_move_score = 0;
        }
    }


    public boolean UndoIsPossible()
    {
        return ! m_calculating && m_last_entered_move_score >= 4;
    }


    public void TakeBack()
    {
        if (TakeBackIsPossible())
        {
            m_Game.TakeBackMove();

            if (m_Game.GetScore(Score.WHITE) + m_Game.GetScore(Score.BLACK) <= 4)
                m_last_entered_move_score = 0;
            else
            if (m_Game.GetScore(Score.WHITE) + m_Game.GetScore(Score.BLACK) <=
                    m_last_entered_move_score)
                m_last_entered_move_score = 0;
        }
    }


    public boolean TakeBackIsPossible()
    {
        return ! m_calculating &&
                m_Game.GetScore(Score.WHITE) + m_Game.GetScore(Score.BLACK) > 4;
    }

    public boolean SetLevelIsPossible()
    {
        return ! m_calculating;
    }


    public void NewGame()
    {
        if (NewGameIsPossible())
        {
            m_last_entered_move_score = 0;
            m_Game.Reset();
        }
    }


    public boolean NewGameIsPossible()
    {
        return ! m_calculating;
    }


//    public void InterruptComputation()
//    {
//        if (InterruptComputationIsPossible())
//            m_Engine.SetInterrupt(true);
//    }


    public boolean InterruptComputationIsPossible()
    {
        return m_calculating;
    }


    private int m_last_entered_move_score;
    private boolean m_calculating;
    private AIPlayer m_Engine_1;
    private AIPlayer m_Engine_2;
    private Game m_Game;
    CommandInterfaceListener m_Listener;
}