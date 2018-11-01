package com.company;

import com.company.newgame.NewState;
import com.company.model.Topology;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.impl.accum.Max;
import org.nd4j.linalg.api.ops.impl.indexaccum.IMax;
import org.nd4j.linalg.factory.Nd4j;

import java.util.Random;
import java.util.Set;

public class NewMain{

    public static void main(String [] args) {

        int size = 4;
        Topology t = new Topology(size);
        Random rand = new Random();

        // Topology initialization
        t.generateObstacle(3, 1, 1);

        // State initialization
        int playerID = 0;
        int goalID = 12;
        int[] obstacleIDs = t.getSwitchedOffIDs();
        NewState s = new NewState(playerID,goalID,obstacleIDs,size);

        // Neighbors used as actions
        t.reInitializeNeighbors();

        /** Neural Network Configuration */

        int nInput = s.getNumID()*3;
        int nLayer1 = 164;
        int nLayer2 = 150;
        int nOutput = s.getNumID();

        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(123)
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
                .layer(2, new OutputLayer.Builder().nIn(nLayer2).nOut(nOutput)
                        .weightInit(WeightInit.XAVIER)
                        .activation(Activation.IDENTITY)
                        .build())
                .pretrain(false).backprop(true).build();

        MultiLayerNetwork NN = new MultiLayerNetwork(conf);

        NN.init();

        /** Training */

        int epochs = 1000;
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
                // make non adjacent nodes unavailable
                for (Integer node: adjacent) {
                    qval.putScalar(node, -1000.0);
                }
                if(rand.nextDouble() < epsilon) {
                    //choose random action
                    action = rand.nextInt(nOutput);
                    while(qval.getDouble(action) < -900.0) {
                        action = rand.nextInt(nOutput);
                    }
                } else {
                    //choose best action from Q(s,a) values
                    action =  Nd4j.getExecutioner().execAndReturn(new IMax(qval)).getFinalResult();
                }
                //take action, observe new state S'
                int newPlayerID = action;
                NewState newState = new NewState(newPlayerID, goalID, obstacleIDs, size);
                //observe reward
                double reward = newState.getReward();
                //get max_Q(S',a)
                INDArray newQ = NN.output(newState.getINDArray().reshape(1,nInput));
                Set<Integer> newAdjacent = t.getNeighborsID(newPlayerID);
                // make non adjacent nodes unavailable
                for (Integer node: newAdjacent) {
                    newQ.putScalar(node, -1000.0);
                }
                double maxQ = Nd4j.getExecutioner().execAndReturn(new Max(newQ)).getFinalResult().doubleValue();
                INDArray y = Nd4j.zeros(1,nOutput);
                y.putRow(0, qval.getRow(0));
                double update = 0;
                if(reward < -0.75 || reward > 0.75) { // terminal state
                    update = reward;
                } else { // non-terminal state
                    update = reward + gamma*maxQ;
                }
                y.putScalar(action, reward); // target output
                System.out.println("Game #" + i);
                NN.fit(currentState.getINDArray(),y);
                currentState = newState;
                currentPlayerID = newPlayerID;
                if(reward == 1 || reward == -1) {
                    status = false;
                }
                if(epsilon > 0.1) {
                    epsilon -= 1/epochs;
                }

            }

        }

    }

}
