package pkg2000;

import java.util.Arrays;

public class LeetCode2300 {
    /**
     * 2333. 最小差值平方和
     * @param nums1
     * @param nums2
     * @param k1
     * @param k2
     * @return
     */
    public long minSumSquareDiff(int[] nums1, int[] nums2, int k1, int k2) {
        // 两个数组的操作次数可以合并使用（对 nums1 减或对 nums2 加效果相同）
        // 总操作次数上限 = k1 + k2
        long k = (long) k1 + k2;
        int n = nums1.length;

        // 第一步：计算每对元素的绝对差值，并求出所有差值的总和
        // 将差值直接存回 nums1 数组，节省额外空间
        long sum = 0;
        for (int i = 0; i < n; i++) {
            nums1[i] = Math.abs(nums1[i] - nums2[i]);  // 每对的差值
            sum += nums1[i];                           // 累加总差值
        }

        // 第二步：如果总差值 <= 总操作次数，说明可以把所有差值都消为 0
        // 此时最小平方和为 0，直接返回
        if (sum <= k) {
            return 0;
        }

        // 第三步：将差值数组按从大到小排序，方便后续「削峰」处理
        // 先升序排序，再反转得到降序数组 d
        Arrays.sort(nums1);
        int[] d = new int[n + 1];      // d[0..n-1] 存差值（降序），d[n]=0 作为哨兵，避免越界
        for (int i = 0; i < n; i++) {
            d[i] = nums1[n - 1 - i];   // 反转：最大的差值放在最前面
        }
        // 此时 d[0] >= d[1] >= ... >= d[n-1] >= d[n]=0

        // 第四步：贪心「削峰」——从最大的差值开始，逐层降低
        // 核心思想：每次把前 i 个最大的差值统一降到与第 i+1 个相同的高度
        for (int i = 1; i <= n; i++) {
            // 计算将前 i 个差值从 d[i-1] 降到 d[i] 所需的操作次数
            // 每个差值降低 (d[i-1] - d[i])，共 i 个差值
            long cost = (long) (d[i - 1] - d[i]) * i;

            if (cost > k) {
                // 剩余操作次数 k 不足以把前 i 个差值全部降到 d[i]
                // 说明最终答案的「峰值高度」落在 (d[i], d[i-1]] 之间

                // 将剩余 k 次操作平均分配给前 i 个差值：
                long q = k / i;           // 每个差值可以统一降低 q
                long r = k % i;           // 还剩下 r 次操作，可以再多降低 1

                long hi = d[i - 1] - q;   // 降低 q 后的高度（较高的那一档）
                // 前 i 个差值中：
                //   • (i - r) 个差值降到 hi
                //   • r 个差值降到 hi - 1（多用 1 次操作）

                // 计算前 i 个差值的平方和贡献
                long ans = hi * hi * (i - r) + (hi - 1) * (hi - 1) * r;

                // 加上剩余差值（d[i] 及之后）的平方和，它们没有被操作过
                for (int j = i; j < n; j++) {
                    ans += (long) d[j] * d[j];
                }

                return ans;
            }

            // 当前层可以完全削平，消耗 cost 次操作，继续处理下一层
            k -= cost;
        }

        // 所有差值都能被消为 0（理论上前面 sum <= k 的判断已经覆盖，这里作为兜底）
        return 0;
    }
}
