package pkg2000;

public class LeetCode2700 {
    /**
     * 2778. 特殊元素平方和
     * @param nums
     * @return
     */
    public int sumOfSquares(int[] nums) {
        int n = nums.length, ans = 0;
        for (int i = 0; i < n; i++) {
            if (n % (i + 1) != 0) {
                continue;
            }
            ans += nums[i] * nums[i];
        }
        return ans;
    }
}
