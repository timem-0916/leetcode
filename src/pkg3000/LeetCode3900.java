package pkg3000;

import java.util.Arrays;

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

    /**
     * 3942. 排序排列的最少操作数
     * 
     * 计算将环形数组变为有序所需的最小操作数。
     *
     * 题目约定数组是 [0, 1, 2, ..., n-1] 的一个环形排列，
     * 操作定义为：每次可以把数组整体循环左移或右移一位。
     * 目标状态有两种：
     *   1. 升序：[0, 1, 2, ..., n-1]
     *   2. 降序：[n-1, n-2, ..., 1, 0]
     * 返回达成任意一种目标所需的最小移动次数；若两种都不可达，返回 -1。
     * 
     * @param nums
     * @return
     */
    public int minOperations(int[] nums) {
        final int n = nums.length;

        // 只有一个元素，天然有序，无需操作
        if (n == 1) {
            return 0;
        }

        // 找到元素 0 的下标，作为环形遍历的“起点”
        int start = 0;
        while (nums[start] != 0) {
            start++;
        }

        // asc：从 0 开始顺时针是否为严格升序 0,1,2,...,n-1
        boolean asc = true;
        // desc：从 0 开始顺时针是否为严格降序 0,n-1,n-2,...,1
        boolean desc = true;

        // 从 0 的下一个位置开始，沿环形依次检查每个位置的值
        for (int i = 1; i < n; i++) {
            // 环形取模：从 start 出发，第 i 个位置的实际下标
            final int value = nums[(start + i) % n];

            // 升序期望：第 i 个位置的值应为 i
            if (value != i) {
                asc = false;
            }

            // 降序期望：第 i 个位置的值应为 n - i
            // 即序列为 0, n-1, n-2, ..., 1
            if (value != n - i) {
                desc = false;
            }

            // 既不是升序也不是降序，无法通过循环移位变成有序
            if (!asc && !desc) {
                return -1;
            }
        }

        if (asc) {
            // 升序可达：
            //   方案1：循环左移 start 次，把 0 移到下标 0
            //   方案2：循环右移 n - start 次；
            //          代码中写作 n - start + 2，可能是题目对“右移”代价的特殊定义
            //          （例如右移一次计为 2 步，或包含额外两次翻转操作）
            return Math.min(start, n - start + 2);
        }

        // 降序可达：
        //   方案1：循环右移 n - start 次，把 0 移到下标 0 后整体反转
        //   方案2：循环左移 start 次后再做两次额外操作
        return Math.min(n - start, start + 2);
    }

    /**
     * 3963. 构造恰好一条路径的网格
     * @param m
     * @param n
     * @return
     */
    public String[] createGrid(int m, int n) {
        String[] ans = new String[m];
        char[] buff = new char[n];
        Arrays.fill(buff, '.');
        ans[0] = String.valueOf(buff);
        Arrays.fill(buff, '#');
        buff[n - 1] = '.';
        String next = String.valueOf(buff);
        for (int i = 1; i < m; i++) {
            ans[i] = next;
        }
        return ans;
    }
}
