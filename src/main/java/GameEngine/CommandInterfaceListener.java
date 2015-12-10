/*
 * CommandInterfaceListener.java
 *
 */

package GameEngine;


/**
 * Called when CommandCenter has finished a computation.
 */

public interface CommandInterfaceListener
{
    public void ComputationFinished(Move m);
}