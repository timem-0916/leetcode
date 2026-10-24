package pkg3000;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LeetCode3800 {

    /**
     * 3863. 将一个字符串排序的最小操作次数
     * 
     * 计算将字符串变为非递减有序所需的最小操作数。
     *
     * 操作定义：每次可以取出字符串中的任意一个字符，将其插入到字符串的任意位置（包括首尾）。
     * 目标：通过最少的移动操作，使字符串变为非递减（字典序升序）排列。
     * 
     * @param s
     * @return
     */
    public int minOperations(String s) {
        int n = s.length();

        // mx 记录字符串中的最大字符，mn 记录最小字符
        char mx = 'a', mn = 'z';

        // sorted 标记字符串是否已经是非递减有序
        boolean sorted = true;

        // 第一遍遍历：收集全局信息（最大/最小字符）并检查是否已有序
        for (int i = 0; i < n; i++) {
            char c = s.charAt(i);
            mx = (char) Math.max(mx, c);
            mn = (char) Math.min(mn, c);

            // 如果当前字符比前一个字符小，说明存在逆序，字符串尚未有序
            if (i > 0 && s.charAt(i - 1) > s.charAt(i)) {
                sorted = false;
            }
        }

        // 情况1：字符串已经非递减有序，无需任何操作
        if (sorted) {
            return 0;
        }

        // 特殊情况：长度为2且无序（如 "ba"），移动任意一个字符都无法使其有序
        // 因为移动后仍然是两个字符的排列，只有两种可能，都无序则无解
        if (n == 2) {
            return -1;
        }

        // 情况2：只需 1 次操作
        // 如果最小字符在首位，或最大字符在末位，说明"边界"已经就位
        // 只需移动一个"捣乱"的字符到合适位置即可恢复有序
        if (s.charAt(0) == mn || s.charAt(n - 1) == mx) {
            return 1;
        }

        // 情况3：只需 2 次操作
        // 如果最小字符或最大字符出现在字符串中间（非首尾位置）
        // 可以通过两次移动（例如先移出再插入到正确位置）来恢复有序
        for (int i = 1; i < n - 1; i++) {
            if (s.charAt(i) == mn || s.charAt(i) == mx) {
                return 2;
            }
        }

        // 情况4：需要 3 次操作
        // 最小和最大字符都不在首尾，也不在中间单独出现
        // 需要更多次移动才能恢复有序
        return 3;
    }

    /**
     * 3870. 统计范围内的逗号
     * @param n
     * @return
     */
    public int countCommas(int n) {
        return Math.max(0, n - 999);
    }

    /**
     * 3871. 统计范围内的逗号 II
     * @param n
     * @return
     */
    public long countCommasII(long n) {
        long p = 1000, ans = 0;
        while (p <= n) {
            ans += n - p + 1;
            p *= 1000;
        }
        return ans;
    }

    /**
     * 3875. 构造奇偶一致的数组 I
     * @param nums1
     * @return
     */
    public boolean uniformArray(int[] nums1) {
        return true;
    }

    /**
     * 3876. 构造奇偶一致的数组 II
     * @param nums1
     * @return
     */
    public boolean uniformArrayII(int[] nums1) {
        int n = nums1.length, minV = nums1[0];
        int[] cnt = new int[2];
        for (int num : nums1) {
            minV = Math.min(minV, num);
            if ((num & 1) == 1) {
                cnt[0]++;
            } else {
                cnt[1]++;
            }
        }
        return (minV & 1) == 1 || cnt[1] == n;
    }

    /**
     * 3886. 可排序整数求和
     * 
     * 计算数组中所有"可排序"块大小的总和。
     *
     * 题目约定：给定长度为 n 的数组 nums，若能将数组划分为若干个长度为 d 的连续块，
     * 且每个块内部可以通过若干次"循环移动"操作变为有序，则称 d 是一个"可排序块大小"。
     * 目标：求出所有满足条件的 d 之和。
     *
     * 核心思路：
     *   1. 先求出 n 的所有因子（因为块大小 d 必须能整除 n，才能均匀分块）。
     *   2. 将原数组排序得到目标数组 target。
     *   3. 对每个因子 d，将数组按长度 d 分块，逐块检查：
     *      该块内的元素能否通过循环移位变成 target 中对应位置的块。
     *   4. 若某个 d 下所有块都满足条件，则将 d 累加到答案中。
     * 
     * @param nums
     * @return
     */
    public int sortableIntegers(int[] nums) {
        int n = nums.length;

        // 第一步：求出 n 的所有因子（块大小必须能整除 n）
        List<Integer> fac = new ArrayList<>();
        for (int x = 1; x * x <= n; x++) {
            if (n % x == 0) {
                // x 是一个因子
                fac.add(x);
                if (n / x != x) {
                    // n/x 也是因子，避免重复添加平方根
                    fac.add(n / x);
                }
            }
        }

        // 第二步：对原数组排序，得到每个位置"应该是什么值"的目标数组
        int[] target = nums.clone();
        Arrays.sort(target);

        // 记录所有合法块大小的累加和
        int ans = 0;

        // 第三步：逐个检查每个因子 d 是否可作为合法块大小
        for (int d : fac) {
            // 标记当前 d 是否对所有块都成立
            boolean allGood = true;

            // 将数组按长度 d 分成 n/d 个块，逐块检查
            for (int left = 0; left < n; left += d) {
                // 当前块的右边界（闭区间）
                int right = left + d - 1;
                // 取出原数组中当前块的元素
                int[] sub1 = Arrays.copyOfRange(nums, left, right + 1);
                // 取出目标数组中对应位置的元素
                int[] sub2 = Arrays.copyOfRange(target, left, right + 1);
                // 检查 sub1 能否通过循环移位变成 sub2
                if (!sortableIntegersCheck(sub1, sub2)) {
                    // 只要有一个块不满足，当前 d 就不合法
                    allGood = false;
                    break;
                }
            }

            // 如果所有块都满足条件，将 d 累加到答案中
            if (allGood) {
                ans += d;
            }
        }

        return ans;
    }

    /**
     * 检查 nums1 能否通过若干次"循环左移"操作变成 nums2。
     *
     * 循环左移：将数组的第一个元素移到末尾，其余元素整体左移一位。
     * 例如：[3, 1, 2] 循环左移一次 → [1, 2, 3]
     *
     * 判断方法：
     *   1. 先找到 nums1 中第一个"下降点"（nums1[i] > nums1[i+1]），
     *      该位置就是循环移位的"断点"。
     *   2. 从断点处将数组切开并重新拼接，得到循环移位后的结果。
     *   3. 与 nums2 比较是否相等。
     *   4. 如果没有下降点（数组本身非递减），直接比较两数组是否相等。
     */
    private boolean sortableIntegersCheck(int[] nums1, int[] nums2) {
        int L = nums1.length;

        // 寻找第一个下降点：nums1[i] > nums1[i+1]
        for (int i = 0; i < L - 1; i++) {
            if (nums1[i] > nums1[i + 1]) {
                // 找到断点，构造循环左移后的数组
                int[] rotated = new int[L];
                // 将断点右侧部分 [i+1, L-1] 复制到 rotated 的开头
                System.arraycopy(nums1, i + 1, rotated, 0, L - i - 1);
                // 将断点左侧部分 [0, i] 复制到 rotated 的末尾
                System.arraycopy(nums1, 0, rotated, L - i - 1, i + 1);
                // 比较旋转后的数组是否与目标一致
                return Arrays.equals(rotated, nums2);
            }
        }

        // 没有下降点，说明 nums1 本身已非递减，直接比较是否与 nums2 相等
        return Arrays.equals(nums1, nums2);
    }
}
