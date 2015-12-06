/*
 * CommandInterfaceListener.java
 *
 */

package GameEngine;


/**
 * Called when CommandInterface has finished a computation.
 */

public interface CommandInterfaceListener
{
    public void ComputationFinished(Move m);
}