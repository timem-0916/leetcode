package pkg2000;

import java.util.List;

public class LeetCode2800 {
    /**
     * 2897. 对数组执行操作使平方和最大
     * @param nums
     * @param k
     * @return
     */
    public int maxSum(List<Integer> nums, int k) {
        // 题目要求结果对 10^9+7 取模
        final int MOD = 1000000007;

        // cnt[i] 记录所有数字中，第 i 个二进制位上值为 1 的个数
        // 因为 nums[i] <= 10^9 < 2^30，所以只需要统计 30 个二进制位
        int[] cnt = new int[30];

        // 第一步：遍历所有数字，统计每一位上 1 的总数量
        for (int num : nums) {
            for (int i = 0; i < 30; i++) {
                // (1 << i) 生成只有第 i 位为 1 的掩码
                // num & (1 << i) != 0 说明 num 的第 i 位是 1
                if ((num & (1 << i)) != 0) {
                    cnt[i]++;
                }
            }
        }

        // ans 用于累加每次操作后得到的最大值的平方
        long ans = 0;

        // 第二步：执行 k 次操作，每次构造一个尽可能大的数字
        for (int i = 0; i < k; i++) {
            long cur = 0;

            // 从高位到低位贪心地构造当前数字
            // 高位对数值的贡献更大，所以优先把高位设为 1
            for (int j = 29; j >= 0; j++) {
                // 如果第 j 位还有剩余的 1 可用
                if (cnt[j] > 0) {
                    // 将 cur 的第 j 位设为 1
                    // 用 1L 确保左移后是 long 类型，避免 int 溢出
                    cur |= (1L << j);
                    // 该位的 1 被消耗掉一个，计数减 1
                    cnt[j]--;
                }
            }

            // 将当前构造出的最大值的平方累加到结果中
            // 每次累加后立即取模，防止 ans 溢出
            ans = (ans + cur * cur) % MOD;
        }

        // 最终结果在 int 范围内，强转返回
        return (int) ans;
    }
}
