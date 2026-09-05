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

    /**
     * 3904. 最小稳定下标 II
     * @param nums
     * @param k
     * @return
     */
    public int firstStableIndexII(int[] nums, int k) {
        int n = nums.length;
        int[] min = new int[n];
        min[n - 1] = nums[n - 1];
        for (int i = n - 2; i >= 0; i--) {
            min[i] = Math.min(min[i + 1], nums[i]);
        }

        for (int i = 0, curMax = -1; i < n; i++) {
            curMax = Math.max(curMax, nums[i]);
            int curMin = min[i];
            if (curMax - curMin <= k) {
                return i;
            }
        }
        return -1;
    }
}
