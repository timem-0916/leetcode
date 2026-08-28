package pkg2000;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

public class LeetCode2600 {

    /**
     * 2642. 设计可以求最短路径的图类
     * Graph
     */
    class Graph {

        private int n;
        private List<int[]>[] g;

        public Graph(int n, int[][] edges) {
            this.n = n;
            g = new List[n];
            for (int i = 0; i < n; i++) {
                g[i] = new ArrayList<int[]>();
            }
            for (int[] e : edges) {
                int u = e[0], v = e[1], w = e[2];
                g[u].add(new int[]{v, w});
            }
        }
        
        public void addEdge(int[] edge) {
            int u = edge[0], v = edge[1], w = edge[2];
            g[u].add(new int[]{v, w});
        }
        
        public int shortestPath(int node1, int node2) {
            final int INF = Integer.MAX_VALUE >> 1;
            int[] dist = new int[n];
            Arrays.fill(dist, INF);
            dist[node1] = 0;

            PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[1] - b[1]);
            pq.offer(new int[]{node1, 0});

            while (!pq.isEmpty()) {
                int[] curr = pq.poll();
                int u = curr[0], cost = curr[1];
                if (u == node2) {
                    return cost;
                }
                if (cost > dist[u]) {
                    continue;
                }
                for (int[] next : g[u]) {
                    int v = next[0], w = next[1];
                    if (dist[u] + w < dist[v]) {
                        dist[v] = dist[u] + w;
                        pq.offer(new int[]{v, dist[v]});
                    }
                }
            }

            return -1;
        }
    }

}
