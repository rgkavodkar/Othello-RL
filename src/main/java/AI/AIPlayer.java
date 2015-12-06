package AI;

import GameEngine.Game;
import GameEngine.Move;

/**
 * Created by Rakesh on 06-12-2015.
 */
public interface AIPlayer {

    Move ComputeMove(Game game);
    void SetInterrupt(boolean cond);
    void SetStrength(int str);
    int GetStrength();

}
