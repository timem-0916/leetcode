package pkg1000;

public class LeetCode1100 {
    /**
     * 1191. K 次串联后最大子数组之和
     * @param arr
     * @param k
     * @return
     */
    public int kConcatenationMaxSum(int[] arr, int k) {
        // 1. 如果只串联 1 次，直接求单次最大子数组和即可
        if (k == 1) {
            return maxSubArray(arr, 1);
        }

        // 2. 如果串联 2 次及以上，最大和的“边界形态”一定在串联 2 次的数组中产生
        // （即前一个数组的尾部最大后缀 + 后一个数组的头部最大前缀）
        long ans = maxSubArray(arr, 2);

        // 3. 计算单次 arr 的总和 s
        int s = 0;
        for (int x : arr) {
            s += x;
        }

        // 4. 如果 s > 0，说明中间多串联的 (k - 2) 个完整的 arr 都能贡献正数收益
        // 直接将这部分收益加到 ans 上；如果 s <= 0，加上负数反而变小，所以取 Math.max(s, 0)
        ans += (long) Math.max(s, 0) * (k - 2);

        // 5. 题目要求对 10^9 + 7 取模，注意先转 long 再取模，防止溢出
        return (int) (ans % 1_000_000_007);
    }

    /**
     * 最大子数组和（遍历 nums repeat 次）
     * @param nums
     * @param repeat
     * @return
     */
    private int maxSubArray(int[] nums, int repeat) {
        // 题目允许子数组为空，所以初始化为 0（空子数组和为 0）
        int ans = 0;
        // f 代表以当前元素结尾的最大子数组和
        int f = 0;
        // 将数组重复遍历 repeat 次
        while (repeat-- > 0) {
            for (int x : nums) {
                // Kadane 算法核心：如果前面的和是负数，就丢弃
                f = Math.max(f, 0) + x;
                // 更新全局最大值
                ans = Math.max(ans, f);
            }
        }
        return ans;
    }

}
