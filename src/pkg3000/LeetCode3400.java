package pkg3000;

import java.util.Arrays;

public class LeetCode3400 {
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
}
