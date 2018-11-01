package com.company;

import com.company.game.Position;
import com.company.game.State;
import com.company.model.Node;
import com.company.model.Topology;


// multilayernetwork
import org.apache.log4j.BasicConfigurator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.layers.*;
import org.deeplearning4j.nn.conf.*;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.impl.accum.Max;
import org.nd4j.linalg.api.ops.impl.indexaccum.IMax;
import org.nd4j.linalg.factory.Nd4j;


import java.util.Random;
import java.util.Set;


public class Main {

    public static void main(String[] args) {

        boolean debug = false;

        BasicConfigurator.configure();

        int size = 4;
        Topology t = new Topology(size);
        State s = new State(size);
        Random rand = new Random();

        // Initializing the state
        Position playerP = new Position(0,0);
        Position goalP = new Position(2,3);
        s.stateInit(playerP, goalP);
        t.generateObstacle(3, 1, 1);
        s.obstaclesInit(t.getSwitchedOffPositions());

        if(debug) {
            System.out.println("The player is: " + s.getPlayerPosition().toString());
            System.out.println("The goal is: " + s.getGoalPosition().toString());
            System.out.println("The obstacles are: ");
            s.getObstaclePosition().stream().map(Position::toString).forEach(System.out::println);
            s.printState();
        }

        // Neighbors used as actions
        t.reInitializeNeighbors();

        if(debug) {
            for (Node n : t.getNodes()) {
                System.out.print("nodo " + n.getId() + "\tneighbors: ");
                n.getNeighbors().stream().map(Node::getId).forEach((x) -> {System.out.print(x+" ");});
                System.out.println();
            }
        }

        if(debug) {
            System.out.println("moving");
            s.makeMove(1);
            s.printState();
            System.out.println("reward: " + s.getReward());
            System.out.println("moving");
            s.makeMove(4);
            s.printState();
            System.out.println("reward: " + s.getReward());
        }

        /** Neural Network Configuration */

        try {
            
            int nInput = size*size*3;
            int nLayer1 = 164;
            int nLayer2 = 150;
            int nOutput = 16;

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

            if(debug) {
                INDArray input = Nd4j.rand(1,48);
                INDArray output = NN.output(input);
                System.out.println(output);
            }

            /** Training */

            int epochs = 1000;
            double gamma = 0.9;
            double epsilon = 1;

            for(int i = 0; i < epochs; i++) {

                boolean status = true;
                //while game still in progress
                while(status) {
                    //we are in state S
                    //let's run our Q function on S to get Q values for all possible actions
                    int action = -1; // 0 <= action < nOutput
                    INDArray qval = NN.output(s.getINDArrayFromState().reshape(1,nInput));
                    //find adjacent nodes
                    Set<Integer> adjacent = t.getNeighborsID(s.getPlayerPosition().fromXYtoK(size));
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
                    s.makeMove(action);
                    //observe reward
                    double reward = s.getReward();
                    //get max_Q(S',a)
                    INDArray newQ = NN.output(s.getINDArrayFromState().reshape(1,nInput));
                    //find adjacent nodes
                    Set<Integer> newAdjacent = t.getNeighborsID(s.getPlayerPosition().fromXYtoK(size));
                    // make non adjacent nodes unavailable
                    for (Integer node: newAdjacent) {
                        newQ.putScalar(node, -1000.0);
                    }
                    double maxQ = Nd4j.getExecutioner().execAndReturn(new Max(newQ)).getFinalResult().doubleValue();
                    

                }


            }
            

        } catch (ExceptionInInitializerError e) {
            System.out.println(e.getCause());
        }


    }
    
}
