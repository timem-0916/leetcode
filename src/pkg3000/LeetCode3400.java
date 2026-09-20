package pkg3000;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LeetCode3400 {

    /**
     * 3414. 不重叠区间的最大得分
     * @param intervals
     * @return
     */
    public int[] maximumWeight(List<List<Integer>> intervals) {
        int n = intervals.size();

        // 将区间转换为 [l, r, weight, originalIndex]，并按右端点排序
        int[][] intervalArray = new int[n][4];
        for (int i = 0; i < n; i++) {
            intervalArray[i][0] = intervals.get(i).get(0);
            intervalArray[i][1] = intervals.get(i).get(1);
            intervalArray[i][2] = intervals.get(i).get(2);
            intervalArray[i][3] = i;
        }
        Arrays.sort(intervalArray, (a, b) -> a[1] - b[1]);

        // dp[i][j] 表示在前 i 个区间中至多选择 j 个不重叠区间的最大得分和，其中 i 的范围是 [0,n]，j 的范围是 [0,4]
        long[][] dp = new long[n + 1][5];
        // indices[i][j]：对应的字典序最小的下标组合
        @SuppressWarnings("unchecked")
        List<Integer>[][] indices = new List[n + 1][5];
        for (int i = 0; i <= n; i++) {
            for (int j = 0; j < 5; j++) {
                indices[i][j] = new ArrayList<>();
            }
        }

        for (int i = 0; i < n; i++) {
            int l = intervalArray[i][0], weight = intervalArray[i][2], idx = intervalArray[i][3];
            // 二分查找找到小于 l 的区间
            int k = binarySearch(intervalArray, i, l);

            for (int j = 1; j < 5; j++) {
                // 选项1：不选第 i + 1 个区间，继承 dp[i][j]
                long s1 = dp[i][j];
                // 选项2：选第 i + 1 个区间，二分查找不与它重叠的前驱
                long s2 = dp[k][j - 1] + weight;
                if (s1 > s2) {
                    dp[i + 1][j] = dp[i][j];
                    indices[i + 1][j] = new ArrayList<>(indices[i][j]);
                    continue;
                }

                List<Integer> newIndex = new ArrayList<>(indices[k][j - 1]);
                newIndex.add(idx);
                Collections.sort(newIndex);
                if (s1 == s2 && compareLex(indices[i][j], newIndex) < 0) {
                    newIndex = new ArrayList<>(indices[i][j]);
                }
                dp[i + 1][j] = s2;
                indices[i + 1][j] = newIndex;
            }
        }

        List<Integer> result = indices[n][4];
        int[] ans = new int[result.size()];
        for (int i = 0; i < result.size(); i++) {
            ans[i] = result.get(i);
        }

        return ans;
    }

    /**
     * 二分查找：在 intervalArray[0..right][1] 中找到最后一个 < target 的位置
     * 即找到第一个 >= target 的位置，然后 -1
     * @param intervalArray
     * @param right
     * @param target
     * @return
     */
    private int binarySearch(int[][] intervalArray, int end, int target) {
        int l = 0, r = end;
        while (l < r) {
            int mid = (l + r) >> 1;
            if (intervalArray[mid][1] < target) {
                l = mid + 1;
            } else {
                r = mid;
            }
        }
        return l;
    }

    /**
     * 字典序比较：a < b 返回负数，a == b 返回 0，a > b 返回正数
     * @param a
     * @param b
     * @return
     */
    private int compareLex(List<Integer> a, List<Integer> b) {
        int len = Math.min(a.size(), b.size());
        for (int i = 0; i < len; i++) {
            if (!a.get(i).equals(b.get(i))) {
                return Integer.compare(a.get(i), b.get(i));
            }
        }
        return a.size() - b.size();
    }

    /**
     * 3479. 水果成篮 III
     * @param fruits
     * @param baskets
     * @return
     */
    public int numOfUnplacedFruits(int[] fruits, int[] baskets) {
        int m = baskets.length, count = 0;
        // 如果没有篮子，所有水果都无法放置
        if (m == 0) {
            return fruits.length;
        }

        // 初始化线段树并构建
        int[] segTree = new int[m * 4];
        Arrays.fill(segTree, Integer.MIN_VALUE);
        build(segTree, baskets, 1, 0, m - 1);

        // 遍历每一个水果，尝试将其放入篮子
        for (int fruit : fruits) {
            // 核心优化：直接在线段树中查找最左侧 >= fruit 的叶子节点索引
            // 如果找不到，返回 -1
            int pos = findFirst(segTree, baskets, 1, 0, m - 1, fruit);

            if (pos != -1) {
                // 找到了合适的篮子，将其标记为已占用（更新为极小值）
                update(segTree, baskets, 1, 0, m - 1, pos, Integer.MIN_VALUE);
            } else {
                // 找不到合适的篮子，未放置水果计数 +1
                count++;
            }
        }

        return count;
    }

    /**
     * 构建线段树
     * @param segTree
     * @param baskets
     * @param p 当前节点在线段树数组中的索引
     * @param l 当前节点代表的区间左边界
     * @param r 当前节点代表的区间右边界
     */
    private void build(int[] segTree, int[] baskets, int p, int l, int r) {
        // 叶子节点，直接赋值为对应篮子的容量
        if (l == r) {
            segTree[p] = baskets[l];
            return;
        }
        // 等价于 (l + r) / 2
        int m = (l + r) >> 1;
        // 递归构建左子树和右子树
        build(segTree, baskets, p << 1, l, m);
        build(segTree, baskets, p << 1 | 1, m + 1, r);
        // 当前节点的值为其左右子树的最大值（维护区间最大值）
        segTree[p] = Math.max(segTree[p << 1], segTree[p << 1 | 1]);
    }

    /**
     * 【核心优化方法】在线段树中查找最左侧 >= target 的叶子节点索引
     * 时间复杂度：O(log M)
     * @param segTree
     * @param baskets
     * @param p
     * @param l
     * @param r
     * @param target
     * @return
     */
    private int findFirst(int[] segTree, int[] baskets, int p, int l, int r, int target) {
        // 剪枝：如果当前区间的最大值都小于 target，说明这个区间内没有合法篮子
        if (segTree[p] < target) {
            return -1;
        }

        // 如果到达叶子节点，说明找到了最左侧的合法篮子
        if (l == r) {
            return l;
        }

        int m = (l + r) >> 1;
        // 【关键逻辑】：优先向左子树寻找！
        // 因为左子树代表更小的索引，如果左子树的最大值 >= target，答案一定在左边
        if (segTree[p << 1] >= target) {
            return findFirst(segTree, baskets, p << 1, l, m, target);
        } else {
            // 左子树不行，只能去右子树找
            return findFirst(segTree, baskets, p << 1 | 1, m + 1, r, target);
        }
    }

    /**
     * 单点更新
     * @param segTree
     * @param baskets
     * @param p
     * @param l
     * @param r
     * @param pos 需要更新的篮子索引
     * @param val 更新后的值（本题中更新为 Integer.MIN_VALUE 表示该篮子已被占用）
     */
    private void update(int[] segTree, int[] baskets, int p, int l, int r, int pos, int val) {
        // 找到叶子节点，进行更新
        if (l == r) {
            segTree[p] = val;
            return;
        }
        int m = (l + r) >> 1;
        // 根据 pos 的位置决定向左还是向右递归
        if (pos <= m) {
            update(segTree, baskets, p << 1, l, m, pos, val);
        } else {
            update(segTree, baskets, p << 1 | 1, m + 1, r, pos, val);
        }
        // 回溯时重新计算当前节点的最大值
        segTree[p] = Math.max(segTree[p << 1], segTree[p << 1 | 1]);
    }

    /**
     * 3483. 不同三位偶数的数目
     * @param digits
     * @return
     */
    public int totalNumbers(int[] digits) {
        int n = digits.length;
        boolean[] visited = new boolean[1000];
        int ans = 0;

        for (int i = 0; i < n; i++) {
            if (digits[i] == 0) {
                continue;
            }
            for (int j = 0; j < n; j++) {
                if (j == i) {
                    continue;
                }
                for (int k = 0; k < n; k++) {
                    if (k == j || k == i || (digits[k] & 1) == 1) {
                        continue;
                    }
                    int x = digits[i] * 100 + digits[j] * 10 + digits[k];
                    if (!visited[x]) {
                        visited[x] = true;
                        ans++;
                    }
                }
            }
        }
        
        return ans;
    }

    /**
     * 3498. 字符串的反转度
     * @param s
     * @return
     */
    public int reverseDegree(String s) {
        char[] sArray = s.toCharArray();
        int n = sArray.length, ans = 0;
        for (int i = 0; i < n; i++) {
            ans += (i + 1) * (26 - (sArray[i] - 'a'));
        }
        return ans;
    }
}
