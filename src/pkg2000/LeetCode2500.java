package pkg2000;

import java.util.ArrayList;
import java.util.List;

public class LeetCode2500 {
    /**
     * 2523. 范围内最接近的两个质数
     * 
     * 在区间 [left, right] 内找到差值最小的一对相邻质数。
     *
     * 算法思路：
     *   1. 先用埃氏筛法筛出 2 ~ right 范围内的所有质数。
     *   2. 用二分查找定位第一个 >= left 的质数下标。
     *   3. 在 [left, right] 范围内的质数中，遍历所有相邻质数对，记录差值最小的一对。
     *
     * @param left  区间左端点（包含）
     * @param right 区间右端点（包含）
     * @return 差值最小的一对相邻质数 [p1, p2]；若不存在则返回 [-1, -1]
     */
    public int[] closestPrimes(int left, int right) {
        // ========== 第一步：埃氏筛法，筛出 2 ~ right 的所有质数 ==========

        // 标记数组：notPrime[i] 为 true 表示 i 不是质数（已被筛掉）
        boolean[] notPrime = new boolean[right + 1];

        // 用于存放所有筛出的质数（按升序排列）
        List<Integer> primes = new ArrayList<>();

        // 从 2 开始遍历到 right
        for (int i = 2; i <= right; i++) {
            // 如果 i 没有被标记为非质数，说明 i 是质数
            if (!notPrime[i]) {
                // 将质数加入列表
                primes.add(i);
                // 将 i 的所有倍数标记为非质数（从 2*i 开始）
                for (int j = 2; i * j <= right; j++) {
                    notPrime[i * j] = true;
                }
            }
        }

        // ========== 第二步：在 [left, right] 范围内找差值最小的相邻质数对 ==========

        // 初始化答案为 [-1, -1]，表示尚未找到有效解
        int[] ans = {-1, -1};

        // 用 lowerBound 找到第一个 >= left 的质数在 primes 列表中的下标
        // 从该下标开始，遍历所有相邻质数对 (primes[i], primes[i+1])
        for (int i = lowerBound(primes, left); i < primes.size() - 1 && primes.get(i + 1) <= right; i++) {
            // 如果尚未找到任何解（ans[0] < 0），
            // 或者当前相邻质数对的差值比之前记录的最小差值更小，则更新答案
            if (ans[0] < 0 || primes.get(i + 1) - primes.get(i) < ans[1] - ans[0]) {
                // 记录较小的质数
                ans[0] = primes.get(i);
                // 记录较大的质数
                ans[1] = primes.get(i + 1);
            }
        }

        return ans;
    }

    /**
     * 二分查找 lower bound：在有序列表中找到第一个 >= target 的元素下标。
     *
     * @param list   有序列表（升序）
     * @param target 目标值
     * @return 第一个 >= target 的元素下标；若所有元素都 < target，则返回 list.size()
     */
    private int lowerBound(List<Integer> list, int target) {
        int l = 0, r = list.size();
        while (l < r) {
            // 防溢出的中点计算：等价于 (l + r) / 2，但避免了 l + r 可能溢出
            int m = l + ((r - l) >>> 1);
            int val = list.get(m);
            if (val < target) {
                // 当前值太小，答案一定在右半部分 [m+1, r)
                l = m + 1;
            } else {
                // 当前值 >= target，m 可能是答案，答案在左半部分 [l, m]
                r = m;
            }
        }
        return l;
    }
}
