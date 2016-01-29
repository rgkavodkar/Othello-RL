package AI;

import GameEngine.Game;
import GameEngine.Move;
import GameEngine.Position;
import GameEngine.Score;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.logic.FeedforwardLogic;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.persist.EncogPersistedCollection;

import java.io.File;
import java.util.HashMap;
import java.util.Random;


/**
 * Created by Rakesh on 06-12-2015.
 */
public class NeuralNetPlayer implements AIPlayer {

    private final String NEURALNET_FILE = "neuralnet.eg";
    private final String NEURALNET_ID = "neuralnet";
    private double Q;
    private double q_learning_rate;
    private double gamma;
    private double reward;
    boolean explore;
    private double t, a = 1, b = 0.99999, c= 0.002;
    int gamesPlayed;


    int playerID;

    private double[][] rewardMatrix = {
            {1,     -0.2,   0.1,    0.05,   0.05,   0.1,    -0.2,   1},
            {-0.2,  -0.5,   -0.02,  -0.02,  -0.02,  -0.02,  -0.5,   -0.2},
            {0.1,   -0.02,  -0.01,  -0.01,  -0.01,  -0.01,  -0.02,  0.1},
            {0.05,  -0.02,  -0.01,  -0.01,  -0.01,  -0.01,  -0.02,  0.05},
            {0.05,  -0.02,  -0.01,  -0.01,  -0.01,  -0.01,  -0.02,  0.05},
            {0.1,   -0.02,  -0.01,  -0.01,  -0.01,  -0.01,  -0.02,  0.1},
            {-0.2,  -0.5,   -0.02,  -0.02,  -0.02,  -0.02,  -0.5,   -0.2},
            {1,     -0.2,   0.1,    0.05,   0.05,   0.1,    -0.2,   1}
    };

    private double[][] reward2Matrix = {
            {10,    -2,     1,      0.5,    0.5,    1,      -2,     10},
            {-2,    -5,     -0.2,   -0.2,   -0.2,   -0.2,   -5,     -2},
            {1,     -0.2,   -0.1,   -0.1,   -0.1,   -0.1,   -0.2,   1},
            {0.5,   -0.2,   -0.1,   -0.1,   -0.1,   -0.1,   -0.2,   0.5},
            {0.5,   -0.2,   -0.1,   -0.1,   -0.1,   -0.1,   -0.2,   0.5},
            {1,     -0.2,   -0.1,   -0.1,   -0.1,   -0.1,   -0.2,   1},
            {-2,    -5,     -0.2,   -0.2,   -0.2,   -0.2,   -5,     -2},
            {10,    -2,     1,      0.5,    0.5,    1,      -2,     10}
    };

    private double[][] reward3Matrix = {
            {100,   -20,   10,   5,    5,    10,   -20,  100},
            {-20,   -50,   -2,   -2,   -2,   -2,   -50,  -20},
            {10,    -2,    -1,   -1,   -1,   -1,   -2,   1},
            {5,     -2,    -1,   -1,   -1,   -1,   -2,   5},
            {5,     -2,    -1,   -1,   -1,   -1,   -2,   5},
            {10,    -2,    -1,   -1,   -1,   -1,   -2,   1},
            {-20,   -50,   -2,   -2,   -2,   -2,   -50,  -20},
            {100,   -20,   10,   5,    5,    10,   -20,  100},
    };

    private HashMap<Integer, BasicNetwork> networkMap;

    public NeuralNetPlayer(double alpha, int player) {

        networkMap = new HashMap<>();

        File f = new File(NEURALNET_FILE);
        if(f.exists() && !f.isDirectory()) {
            loadNetwork();
        } else {
            for (int i = 0; i < 64; i++) {

                // Create a basic neuralnet object
                BasicNetwork neuralnet = new BasicNetwork();
                // Add the input layer with 64 neurons, no bias
                neuralnet.addLayer(new BasicLayer(null, false, 64));
                // Add the hidden layer with 32 neurons, TANH activated
                neuralnet.addLayer(new BasicLayer(new ActivationTANH(), false, 32));
                // Add the output layer with 1 neuron, TANH activated
                neuralnet.addLayer(new BasicLayer(new ActivationTANH(), false, 1));
                // Set it as a feedforward network
                neuralnet.setLogic(new FeedforwardLogic());
                // Finalize and reset the structure
                neuralnet.getStructure().finalizeStructure();
                neuralnet.reset();

                networkMap.put(i+1, neuralnet);
            }
        }

        playerID = player;
        Q = 0;
//        q_learning_rate = alpha;
        q_learning_rate = 0.1;
        gamma = 1;
        reward = 0;

        explore = true;

        t = a*Math.pow(b, gamesPlayed);
    }

    private void saveNetwork() {
        if(playerID == 1) return;
        final EncogPersistedCollection persistor = new EncogPersistedCollection(NEURALNET_FILE);
        persistor.create();
        for (int i = 0; i < 64; i++) {
            System.out.println("Writing NN " + (i+1));
            persistor.add(NEURALNET_ID + (i + 1), networkMap.get(i + 1));
        }
        System.out.println("Done Writing NN");
    }

    private void loadNetwork() {
        final EncogPersistedCollection persistor = new EncogPersistedCollection(NEURALNET_FILE);

        for (int i = 0; i < 64; i++) {
            System.out.println("Loading NeuralNet " + i);
            networkMap.put((i + 1), (BasicNetwork)persistor.find(NEURALNET_ID + (i + 1)));
        }
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
    private void updateWeights(double[] inputState, double expected, int nn_index) {

        if(nn_index == -1) {
            return;
        }

        // Create a NeuralDataSet object which contains the input and the expected output
        NeuralDataSet trainingSet = new BasicNeuralDataSet(
                new double[][]{ inputState },
                new double[][] { {expected} }
        );

        // Create a BackPropagation object that adjusts the weights of the NN passed
        Backpropagation bProp = new Backpropagation(networkMap.get(nn_index), trainingSet);
        // Set the neural network learning rate and the momentum
        bProp.setLearningRate(0.1);
        bProp.setMomentum(0);
        // Perform the backpropagation
        bProp.iteration();
    }

    public Move ComputeMove(Game game) {

//        System.out.println("t = " + t + " | c = " + c);

        // Get the current position and extract the state
        Position currentPosition = game.getCurrentPosition();
        double[] state = getStateArray(currentPosition);

        // Input state for the neuralnetwork
        NeuralData inputState = new BasicNeuralData(state);

        int player = game.GetWhoseTurn();
        int max_x = -1, max_y = -1;
        double max_q = -999999;

        int best_nn_index = -1;

        double divisor = 0;

        // Get the max output from the current position using the neuralnet
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                Move move = new Move(i, j, player);
                if(currentPosition.MoveIsLegal(move)) {

                    int state_pos = (i - 1) * 8 + (j - 1);
                    best_nn_index = state_pos + 1;
                    BasicNetwork localNetwork = networkMap.get(best_nn_index);
                    NeuralData output = localNetwork.compute(inputState);

                    double qValue = output.getData(0);

                    if (qValue > max_q) {
                        max_q = qValue;
                        max_x = i;
                        max_y = j;
                    }

                    divisor += Math.exp(qValue / t);
                }
            }
        }

        if(explore) {
            double choice = new Random().nextDouble();
            double sum = 0;

            for (int i = 1; i < 9; i++) {

                boolean breaker = false;

                for (int j = 1; j < 9; j++) {
                    Move move = new Move(i, j, player);
                    if(currentPosition.MoveIsLegal(move)) {
                        int state_pos = (i - 1) * 8 + (j - 1);
                        best_nn_index = state_pos + 1;
                        BasicNetwork localNetwork = networkMap.get(best_nn_index);
                        NeuralData output = localNetwork.compute(inputState);

                        double qValue = output.getData(0);

                        double p = Math.exp(qValue/t)/divisor;
                        sum += p;

                        if(choice <= sum) {
                            max_q = qValue;
                            max_x = i;
                            max_y = j;

                            breaker = true;
                            break;
                        }
                    }
                }
                if(breaker) break;
            }

        }

        Q = max_q;

        // MAke the best move from the above deduction
        Move final_move = null;
        if(max_x != -1 && max_y != -1) {
            final_move = new Move(max_x, max_y, player);
            game.MakeMove(final_move);
            reward = reward3Matrix[max_x - 1][max_y - 1];
        }

        // Go to the new state, where the opponent has to make the move
        Position newPosition = game.getCurrentPosition();
        double[] newState = getStateArray(newPosition);

        // Construct the NeuralData object using the state array
        // And the get the predicted output array
        // Input state for the neuralnetwork
        NeuralData nextState = new BasicNeuralData(newState);

        // Opponents move
        int oppo_player = game.GetWhoseTurn();
        double new_max_q = -999999;

        // Get the max Q value from the new state
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                Move move = new Move(i, j, oppo_player);
                if(newPosition.MoveIsLegal(move)) {

                    int state_pos = (i - 1) * 8 + (j - 1);
                    BasicNetwork localNetwork = networkMap.get(state_pos + 1);
                    NeuralData output = localNetwork.compute(nextState);

                    if(output.getData(0) > new_max_q) {
                        new_max_q = output.getData(0);
                    }
                }
            }
        }

        boolean gameComplete = false;

        if(game.GetMoveNumber() == 59) {
            gameComplete = true;

            int[][] b1 = newPosition.getBoard();
            int b_i = -1, b_j = -1;

            for (int i = 1; i < 9; i++) {
                for (int j = 1; j < 9; j++) {
                    if(b1[i][j] == 0) {
                        b_i = i;
                        b_j = j;
                        break;
                    }
                }
            }

            // Make the last move, if possible
            if(game.MoveIsLegal(new Move(b_i, b_j, game.GetWhoseTurn()))) {
                game.MakeMove(new Move(b_i, b_j, game.GetWhoseTurn()));
            } else if(game.MoveIsLegal(new Move(b_i, b_j, player))) {
                game.MakeMove(new Move(b_i, b_j, player));
            }

            // Compute the NN score
            int nn_score = game.getCurrentPosition().GetScore(oppo_player);
            if(nn_score > 32) {
                reward = 10;
            } else if(64 - nn_score > 32){
                reward = -10;
            }

            t = a*Math.pow(b, ++gamesPlayed);
            explore = (t > c);

        } else if(game.GetMoveNumber() == 60) {
            gameComplete = true;
            int nn_score = game.getCurrentPosition().GetScore(oppo_player);
            if(nn_score > 32) {
                reward = 10;
            } else if(64 - nn_score > 32){
                reward = -10;
            }
            t = a*Math.pow(b, ++gamesPlayed);
            explore = (t > c);
        }

        Q = (1 - q_learning_rate)*Q + q_learning_rate * (reward + gamma * new_max_q);

        updateWeights(state, Q, best_nn_index);


        if((max_x != -1 && max_y != -1) && !gameComplete)
            game.TakeBackMove();


        return final_move;
    }

//    public Move ComputeMove2(Game game) {
//
//        if(game.GetMoveNumber() == 1) {
//            Q = 0;
//        }
//
//        // Get the current position and extract the state
//        Position currentPosition = game.getCurrentPosition();
//        double[] state = getStateArray(currentPosition);
//
//        // Construct the NeuralData object using the state array
//        NeuralData d = new BasicNeuralData(state);
//        NeuralData output = neuralnet.compute(d);
//
//        int player = game.GetWhoseTurn();
//        int max_x = 1, max_y = 1;
//        double max_q = -999999;
//
//        // Get the max output from the current position using the neuralnet
//        for (int i = 1; i < 9; i++) {
//            for (int j = 1; j < 9; j++) {
//                Move move = new Move(i, j, player);
//                if(currentPosition.MoveIsLegal(move)) {
//                    int state_pos = (i - 1) * 8 + (j - 1);
//                    if(output.getData()[state_pos] > max_q) {
//                        max_q = output.getData()[state_pos];
//                        max_x = i;
//                        max_y = j;
//                    }
//                }
//            }
//        }
//
//        // Make the move predicted above
//        Move final_move = new Move(max_x, max_y, player);
//        game.MakeMove(final_move);
//
//        // Go to the new state, where the opponent has to make the move
//        Position newPosition = game.getCurrentPosition();
//        double[] newState = getStateArray(currentPosition);
//
//        // Construct the NeuralData object using the state array
//        // And the get the predicted output array
//        d = new BasicNeuralData(newState);
//        NeuralData output2 = neuralnet.compute(d);
//
//        int oppo_player = game.GetWhoseTurn();
//        double new_max_q = -999999;
//
//        // Get the max Q value from the new state
//        for (int i = 1; i < 9; i++) {
//            for (int j = 1; j < 9; j++) {
//                Move move = new Move(i, j, oppo_player);
//                if(currentPosition.MoveIsLegal(move)) {
//                    int state_pos = (i - 1) * 8 + (j - 1);
//                    if(output2.getData()[state_pos] > new_max_q) {
//                        new_max_q = output2.getData()[state_pos];
//                    }
//                }
//            }
//        }
//
//        // Branch to calculate the reward
//        if(game.GetMoveNumber() == 59) {
//            Move move = null;
//            int[][] b = newPosition.getBoard();
//            for (int i = 1; i < 9; i++) {
//                for (int j = 1; j < 9; j++) {
//                    if(b[i][j] == 0) {
//                        move = new Move(i, j, game.GetWhoseTurn());
//                        break;
//                    }
//                }
//            }
//
//            if(game.MoveIsLegal(move)) {
//                game.MakeMove(move);
//                int oppo_score = game.getCurrentPosition().GetScore(oppo_player);
//                if(64 - oppo_score > 32) {
//                    reward = 1;
//                } else if(oppo_score > 32){
//                    reward = -1;
//                }
//            }
//        } else if(game.GetMoveNumber() == 60) {
//            int nn_score = game.getCurrentPosition().GetScore(oppo_player);
//            if(nn_score > 32) {
//                reward = 1;
//            } else if(64 - nn_score > 32){
//                reward = -1;
//            }
//        }
//
//
//        Q = (1 - q_learning_rate)*Q + q_learning_rate * (reward + gamma * new_max_q);
//
//        double[] expected = output.getData();
//        expected[(max_x - 1) * 8 + (max_y - 1)] = Q;
//
//        // Do backpropagation
//        updateWeights(state, expected);
//        if(game.GetMoveNumber() == 60 || game.GetMoveNumber() == 59) {
//            saveNetwork();
//        }
//        game.TakeBackMove();
//        reward = 0;
//        return final_move;
//    }

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
