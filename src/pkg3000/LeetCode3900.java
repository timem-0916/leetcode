package pkg3000;

public class LeetCode3900 {
    /**
     * 3903. 最小稳定下标 I
     * @param nums
     * @param k
     * @return
     */
    public int firstStableIndex(int[] nums, int k) {
        int n = nums.length;
        int[] max = new int[n], min = new int[n];
        max[0] = nums[0];
        min[n - 1] = nums[n - 1];
        for (int i = 1; i < n; i++) {
            max[i] = Math.max(max[i - 1], nums[i]);
            min[n - i - 1] = Math.min(min[n - i], nums[n - i - 1]);
        }

        for (int i = 0; i < n; i++) {
            int v = max[i] - min[i];
            if (v <= k) {
                return i;
            }
        }
        return -1;
    }
}
