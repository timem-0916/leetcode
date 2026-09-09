package pkg3000;

public class LeetCode3800 {
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
