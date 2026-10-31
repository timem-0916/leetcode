package pkg3000;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

public class LeetCode3900 {
    /**
     * 3903. 最小稳定下标 I
     * @param nums
     * @param k
     * @return
     */
    public int firstStableIndex(int[] nums, int k) {
        int n = nums.length;
        int[] max = new int[n], min = new int[n];
        max[0] = nums[0];
        min[n - 1] = nums[n - 1];
        for (int i = 1; i < n; i++) {
            max[i] = Math.max(max[i - 1], nums[i]);
            min[n - i - 1] = Math.min(min[n - i], nums[n - i - 1]);
        }

        for (int i = 0; i < n; i++) {
            int v = max[i] - min[i];
            if (v <= k) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 3904. 最小稳定下标 II
     * @param nums
     * @param k
     * @return
     */
    public int firstStableIndexII(int[] nums, int k) {
        int n = nums.length;
        int[] min = new int[n];
        min[n - 1] = nums[n - 1];
        for (int i = n - 2; i >= 0; i--) {
            min[i] = Math.min(min[i + 1], nums[i]);
        }

        for (int i = 0, curMax = -1; i < n; i++) {
            curMax = Math.max(curMax, nums[i]);
            int curMin = min[i];
            if (curMax - curMin <= k) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 3942. 排序排列的最少操作数
     * 
     * 计算将环形数组变为有序所需的最小操作数。
     *
     * 题目约定数组是 [0, 1, 2, ..., n-1] 的一个环形排列，
     * 操作定义为：每次可以把数组整体循环左移或右移一位。
     * 目标状态有两种：
     *   1. 升序：[0, 1, 2, ..., n-1]
     *   2. 降序：[n-1, n-2, ..., 1, 0]
     * 返回达成任意一种目标所需的最小移动次数；若两种都不可达，返回 -1。
     * 
     * @param nums
     * @return
     */
    public int minOperations(int[] nums) {
        final int n = nums.length;

        // 只有一个元素，天然有序，无需操作
        if (n == 1) {
            return 0;
        }

        // 找到元素 0 的下标，作为环形遍历的“起点”
        int start = 0;
        while (nums[start] != 0) {
            start++;
        }

        // asc：从 0 开始顺时针是否为严格升序 0,1,2,...,n-1
        boolean asc = true;
        // desc：从 0 开始顺时针是否为严格降序 0,n-1,n-2,...,1
        boolean desc = true;

        // 从 0 的下一个位置开始，沿环形依次检查每个位置的值
        for (int i = 1; i < n; i++) {
            // 环形取模：从 start 出发，第 i 个位置的实际下标
            final int value = nums[(start + i) % n];

            // 升序期望：第 i 个位置的值应为 i
            if (value != i) {
                asc = false;
            }

            // 降序期望：第 i 个位置的值应为 n - i
            // 即序列为 0, n-1, n-2, ..., 1
            if (value != n - i) {
                desc = false;
            }

            // 既不是升序也不是降序，无法通过循环移位变成有序
            if (!asc && !desc) {
                return -1;
            }
        }

        if (asc) {
            // 升序可达：
            //   方案1：循环左移 start 次，把 0 移到下标 0
            //   方案2：循环右移 n - start 次；
            //          代码中写作 n - start + 2，可能是题目对“右移”代价的特殊定义
            //          （例如右移一次计为 2 步，或包含额外两次翻转操作）
            return Math.min(start, n - start + 2);
        }

        // 降序可达：
        //   方案1：循环右移 n - start 次，把 0 移到下标 0 后整体反转
        //   方案2：循环左移 start 次后再做两次额外操作
        return Math.min(n - start, start + 2);
    }

    /**
     * 3963. 构造恰好一条路径的网格
     * @param m
     * @param n
     * @return
     */
    public String[] createGrid(int m, int n) {
        String[] ans = new String[m];
        char[] buff = new char[n];
        Arrays.fill(buff, '.');
        ans[0] = String.valueOf(buff);
        Arrays.fill(buff, '#');
        buff[n - 1] = '.';
        String next = String.valueOf(buff);
        for (int i = 1; i < m; i++) {
            ans[i] = next;
        }
        return ans;
    }

    /**
     * 3970. 最多 K 个连续相同字符的最短路径
     * 
     * 求从节点 0 到节点 n-1 的最短路径长度，约束条件为：
     * 路径上不能有连续超过 k 个相同字母的节点。
     *
     * 算法思路：
     *   在经典 Dijkstra 最短路的基础上，扩展状态维度：
     *   状态为 (节点, 当前连续相同字母的个数)，即 dis[x][cnt] 表示到达节点 x 且末尾连续相同字母数为 cnt 时的最短距离。
     *   这样可以在状态转移时检查连续相同字母是否超过 k。
     *
     * @param n      节点数（节点编号 0 到 n-1）
     * @param edges  边的列表，每条边为 [起点, 终点, 权重]
     * @param labels 每个节点的字母标签（labels[i] 对应节点 i）
     * @param k      允许的最大连续相同字母个数
     * @return 从节点 0 到节点 n-1 的最短路径长度；若不可达则返回 -1
     */
    @SuppressWarnings("unchecked")
    public int shortestPath(int n, int[][] edges, String labels, int k) {
        // ========== 第一步：建图（邻接表）==========
        // 每个元素为 ArrayList<int[]>，int[] 为 {邻居节点, 边权重}
        List<int[]>[] g = new ArrayList[n];
        Arrays.setAll(g, i -> new ArrayList<>());
        for (int[] e : edges) {
            // 有向边：e[0] -> e[1]，权重为 e[2]
            g[e[0]].add(new int[]{e[1], e[2]});
        }

        // ========== 第二步：初始化距离数组 ==========
        // dis[x][cnt]：到达节点 x 且末尾连续相同字母数为 cnt 时的最短距离
        // 第二维大小为 k+1，因为连续个数最多为 k（超过 k 的状态会被剪枝）
        long[][] dis = new long[n][k + 1];
        for (long[] row : dis) {
            // 初始化为无穷大
            Arrays.fill(row, Integer.MAX_VALUE);
        }

        // ========== 第三步：初始化优先队列（小根堆）==========
        // 堆中元素为 long[] {最短距离, 节点编号, 末尾连续相同字母个数}
        // 按距离升序排列，保证 Dijkstra 每次取出当前最短的状态
        PriorityQueue<long[]> pq = new PriorityQueue<>((a, b) -> Long.compare(a[0], b[0]));

        // 起点：节点 0，距离为 0，连续相同字母数为 1（自身算 1 个）
        dis[0][1] = 0;
        pq.offer(new long[]{0, 0, 1});

        // ========== 第四步：Dijkstra 主循环 ==========
        while (!pq.isEmpty()) {
            long[] cur = pq.poll();
            // 当前状态的最短距离
            long d = cur[0];
            // 当前节点编号
            int x = (int) cur[1];
            // 到达当前节点时，末尾连续相同字母的个数
            int cnt = (int) cur[2];

            // 剪枝：如果当前距离已经大于记录的最短距离，说明是过期状态，跳过
            if (d > dis[x][cnt]) {
                continue;
            }

            // 若到达终点，直接返回最短距离（Dijkstra 保证首次到达终点时即为最短路）
            if (x == n - 1) {
                return (int) d;
            }

            // 遍历当前节点的所有出边
            for (int[] e : g[x]) {
                // 邻居节点
                int y = e[0];
                // 边权重
                int w = e[1];

                // 计算到达邻居节点 y 时的新连续相同字母数：
                // 如果当前节点 x 和邻居节点 y 的字母相同，则连续数 +1；否则重置为 1
                int newCnt = labels.charAt(x) == labels.charAt(y) ? cnt + 1 : 1;

                // 约束检查：如果连续相同字母数超过 k，则该状态非法，跳过
                if (newCnt > k) {
                    continue;
                }

                // 松弛操作：计算新距离
                long nd = d + w;
                if (nd < dis[y][newCnt]) {
                    // 更新最短距离
                    dis[y][newCnt] = nd;
                    // 将新状态加入优先队列
                    pq.offer(new long[]{nd, y, newCnt});
                }
            }
        }

        // 若循环结束仍未到达终点，说明不可达
        return -1;
    }
}
