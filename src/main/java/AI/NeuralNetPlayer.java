package AI;

import GameEngine.Game;
import GameEngine.Move;
import GameEngine.Position;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.logic.FeedforwardLogic;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.persist.EncogPersistedCollection;
import org.encog.persist.PersistWriter;

import java.io.File;
import java.util.Iterator;


/**
 * Created by Rakesh on 06-12-2015.
 */
public class NeuralNetPlayer implements AIPlayer {

    final String NEURALNET_FILE = "neuralnet.eg";
    final String NEURALNET_ID = "neuralnet";

    BasicNetwork neuralnet;
    double Q;
    double q_learning_rate;
    double gamma;
    int reward;

    public NeuralNetPlayer(double alpha) {

        File f = new File(NEURALNET_FILE);
        if(f.exists() && !f.isDirectory()) {
            loadNetwork();
        } else {
            neuralnet = new BasicNetwork();
            neuralnet.addLayer(new BasicLayer(new ActivationSigmoid(), false, 64));     // Input layer
            neuralnet.addLayer(new BasicLayer(new ActivationTANH(), true, 44));         // Hidden layer, need to change the bias and neoron count and experiment if possible
            neuralnet.addLayer(new BasicLayer(new ActivationSigmoid(), false, 64));     // Output layer
            neuralnet.setLogic(new FeedforwardLogic());
            neuralnet.getStructure().finalizeStructure();
            neuralnet.reset();
        }

        Q = 0;
        q_learning_rate = alpha;
        gamma = 0.9;
        reward = 0;
    }

    private void saveNetwork() {
        final EncogPersistedCollection persistor = new EncogPersistedCollection(NEURALNET_FILE);
        persistor.create();
        persistor.add(NEURALNET_ID, neuralnet);
    }

    private void loadNetwork() {
        final EncogPersistedCollection persistor = new EncogPersistedCollection(NEURALNET_FILE);
        neuralnet = (BasicNetwork)persistor.find(NEURALNET_ID);
    }

    private double[] getStateArray(Position position) {
        double[] stateArray = new double[64];
        int[][] board = position.getBoard();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                stateArray[8*(i)+j] = board[i][j];
            }
        }

        return stateArray;
    }

    // Perform one iteration of backpropagation
    private void updateWeights(double[] inputState, double[] expected) {
        double[][] input = new double[1][64];
        double[][] ideal = new double[1][64];
        input[0] = inputState;
        ideal[0] = expected;

        NeuralDataSet dataset = new BasicNeuralDataSet(input, ideal);

        Backpropagation bProp = new Backpropagation(neuralnet, dataset);
        bProp.setLearningRate(0.1);
        bProp.iteration();
    }

    public Move ComputeMove(Game game) {

        // Get the current position and extract the state
        Position currentPosition = game.getCurrentPosition();
        double[] state = getStateArray(currentPosition);

        // Construct the NeuralData object using the state array
        NeuralData d = new BasicNeuralData(state);
        NeuralData output = neuralnet.compute(d);

        int player = game.GetWhoseTurn();
        int max_x = 1, max_y = 1;
        double max_q = -999999;

        // Get the max output from the current position using the neuralnet
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                Move move = new Move(i, j, player);
                if(currentPosition.MoveIsLegal(move)) {
                    int state_pos = (i - 1) * 8 + (j - 1);
                    if(output.getData()[state_pos] > max_q) {
                        max_q = output.getData()[state_pos];
                        max_x = i;
                        max_y = j;
                    }
                }
            }
        }

        // Make the move predicted above
        Move final_move = new Move(max_x, max_y, player);
        game.MakeMove(final_move);

        // Go to the new state, where the opponent has to make the move
        Position newPosition = game.getCurrentPosition();
        double[] newState = getStateArray(currentPosition);

        // Construct the NeuralData object using the state array
        // And the get the predicted output array
        d = new BasicNeuralData(newState);
        NeuralData output2 = neuralnet.compute(d);

        player = game.GetWhoseTurn();
        double new_max_q = -999999;

        // Get the max Q value from the new state
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                Move move = new Move(i, j, player);
                if(currentPosition.MoveIsLegal(move)) {
                    int state_pos = (i - 1) * 8 + (j - 1);
                    if(output2.getData()[state_pos] > new_max_q) {
                        new_max_q = output2.getData()[state_pos];
                    }
                }
            }
        }

        // Branch to calculate the reward
        if(game.GetMoveNumber() == 59) {
            Move move = null;
            int[][] b = newPosition.getBoard();
            for (int i = 1; i < 9; i++) {
                for (int j = 1; j < 9; j++) {
                    if(b[i][j] == 0) {
                        move = new Move(i, j, game.GetWhoseTurn());
                        break;
                    }
                }

            }

            if(game.MoveIsLegal(move)) {
                game.MakeMove(move);
                int oppo_score = game.getCurrentPosition().GetScore(player);
                if(64 - oppo_score > 32) {
                    reward = 1;
                } else if(oppo_score > 32){
                    reward = -1;
                }
            }
        } else if(game.GetMoveNumber() == 60) {
            int nn_score = game.getCurrentPosition().GetScore(player);
            if(nn_score > 32) {
                reward = 1;
            } else if(64 - nn_score > 32){
                reward = -1;
            }
        }


        Q = (1 - q_learning_rate)*Q + q_learning_rate * (reward + gamma * new_max_q);

        double[] expected = output.getData();
        expected[(max_x - 1) * 8 + (max_y - 1)] = Q;

        // Do backpropagation
        updateWeights(state, expected);
        if(game.GetMoveNumber() == 60 || game.GetMoveNumber() == 59) {
            saveNetwork();
        }
        game.TakeBackMove();
        reward = 0;
        return final_move;
    }

    public void SetInterrupt(boolean cond) {

    }

    public void SetStrength(int str) {

    }

    public int GetStrength() {
        return 0;
    }

    public void closingTasks() {
        saveNetwork();
    }
}
