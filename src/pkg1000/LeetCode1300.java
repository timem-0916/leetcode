package pkg1000;

import java.util.Arrays;

public class LeetCode1300 {
    /**
     * 1334. 阈值距离内邻居最少的城市
     * @param n
     * @param edges
     * @param distanceThreshold
     * @return
     */
    public int findTheCity(int n, int[][] edges, int distanceThreshold) {
        final int INF = Integer.MAX_VALUE >> 1;

        int[][] g = new int[n][n];
        for (int i = 0; i < n; i++) {
            Arrays.fill(g[i], INF);
        }
        for (int[] e : edges) {
            int u = e[0], v = e[1], w = e[2];
            g[u][v] = w;
            g[v][u] = w;
        }

        // 1. Floyd-Warshall 算法
        // 从 u -> v，t 表示中间节点
        for (int t = 0; t < n; t++) {
            g[t][t] = 0;
            for (int u = 0; u < n; u++) {
                for (int v = u + 1; v < n; v++) {
                    int w = g[u][t] + g[t][v];
                    if (w < g[u][v]) {
                        g[u][v] = g[v][u] = w;
                    }
                }
            }
        }

        // 2. 统计每个城市在阈值内的邻居数量
        int minCnt = n, ans = n;
        for (int i = 0; i < n; i++) {
            int cnt = 0;
            for (int j = 0; j < n; j++) {
                if (g[i][j] <= distanceThreshold) {
                    cnt++;
                }
            }
            if (cnt <= minCnt) {
                minCnt = cnt;
                ans = i;
            }
        }

        return ans;
    }
}
