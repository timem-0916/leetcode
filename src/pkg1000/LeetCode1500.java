package pkg1000;

import java.util.Arrays;

public class LeetCode1500 {
    /**
     * 1584. 连接所有点的最小费用
     * @param points
     * @return
     */
    public int minCostConnectPoints(int[][] points) {
        int n = points.length, m = (n * (n - 1)) >> 1;
        int[][] edges = new int[m][3];
        int idx = 0;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int w = Math.abs(points[i][0] - points[j][0]) + Math.abs(points[i][1] - points[j][1]);
                edges[idx][0] = i;
                edges[idx][1] = j;
                edges[idx++][2] = w;
            }
        }
        Arrays.sort(edges, (a, b) -> a[2] - b[2]);

        UnionFind uf = new UnionFind(n);
        int v = 0;
        for (int i = 0; i < m; i++) {
            if (uf.union(edges[i][0], edges[i][1])) {
                v += edges[i][2];
            }
        }
        return uf.getCount() == 1 ? v : -1;
    }

    /**
     * 内部类：并查集 (Union-Find)
     * UnionFind
     */
    class UnionFind {
        private int[] parent;
        // 记录当前连通分量的数量
        private int count;

        public UnionFind(int n) {
            parent = new int[n];
            count = n;
            for (int i = 0; i < n; i++) {
                parent[i] = i;
            }
        }

        /**
         * 查找根节点，带路径压缩
         * @param x
         * @return
         */
        public int find(int x) {
            return parent[x] == x ? x : (parent[x] = find(parent[x]));
        }

        /**
         * 合并两个集合，如果原本不连通则合并并返回 true，否则返回 false
         * @param x
         * @param y
         * @return
         */
        public boolean union(int x, int y) {
            x = find(x);
            y = find(y);
            // 已经在同一个集合，加入会形成环
            if (x == y) {
                return false;
            }

            parent[x] = y;
            count--;
            return true;
        }

        public int getCount() {
            return count;
        }
    }
}
