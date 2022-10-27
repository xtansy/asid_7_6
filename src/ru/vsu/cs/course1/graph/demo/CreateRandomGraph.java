package ru.vsu.cs.course1.graph.demo;

import java.util.Random;

public class CreateRandomGraph {
    private StringBuilder consol = new StringBuilder();
    private StringBuilder graphWiz = new StringBuilder();

    public CreateRandomGraph(int vCount, double ratio) {
        this.consol = new StringBuilder();
        this.graphWiz = new StringBuilder();

        getRndGraph(vCount, ratio);
    }

    private void getRndGraph(int vCount, double ratio){

        graphWiz.append("graph{\n");

        int count = 0;
        for (int i = 0; i < vCount - 1; i++) {
            for (int j = i + 1; j < vCount; j++) {
                if(new Random().nextDouble() / 5 <=ratio){
                    consol.append(Integer.toString(i)+" "+Integer.toString(j)+"\n");
                    graphWiz.append(Integer.toString(i)+" -- "+Integer.toString(j)+"\n");
                    count++;
                }
            }
        }

        consol.insert(0, Integer.toString(count)+"\n");
        consol.insert(0,Integer.toString(vCount)+"\n");
        graphWiz.append("}\n");
    }

    public String getConsol() {
        return consol.toString();
    }

    public String getGraphWiz() {
        return graphWiz.toString();
    }
}
