package ru.vsu.cs.course1.graph.demo;

import ru.vsu.cs.course1.graph.Graph;


import java.util.*;


public class Task {
    private Graph graph;
    private int start;
    private int end;
    private Set<List<Integer>> roads;

    public Task(Graph graph, int start, int end) {
        this.start = start;
        this.end = end;
        roads = new TreeSet<>(new Comparator<List<Integer>>() {
            @Override
            public int compare(List<Integer> o1, List<Integer> o2) {
                if(o1.size()-o2.size() == 0) {
                    for (int i = 0; i < o2.size(); i++) {
                        if(o2.get(i) > o1.get(i))
                            return -1;
                        else if (o2.get(i) < o1.get(i))
                            return 1;
                    }
                }
                return o1.size()-o2.size();
            }
        });
        this.graph = graph;

        serchRoad();
    }



    public void serchRoad(){

        boolean[] visited = new boolean[graph.vertexCount()];
        Arrays.fill(visited, false);
        visited[start] = true;
        List<Integer> l = new ArrayList<>();
        l.add(start);
        newRoad(l, visited);

        //getBelongEveryone();

    }
    private void newRoad(List<Integer> tmpRoad, boolean[] visited){

        if(tmpRoad.size() > 2 && tmpRoad.get(tmpRoad.size()-1) == end){
            LinkedList<Integer> cont = new LinkedList<>();
            cont.addAll(tmpRoad);
            roads.add(cont);
            return;
        }

        for (int i = 0; i < graph.vertexCount(); i++) {
            if(!graph.isAdj(tmpRoad.get(tmpRoad.size()-1), i) || visited[i]){
                continue;
            }
            tmpRoad.add(i);
            visited[i] = true;
            newRoad(tmpRoad, visited);
            tmpRoad.remove(tmpRoad.size()-1);
            visited[i] = false;
        }

    }

    public Set<List<Integer>> getResCycle() {
        return roads;
    }


    public List<Integer> getBelongEveryone(){
        if (roads.isEmpty())
            return new ArrayList<>();

        List<Integer> shortestRoad = roads.stream().findFirst().get();
        List<Integer> result = new ArrayList<>();
        for (Integer vertex:shortestRoad) {
            if(vertex == start||vertex == end)
                continue;

            boolean flag = true;
            for (List<Integer> road:roads) {
                if(!road.contains(vertex)) {
                    flag = false;
                    break;
                }
            }
            if(flag){
                result.add(vertex);
            }

        }

        return result;

    }






}
