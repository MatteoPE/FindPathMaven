package com.company.deeplearning;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.impl.accum.Max;
import org.nd4j.linalg.api.ops.impl.indexaccum.IMax;
import org.nd4j.linalg.factory.Nd4j;

import java.util.Random;
import java.util.Set;

public class ActionValue {

    private int[] actionArray;
    private INDArray QValueArray;
    private int numActions;

    public ActionValue(Set<Integer> possibleActions, INDArray QValues) {

        numActions = possibleActions.size();
        actionArray = new int[numActions];
        QValueArray = Nd4j.zeros(numActions);

        int id = 0;
        for (int action : possibleActions) {
            actionArray[id] = action;
            QValueArray.put(id, QValues.getColumn(action));
            id++;
        }

    }

    public int randomAction(Random rand) {
        if(numActions>0) {
            return actionArray[rand.nextInt(numActions)];
        }
        return -1;
    }

    public int bestAction() {
        return actionArray[
                Nd4j.getExecutioner().execAndReturn(new IMax(QValueArray)).getFinalResult()
                ];
    }

    public double bestQValue() {
        //return Nd4j.getExecutioner().execAndReturn(new Max(QValueArray)).getFinalResult().doubleValue();
        double max = -1.0;
        for(int i = 0; i <numActions; i++) {
            double current = QValueArray.getDouble(i);
            if(current > max) {
                max = current;
            }
        }
        return max;
    }

}
