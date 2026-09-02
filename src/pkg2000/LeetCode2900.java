package pkg2000;

public class LeetCode2900 {
    /**
     * 2904. 最短且字典序最小的美丽子字符串
     * @param s
     * @param k
     * @return
     */
    public String shortestBeautifulSubstring(String s, int k) {
        // 特判：如果整个字符串中 1 的个数都不够 k，直接返回空
        if (s.replace("0", "").length() < k) {
            return "";
        }

        int n = s.length(), cnt1 = 0;
        // 初始化为整个字符串，作为最坏情况下的兜底
        String ans = s;

        for (int l = 0, r = 0; r < n; r++) {
            // 1. 扩大窗口：将右边界字符加入统计
            if (s.charAt(r) == '1') {
                cnt1++;
            }

            // 2. 收缩窗口：如果 1 的个数超过 k，或者左边界是 '0'（为了追求最短）
            while (cnt1 > k || s.charAt(l) == '0') {
                if (s.charAt(l++) == '1') {
                    cnt1--;
                }
            }

            // 3. 更新答案：当窗口内恰好有 k 个 1 时
            if (cnt1 == k) {
                String temp = s.substring(l, r + 1);
                // 优先比较长度，长度相同再比较字典序（Java 的 compareTo 天然支持字典序比较）
                if (temp.length() < ans.length() || (temp.length() == ans.length() && temp.compareTo(ans) < 0)) {
                    ans = temp;
                }
            }
        }

        return ans;
    }

    /**
     * 2940. 找到 Alice 和 Bob 可以相遇的建筑
     * @param heights
     * @param queries
     * @return
     */
    public int[] leftmostBuildingQueries(int[] heights, int[][] queries) {
        int n = heights.length;
        // 1. 初始化线段树，用于维护区间内的最大高度
        int[] segmentTree = new int[n * 4];
        build(segmentTree, 0, n - 1, 0, heights);

        int m = queries.length;
        int[] ans = new int[m];
        for (int i = 0; i < m; i++) {
            int a = queries[i][0], b = queries[i][1];

            // 2. 逻辑预处理：确保 a 是较小的索引，b 是较大的索引
            // 因为题目要求从 a 到 b，如果 a > b，则相当于在 b 的左侧寻找
            // 统一处理为：在 b 的右侧（或 b 本身）寻找目标
            if (a > b) {
                int temp = a;
                a = b;
                b = temp;
            }

            // 3. 快速判断：如果 a == b，或者 a 的高度已经小于 b 的高度
            // 那么 b 本身就是满足条件的最左侧建筑，无需查询线段树
            if (a == b || heights[a] < heights[b]) {
                ans[i] = b;
                continue;
            }

            // 4. 核心查询：在 [b + 1, n - 1] 区间内，寻找第一个高度 > heights[a] 的建筑
            // 如果找不到，query 方法会返回 -1
            ans[i] = query(segmentTree, b + 1, heights[a], 0, n - 1, 0);
        }

        return ans;
    }

    /**
     * 构建线段树（区间最大值）
     * @param segmentTree 线段树数组
     * @param l 当前节点管理的区间左端点
     * @param r 当前节点管理的区间右端点
     * @param rt 当前节点在 segmentTree 数组中的索引
     * @param heights 原始高度数组
     */
    private void build(final int[] segmentTree, int l, int r, int rt, int[] heights) {
        // 递归终止条件：到达叶子节点，直接存储原数组的高度
        if (l == r) {
            segmentTree[rt] = heights[l];
            return;
        }
        // 计算中点，使用位运算 >> 1 代替除以 2
        int m = (l + r) >> 1;
        // 递归构建左子树和右子树
        build(segmentTree, l, m, rt * 2 + 1, heights);
        build(segmentTree, m + 1, r, rt * 2 + 2, heights);
        // 核心逻辑：当前节点的值 = 左右子树的最大值
        segmentTree[rt] = Math.max(segmentTree[rt * 2 + 1], segmentTree[rt * 2 + 2]);
    }

    /**
     * 在线段树中查询：从 pos 位置开始，寻找第一个高度 > val 的索引
     * @param segmentTree 线段树数组
     * @param pos 查询的起始位置（即 b + 1）
     * @param val 目标高度阈值（即 heights[a]）
     * @param l 当前节点管理的区间左端点
     * @param r 当前节点管理的区间右端点
     * @param rt 当前节点在 segmentTree 数组中的索引
     * @return 满足条件的最左侧索引，如果不存在返回 -1
     */
    private int query(final int[] segmentTree, int pos, int val, int l, int r, int rt) {
        // 1. 剪枝：如果当前区间的最大高度都 <= val，说明这个区间内没有符合条件的建筑
        // 直接返回 -1，避免无效递归
        if (val >= segmentTree[rt]) {
            return -1;
        }

        // 2. 递归终止条件：到达叶子节点，说明该位置的高度 > val，直接返回索引
        if (l == r) {
            return l;
        }

        int m = (l + r) >> 1;
        // 3. 核心查找逻辑：优先在左子树寻找，因为题目要求“最左侧”
        if (pos <= m) {
            // 尝试在左子树 [l, m] 中寻找
            int res = query(segmentTree, pos, val, l, m, rt * 2 + 1);
            // 如果在左子树找到了结果（res >= 0），直接返回，无需再查右子树
            if (res >= 0) {
                return res;
            }
        }
        
        // 4. 如果左子树没找到，或者起始位置 pos 已经在右子树范围内，则去右子树寻找
        return query(segmentTree, pos, val, m + 1, r, rt * 2 + 2);
    }
}
