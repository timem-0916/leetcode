package pkg1000;

import java.util.Arrays;

import util.Constant;

public class LeetCode1600 {
    /**
     * 1621. 大小为 K 的不重叠线段的数目
     * @param n
     * @param k
     * @return
     */
    public int numberOfSets(int n, int k) {
        // dp[j]：当前阶段（已放置 i 个集合）时，第 j 个位置结尾的方案数
        int[] dp = new int[n];
        // prefixSums[j+1] = dp[0] + dp[1] + ... + dp[j]，用于 O(1) 求前缀和
        int[] prefixSums = new int[n + 1];

        // 初始化阶段：i = 0，即还没有放置任何集合时
        // 每个位置 j 的方案数都是 1（空集的唯一方案）
        for (int j = 0; j < n; j++) {
            dp[j] = 1;
            // 构建前缀和数组
            prefixSums[j + 1] = (prefixSums[j] + dp[j]) % Constant.MOD;
        }

        // 动态规划主循环：依次放置第 1 个、第 2 个、...、第 k 个集合
        for (int i = 1; i <= k; i++) {
            // 第 0 个位置无法放置集合（至少需要 1 个元素），方案数为 0
            dp[0] = 0;

            // 状态转移：dp[j] = dp[j-1] + prefixSums[j]
            // dp[j-1]：第 j 个位置不选，继承前一个位置的方案数
            // prefixSums[j]：第 j 个位置选，且当前集合的起始位置可以是 0~j-1 中的任意一个
            //                即 sum(dp[0] + dp[1] + ... + dp[j-1])
            for (int j = 1; j < n; j++) {
                dp[j] = (dp[j - 1] + prefixSums[j]) % Constant.MOD;
            }

            // 本轮 dp 计算完成后，重新构建前缀和数组，供下一轮 i+1 使用
            for (int j = 0; j < n; j++) {
                prefixSums[j + 1] = (prefixSums[j] + dp[j]) % Constant.MOD;
            }
        }

        // 返回第 n-1 个位置结尾、放置了 k 个集合的方案数
        return dp[n - 1];
    }

    /**
     * 1658. 将 x 减到 0 的最小操作数
     * @param nums
     * @param x
     * @return
     */
    public int minOperations(int[] nums, int x) {
        // target 中间子数组的目标和
        int n = nums.length, target = Arrays.stream(nums).sum() - x;
        // target < 0 说明 x > totalSum，不可能完成，返回 -1
        if (target < 0) {
            return -1;
        }
        // 处理 target == 0 的情况，需要移除整个数组
        if (target == 0) {
            return n;
        }

        int subSum = 0, maxLen = 0;
        // 滑动窗口：找和为 target 的最长子数组
        for (int l = 0, r = 0; r < n; r++) {
            subSum += nums[r];
            // 当窗口和超过 target 时，收缩左边界
            while (subSum > target && l <= r) {
                subSum -= nums[l++];
            }
            // 如果窗口和恰好等于 target，更新最大长度
            if (subSum == target) {
                maxLen = Math.max(maxLen, r - l + 1);
            }
        }
        
        // 如果没找到合法子数组，返回 -1；否则返回需要移除的元素个数
        return maxLen == 0 ? -1 : n - maxLen;
    }
}
