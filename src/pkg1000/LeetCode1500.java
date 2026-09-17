package pkg1000;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeetCode1500 {
    /**
     * 1520. 最多的不重叠子字符串
     * @param s
     * @return
     */
    public List<String> maxNumOfSubstrings(String s) {
        // ============================================================
        // 第一步：预处理每个字符的第一次和最后一次出现位置
        // 用 HashMap 记录每个字符的出现范围：
        //   key   -> 字符本身
        //   value -> int[2]，value[0] 为第一次出现位置，value[1] 为最后一次出现位置
        // ============================================================
        Map<Character, int[]> charIdx = new HashMap<>();
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (!charIdx.containsKey(ch)) {
                // 第一次遇到该字符，起始位置和结束位置都初始化为当前下标 i
                charIdx.put(ch, new int[]{i, i});
            } else {
                // 该字符已存在，只更新结束位置为当前下标 i（最终即为最后一次出现的位置）
                charIdx.get(ch)[1] = i;
            }
        }

        // ============================================================
        // 第二步：构建所有合法的区间
        // 合法区间的定义：
        //   如果子字符串中包含某个字符，那么该子字符串必须包含该字符在整串中的所有出现位置
        //   换句话说，区间内不能出现"部分包含"的情况
        // 构建方法：对每个字符的初始区间 [l, r] 进行扩展，
        //   扫描区间内的每个字符，如果该字符的出现范围超出了当前区间，就扩展区间，
        //   直到区间内所有字符的完整出现都被包含为止
        // ============================================================
        List<int[]> valid = new ArrayList<>();

        for (Map.Entry<Character, int[]> entry : charIdx.entrySet()) {
            // 获取当前字符的初始区间 [l, r]
            int[] range = entry.getValue();
            int l = range[0], r = range[1];

            // nl 和 nr 是两个扫描指针，都从 l 开始
            // nl 向左扫描（递减），nr 向右扫描（递增）
            // 通过双指针逐步扩展区间，确保区间内所有字符的完整出现都被包含
            int nl = l, nr = l;

            // 循环条件：只要还有未扫描的位置（nl >= l 表示左边还有未扫描的，nr <= r 表示右边还有未扫描的）
            while (nl >= l || nr <= r) {
                // 优先处理左边的指针，如果左边还有未扫描的位置，取 nl；否则取 nr
                int idx = (nl >= l) ? nl : nr;

                // 获取当前扫描位置字符的起始和结束下标
                int[] currIdx = charIdx.get(s.charAt(idx));
                // 该字符的起始和结束位置
                int l_t = currIdx[0], r_t = currIdx[1];

                // 如果该字符的起始位置在当前区间左侧之外，说明区间需要向左扩展
                // 扩展后 l 变小，可能会覆盖更多字符，需要继续检查
                if (l_t < l) {
                    l = l_t;
                }

                // 如果该字符的结束位置在当前区间右侧之外，说明区间需要向右扩展
                // 扩展后 r 变大，可能会覆盖更多字符，需要继续检查
                if (r_t > r) {
                    r = r_t;
                }

                // 如果当前处理的是左指针位置，左指针向左移动一位（确保 nl 在合法范围内）
                if (idx == nl) {
                    nl--;
                }

                // 如果当前处理的是右指针位置，右指针向右移动一位（确保 nr 在合法范围内）
                if (idx == nr) {
                    nr++;
                }
            }

            // 区间不再扩展，得到一个合法区间 [l, r]
            // 该区间内的所有字符的完整出现都被包含在区间内
            valid.add(new int[]{l, r});
        }

        // ============================================================
        // 第三步：按右端点升序排序
        // 为后续贪心算法做准备：优先选择结束位置早的区间，可以留出更多空间给后续区间
        // ============================================================
        valid.sort((a, b) -> a[1] - b[1]);

        // ============================================================
        // 第四步：贪心选择互不重叠的区间
        // 经典的活动选择问题：按结束时间排序后，每次选择与上一个选中区间不重叠且结束最早的区间
        // 这样可以最大化选中区间的数量
        // ============================================================
        List<String> ans = new ArrayList<>();
        // 记录上一个选中区间的结束位置，初始为 -1 表示还没有选中任何区间
        int end = -1;

        for (int[] interval : valid) {
            int l = interval[0], r = interval[1];
            // 如果当前区间的起始位置 > 上一个选中区间的结束位置，说明不重叠，可以选中
            if (l > end) {
                // 截取子字符串并加入结果
                ans.add(s.substring(l, r + 1));
                // 更新上一个选中区间的结束位置
                end = r;
            }
        }

        return ans;
    }

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
