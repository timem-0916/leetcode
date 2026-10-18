package pkg2000;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

import util.TreeNode;

public class LeetCode2200 {

    /**
     * 2203. 包含要求路径的最小带权子图
     * 
     * 核心思想：枚举汇聚点。两条路径在到达终点前，必然会在某个节点 i 汇合。
     * 总代价 = src1 到 i 的最短距离 + src2 到 i 的最短距离 + i 到 dest 的最短距离
     * @param n
     * @param edges
     * @param src1
     * @param src2
     * @param dest
     * @return
     */
    @SuppressWarnings("unchecked")
    public long minimumWeight(int n, int[][] edges, int src1, int src2, int dest) {
        // 1. 使用邻接表建图，空间复杂度为 O(N + M)，且天然支持重边（多条边会被全部存入列表）
        // 正向图：用于计算 src1 和 src2 到各点的距离
        // 反向图：用于计算 dest 到各点的距离
        List<int[]>[] g = new List[n], f = new List[n];

        // 初始化邻接表
        for (int i = 0; i < n; i++) {
            g[i] = new ArrayList<int[]>();
            f[i] = new ArrayList<int[]>();
        }

        // 遍历所有边，分别加入正向图和反向图
        for (int[] e : edges) {
            int u = e[0], v = e[1], w = e[2];
            // 正向边：u -> v
            g[u].add(new int[]{v, w});
            // 反向边：v -> u
            f[v].add(new int[]{u, w});
        }

        // 2. 分别以 src1, src2 为起点，在正向图上跑 Dijkstra
        long[] dist1 = dijkstra(g, src1);
        long[] dist2 = dijkstra(g, src2);

        // 以 dest 为起点，在反向图上跑 Dijkstra，等价于求原图中所有点到 dest 的最短距离
        long[] dist3 = dijkstra(f, dest);

        // 3. 枚举所有可能的汇聚点 i，寻找最小的总代价
        // 防止相加时发生 long 溢出
        final long INF = Long.MAX_VALUE / 2;
        long ans = INF;
        for (int i = 0; i < n; i++) {
            // 只有当 src1, src2, dest 都能到达节点 i 时，才尝试更新答案
            if (dist1[i] != INF && dist2[i] != INF && dist3[i] != INF) {
                ans = Math.min(ans, dist1[i] + dist2[i] + dist3[i]);
            }
        }

        // 如果 ans 依然是 INF，说明无法连通，返回 -1
        return ans == INF ? -1 : ans;
    }

    /**
     * 标准的 Dijkstra 算法（优先队列优化版）
     * @param graph 邻接表表示的图
     * @param start 起始节点
     * @return 从 start 到图中所有其他节点的最短距离数组
     */
    private long[] dijkstra(List<int[]>[] graph, int start) {
        int n = graph.length;
        final long INF = Long.MAX_VALUE / 2;
        // 记录起点到各点的最短距离
        long[] dist = new long[n];
        Arrays.fill(dist, INF);
        // 起点到自身的距离为 0
        dist[start] = 0;

        // 小顶堆，存储 {节点编号, 当前累计距离}
        // 注意：必须使用 Long.compare 避免 long 类型直接相减导致的溢出
        PriorityQueue<long[]> pq = new PriorityQueue<>((a, b) -> Long.compare(a[1], b[1]));
        pq.offer(new long[]{start, 0});
        while (!pq.isEmpty()) {
            long[] curr = pq.poll();
            int u = (int) curr[0];
            long len = curr[1];

            // 惰性删除：如果当前弹出的距离已经大于记录的最短距离，说明这是旧数据，直接跳过
            if (len > dist[u]) {
                continue;
            }

            // 遍历当前节点 u 的所有邻居
            for (int[] next : graph[u]) {
                int v = next[0], w = next[1];
                // 松弛操作：如果经过 u 到达 v 的距离更短，则更新并加入优先队列
                if (dist[u] + w < dist[v]) {
                    dist[v] = dist[u] + w;
                    pq.offer(new long[]{v, dist[v]});
                }
            }
        }

        return dist;
    }

    /**
     * 2218. 从栈中取出 K 个硬币的最大面值和（分组背包 DP）
     *
     * 核心思路：
     *   1. 每堆硬币只能从顶部连续取，取前 p 个的价值 = 前缀和 cost[i][p]。
     *   2. 问题转化为分组背包：n 组物品，第 i 组可选 0~cnt[i] 个，总容量 k，求最大价值。
     *   3. dp[i][j] 表示从前 i 堆中恰好取 j 个硬币能获得的最大面值和。
     *   4. 转移：枚举第 i 堆取 p 个，dp[i][j] = max(cost[i-1][p] + dp[i-1][j-p])。
     *
     * 时间复杂度 O(n * k * max(cnt[i]))，空间复杂度 O(n * k)。
     * 
     * @param piles
     * @param k
     * @return
     */
    @SuppressWarnings("unchecked")
    public int maxValueOfCoins(List<List<Integer>> piles, int k) {
        // 一共有 n 个硬币栈
        int n = piles.size();

        // cost[i][j] -> 从第 i 堆中选取前 j 个硬币的面值总和（前缀和）
        // cost[i][0] = 0，表示从第 i 堆取 0 个硬币，价值为 0
        List<Integer>[] cost = new List[n];

        // cnt[i] -> 第 i 堆中有多少枚硬币
        int[] cnt = new int[n];

        // 预处理：计算每堆的前缀和，方便 O(1) 查询"取前 p 个的价值"
        for (int i = 0; i < n; i++) {
            cnt[i] = piles.get(i).size();
            cost[i] = new ArrayList<>();
            // 取 0 个，价值为 0
            cost[i].add(0);
            for (int j = 0; j < piles.get(i).size(); j++) {
                // 前 j+1 个的价值 = 前 j 个的价值 + 第 j 个硬币的面值
                cost[i].add(piles.get(i).get(j) + cost[i].getLast());
            }
        }

        // dp[i][j] -> 从前 i 堆中恰好取 j 个硬币，能获得的最大面值和
        // 默认值为 0，表示不取任何硬币时价值为 0
        int[][] dp = new int[n + 1][k + 1];

        // 动态规划：逐堆考虑，枚举取多少个
        for (int i = 1; i <= n; i++) {
            // 第 i 堆的硬币总数
            int m = cnt[i - 1];
            for (int j = 1; j <= k; j++) {
                // 先继承“第 i 堆取 0 个”的情况
                dp[i][j] = dp[i - 1][j];

                // 第 i 堆最多有 cnt[i-1] 个硬币（注意 piles 下标从 0 开始）
                // 如果第 i 堆的硬币数 >= j，才有可能从这一堆取 j 个
                // 实际上这个判断可以去掉，因为下面的 p <= Math.min(j, m) 已经做了限制
                for (int p = 0; p <= Math.min(j, m); p++) {
                    // 从第 i 堆取 p 个，价值为 cost[i-1][p]
                    // 从前 i-1 堆取 j-p 个，价值为 dp[i-1][j-p]
                    dp[i][j] = Math.max(dp[i][j], cost[i - 1].get(p) + dp[i - 1][j - p]);
                }
            }
        }

        // 从前 n 堆中恰好取 k 个硬币的最大面值和
        return dp[n][k];
    }

    /**
     * 2236. 判断根结点是否等于子结点之和
     * @param root
     * @return
     */
    public boolean checkTree(TreeNode root) {
        return root.val == root.left.val + root.right.val;
    }

    /**
     * 2265. 统计值等于子树平均值的节点数
     * @param root
     * @return
     */
    public int averageOfSubtree(TreeNode root) {
        int[] cnt = {0};
        averageOfSubtreeDfs(root, cnt);
        return cnt[0];
    }

    /**
     * 深度优先搜索
     * @param node
     * @param cnt
     * @return
     */
    private int[] averageOfSubtreeDfs(TreeNode node, int[] cnt) {
        if (node == null) {
            return new int[]{0, 0};
        }
        int[] left = averageOfSubtreeDfs(node.left, cnt);
        int[] right = averageOfSubtreeDfs(node.right, cnt);
        int size = left[0] + right[0] + 1;
        int sum = left[1] + right[1] + node.val;
        if (sum / size == node.val) {
            cnt[0]++;
        }
        return new int[]{size, sum};
    }

}
