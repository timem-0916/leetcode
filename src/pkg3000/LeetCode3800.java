package pkg3000;

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
}
