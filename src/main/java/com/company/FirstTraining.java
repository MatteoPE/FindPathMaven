package com.company;

import com.company.deeplearning.ActionValue;
import com.company.model.Topology;
import com.company.newgame.NewState;
import com.company.util.PrinterHelper;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.RmsProp;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.util.Random;
import java.util.Set;

public class FirstTraining {

    public static void main(String [] args) {

        int size = 4;
        Topology t = new Topology(size);
        Random rand = new Random();
        PrinterHelper ph = new PrinterHelper();

        // Topology initialization
        t.generateObstacle(3, 1, 1);

        // State initialization
        int playerID = 0;
        int goalID = 15;
        int[] obstacleIDs = t.getSwitchedOffIDs();
        NewState s = new NewState(playerID,goalID,obstacleIDs,size);

        // Neighbors used as actions
        t.reInitializeNeighbors();

        /** Neural Network Configuration */

        int nInput = s.getNumID()*3;
        int nLayer1 = 4*s.getNumID();
        int nLayer2 = 2*s.getNumID();
        int nOutput = s.getNumID();

        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(123)
                .weightInit(WeightInit.XAVIER)
                //.l2(0.1)

                .updater(new RmsProp())
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .list()
                .layer(0, new DenseLayer.Builder().nIn(nInput).nOut(nLayer1)
                        .weightInit(WeightInit.XAVIER)
                        .activation(Activation.RELU)
                        .build())
                .layer(1, new DenseLayer.Builder().nIn(nLayer1).nOut(nLayer2)
                        .weightInit(WeightInit.XAVIER)
                        .activation(Activation.RELU)
                        .build())
                .layer(2, new OutputLayer.Builder(LossFunctions.LossFunction.MSE).nIn(nLayer2).nOut(nOutput)
                        .weightInit(WeightInit.XAVIER)
                        .activation(Activation.IDENTITY)
                        .build())
                .pretrain(false).backprop(true).build();

        MultiLayerNetwork NN = new MultiLayerNetwork(conf);

        NN.init();

        /** Training */

        int epochs = 2000;
        double gamma = 0.9;
        double epsilon = 1;

        for(int i = 0; i < epochs; i++) {

            NewState currentState = s;
            int currentPlayerID = s.getPlayerID();
            boolean status = true;
            //while game still in progress
            while(status) {
                //we are in state S
                //let's run our Q function on S to get Q values for all possible actions
                int action = -1; // 0 <= action < nOutput
                INDArray qval = NN.output(currentState.getINDArray().reshape(1,nInput));
                //find adjacent nodes
                Set<Integer> adjacent = t.getNeighborsID(currentPlayerID);
                ActionValue currentAV = new ActionValue(adjacent, qval);

                if(rand.nextDouble() < epsilon) {
                    //choose random action
                    action = currentAV.randomAction(rand);
                    if(action == -1) {
                        action = currentPlayerID;
                        status = false;
                    }
                } else {
                    //choose best action from Q(s,a) values
                    action =  currentAV.bestAction();
                }
                //take action, observe new state S'
                int newPlayerID = action;
                NewState newState = new NewState(newPlayerID, goalID, obstacleIDs, size);
                //observe reward
                double reward = newState.getReward();
                //get max_Q(S',a)
                INDArray newQ = NN.output(newState.getINDArray().reshape(1,nInput));
                Set<Integer> newAdjacent = t.getNeighborsID(newPlayerID);
                ActionValue newAV = new ActionValue(newAdjacent, newQ);

                double maxQ = newAV.bestQValue();
                INDArray y = Nd4j.zeros(1,nOutput);
                y.putRow(0, qval.getRow(0));
                double update = 0;
                if(reward < -0.75 || reward > 0.75) { // terminal state
                    update = reward;
                } else { // non-terminal state
                    update = reward + gamma*maxQ;
                }
                y.putScalar(action, update); // target output

                NN.fit(currentState.getINDArray(),y);
                currentState = newState;
                currentPlayerID = newPlayerID;
                if(reward < -0.75 || reward > 0.75) {
                    status = false;
                }
                if(epsilon > 0.1) {
                    epsilon -= 1/epochs;
                }

            }
            if(i%100 == 0)
                System.out.println("Game #" + i);

        }

        /** Testing */

        NewState testState = s;
        int testPlayerID = s.getPlayerID();
        System.out.println("Initial State");
        ph.printGrid("_PGO".toCharArray(), testState.displayGrid());
        boolean status = true;
        //while game still in progress
        int i = 1;
        while(status) {
            int action = -1; // 0 <= action < nOutput
            INDArray INDAState = testState.getINDArray();
            INDArray inputNN = INDAState.reshape(1,nInput);
            INDArray qval = NN.output(inputNN);
            //INDArray qval = NN.output(testState.getINDArray().reshape(1,nInput));
            //find adjacent nodes
            Set<Integer> adjacent = t.getNeighborsID(testPlayerID);
            ActionValue testAV = new ActionValue(adjacent, qval);

            action = testAV.bestAction();
            System.out.println("Move #" + i + "; Going to node: " + action);
            testState = new NewState(action, goalID, obstacleIDs, size);
            testPlayerID = action;
            ph.printGrid("_PGO".toCharArray(), testState.displayGrid());
            double reward = testState.getReward();
            if(reward < -0.75 || reward > 0.75) {
                status = false;
                System.out.println("Reward: " + reward);
            }
            i++;
            if(i>10) {
                System.out.println("Game lost, too many moves!");
                break;
            }
        }




    }

}
