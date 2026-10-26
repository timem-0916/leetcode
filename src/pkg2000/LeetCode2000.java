package pkg2000;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeetCode2000 {

    /**
     * 2043. 简易银行系统
     */
    class Bank {

        long[] balance;

        public Bank(long[] balance) {
            this.balance = balance;
        }
        
        public boolean transfer(int account1, int account2, long money) {
            if (account1 > balance.length || account2 > balance.length || balance[account1 - 1] < money) {
                return false;
            }
            balance[account1 - 1] -= money;
            balance[account2 - 1] += money;
            return true;
        }
        
        public boolean deposit(int account, long money) {
            if (account > balance.length) {
                return false;
            }
            balance[account - 1] += money;
            return true;
        }
        
        public boolean withdraw(int account, long money) {
            if (account > balance.length || balance[account - 1] < money) {
                return false;
            }
            balance[account - 1] -= money;
            return true;
        }
    }

    /**
     * 2080. 区间内查询数字的频率
     * 
     * 区间频率查询器（优化版）：使用「值 → 位置列表」映射 + 二分查找。
     *
     * 核心思路：
     *   1. 预处理阶段：遍历数组，将每个值出现的所有下标存入一个有序列表（ArrayList）。
     *      由于按下标顺序遍历，列表天然有序，无需额外排序。
     *   2. 查询阶段：对于 query(left, right, value)，在 value 对应的位置列表中，
     *      用二分查找找到第一个 ≥ left 的位置和第一个 > right 的位置，
     *      两者之差即为 value 在 [left, right] 中的出现次数。
     *
     * 时间复杂度：
     *   - 构造：O(n)，遍历一次数组
     *   - 查询：O(log n)，两次二分查找
     * 空间复杂度：O(n)，存储所有下标
     */
    class RangeFreqQuery {

        // 值 → 该值在数组中出现的所有下标列表（有序）
        Map<Integer, List<Integer>> positions;

        /**
         * 构造函数：构建值到下标列表的映射。
         *
         * @param arr 输入数组
         */
        public RangeFreqQuery(int[] arr) {
            int n = arr.length;
            positions = new HashMap<>();
            for (int i = 0; i < n; i++) {
                // 如果该值首次出现，创建新的列表；否则追加下标
                positions.computeIfAbsent(arr[i], k -> new ArrayList<>()).add(i);
            }
        }
        
        /**
         * 查询子数组 arr[left..right] 中 value 的出现次数。
         *
         * 二分查找思路：
         *   - lowerBound(list, left)：找到第一个 ≥ left 的下标位置。
         *   - upperBound(list, right)：找到第一个 > right 的下标位置。
         *   - 两者之差即为 [left, right] 范围内 value 的出现次数。
         *
         * 示例：
         *   arr = [12, 33, 4, 56, 22, 2, 34, 33, 22, 12, 34, 56]
         *   positions[33] = [1, 7]
         *   query(1, 5, 33)：
         *     lowerBound([1,7], 1) = 0（第一个 ≥ 1 的位置是索引 0，值为 1）
         *     upperBound([1,7], 5) = 1（第一个 > 5 的位置是索引 1，值为 7）
         *     结果 = 1 - 0 = 1（arr[1..5] 中 33 出现 1 次）
         *
         * @param left  子区间左端点（包含）
         * @param right 子区间右端点（包含）
         * @param value 要查询频率的值
         * @return value 在 arr[left..right] 中的出现次数
         */
        public int query(int left, int right, int value) {
            // 如果该值从未出现过，直接返回 0
            if (!positions.containsKey(value)) {
                return 0;
            }

            List<Integer> pos = positions.get(value);

            // 找到第一个 ≥ left 的位置（lower bound）
            int lo = lowerBound(pos, left);
            // 找到第一个 > right 的位置（upper bound）
            int hi = upperBound(pos, right);

            // 两者之差即为 [left, right] 范围内的出现次数
            return hi - lo;
        }

        /**
         * 二分查找：在有序列表 list 中找到第一个 ≥ target 的元素下标。
         * 即 C++ 中的 std::lower_bound。
         *
         * @param list   有序下标列表
         * @param target 目标值
         * @return 第一个 ≥ target 的元素在 list 中的索引；若所有元素都 < target，返回 list.size()
         */
        private int lowerBound(List<Integer> list, int target) {
            int lo = 0, hi = list.size();
            while (lo < hi) {
                // 防止溢出
                int mid = lo + ((hi - lo) >>> 1);
                if (list.get(mid) < target) {
                    lo = mid + 1;
                } else {
                    hi = mid;
                }
            }
            return lo;
        }

        /**
         * 二分查找：在有序列表 list 中找到第一个 > target 的元素下标。
         * 即 C++ 中的 std::upper_bound。
         *
         * @param list   有序下标列表
         * @param target 目标值
         * @return 第一个 > target 的元素在 list 中的索引；若所有元素都 ≤ target，返回 list.size()
         */
        private int upperBound(List<Integer> list, int target) {
            int lo = 0, hi = list.size();
            while (lo < hi) {
                // 防止溢出
                int mid = lo + ((hi - lo) >>> 1);
                if (list.get(mid) <= target) {
                    lo = mid + 1;
                } else {
                    hi = mid;
                }
            }
            return lo;
        }
    }

    /**
     * 2089. 找出数组排序后的目标下标
     * @param nums
     * @param target
     * @return
     */
    public List<Integer> targetIndices(int[] nums, int target) {
        // n -> 数组大小，preCnt -> 小于 target 的数目，cnt -> 等于 target 的数目
        int n = nums.length, preCnt = 0, cnt = 0;

        // 统计数目
        for (int i = 0; i < n; i++) {
            if (nums[i] < target) {
                preCnt++;
            } else if (nums[i] == target) {
                cnt++;
            }
        }

        List<Integer> ans = new ArrayList<>();

        for (int i = preCnt; i < preCnt + cnt; i++) {
            ans.add(i);
        }

        return ans;
    }
}
