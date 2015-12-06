package Engine;

/**
 * Created by Rakesh on 06-12-2015.
 */
public class PositionalPlayer implements AIPlayer {

    int[] positionalValues;

    public PositionalPlayer() {

    }

    // This should return a move based on the best positional values
    public Move ComputeMove(Game game) {
        return null;
    }

    // Set the values of the positions
    void setPositionValues() {

    }

    /*
        Unwanted set of methods for this player
     */
    public void SetInterrupt(boolean cond) {
        // Do nothing
    }

    public void SetStrength(int str) {
        // Do nothing
    }

    public int GetStrength() {
        return 0;
    }
}
