package zyklusFinder;

import java.util.*;

import model.Graph;
import model.Knoten;

/**
 * Date 16.01.2021
 * @author Lukas Schumann, Johannes Römisch
 * 
 * Header comment of original github source code:
 * Date 08/16/2015
 * @author Tushar Roy
 * https://github.com/mission-peace/interview/blob/master/src/com/interview/graph/TarjanStronglyConnectedComponent.java
 *
 * Find strongly connected components (sccs) of directed graph.
 *
 * The Algorithm:
 * Each node has an index and a LowLink value.
 * The LowLink value of a node describes its lowest reachable node index.
 * 
 * The Algorithm works by doing a DFS, tracking visited nodes in the 'visited' Set.
 * The stack is tracking the visit order of the nodes, but once a strongly connected component is found, 
 * all nodes belonging to that component are removed from the stack and stored in the result List.
 * 
 * If the DFS reaches a node n, that is marked as visited and that is on the stack, it means, 
 * that n is reachable from the previous node p and p is also reachable from n, so they belong to the same scc and therefore have the same LowLink value.
 * 
 * If a node is already visited and NOT on the stack, it has to belong to a different scc and therefore has to be ignored.
 * 
 * Synopsis:
 * Tarjan's Algorithm uses DFS and tracks the lowest reachable node index of every node.
 * Each time all neighbors of an scc's root node (index == LowLink value) have been explored, the nodes on the stack, 
 * from the top to the root node, are removed and stored as one scc.
 * This is done until every node has been visited.
 * 
 *
 * Reference - https://en.wikipedia.org/wiki/Tarjan%27s_strongly_connected_components_algorithm
 * Explanation of the Algorithm: https://www.youtube.com/watch?v=wUgWX0nc4NY
 */
public class TarjanStronglyConnectedComponent {

    private Map<Knoten, Integer> indices;
    private Map<Knoten, Integer> lowLink;
    private Deque<Knoten> stack;
    private Set<Knoten> visited;
    private List<Set<Knoten>> result;
    private int index;

    public List<Set<Knoten>> scc(Graph graph) {

        //keeps the sequence order when every node is visited
        index = 0;
        //keeps map of node -> index it was visited
        indices = new HashMap<>();

        //keeps map of node and index of first node visited in current DFS
        lowLink = new HashMap<>();

        //stack of visited nodes
        stack = new LinkedList<>();

        //tells if node has ever been visited or not. This is for DFS purpose.
        visited = new HashSet<>();

        //stores the strongly connected components result;
        result = new ArrayList<>();

        //start from any node in the graph.
        for (Knoten knoten : graph.getKnoten()) {
            if(!visited.contains(knoten)) {
                sccUtil(knoten);
            }
        }

        return result;
    }

    private void sccUtil(Knoten knoten) {

        visited.add(knoten);
        indices.put(knoten, index);
        lowLink.put(knoten, index);
        index++;
        stack.push(knoten);

        for (Knoten neighbor : knoten.getVerbundeneKnoten()) {
            //if neighbor is not visited then visit it and see if it has link back to node's ancestor. In that case update lowLink value to ancestor's index
            // --> DFS until a visited node is hit 
            if (!visited.contains(neighbor)) {
                sccUtil(neighbor);
                //updates lowLink[Knoten] = min(lowLink[Knoten], lowLink[neighbor]);
                lowLink.put(knoten, Math.min(lowLink.get(knoten), lowLink.get(neighbor)));
            } //if neighbor is on stack then see if its index is lower than node's lowLink. If yes then update node's lowLink to that.
            else if (stack.contains(neighbor)) {
                //If neighbor is not on stack, then the edge to neighbor is pointing to an SCC already found and must be ignored
                //DFS found a leaf/end --> "backtrack" one step and update lowLink value
                //updates lowLink[Knoten] = min(lowLink[Knoten], indices[neighbor]);
                lowLink.put(knoten, Math.min(lowLink.get(knoten), indices.get(neighbor)));
            }
        }

        //End of DFS:
        //--> All neighbors have been explored
        //if nodes lowLink value is the same as index then this is the root node for a strongly connected component.
        //keep taking nodes off the stack until the current node is found. They are all part of one strongly connected component.
        if (indices.get(knoten) == lowLink.get(knoten)) {
            Set<Knoten> stronglyConnectedComponent = new HashSet<>();
            Knoten v;
            do {
                v = stack.pollFirst();  // pollFirst() is analogue to pop(), but throws no exception
                stronglyConnectedComponent.add(v);
            } while (!knoten.equals(v));
            result.add(stronglyConnectedComponent);
        }
    }
}