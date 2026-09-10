package pkg1000;

public class LeetCode1900 {
    /**
     * 1979. 找出数组的最大公约数
     * @param nums
     * @return
     */
    public int findGCD(int[] nums) {
        int min = nums[0], max = nums[0];
        for (int i = 1; i < nums.length; i++) {
            min = Math.min(min, nums[i]);
            max = Math.max(max, nums[i]);
        }
        return gcd(max, min);
    }

    private int gcd(int a, int b) {
        if (b == 0) {
            return a;
        }
        return gcd(b, a % b);
    }
}
