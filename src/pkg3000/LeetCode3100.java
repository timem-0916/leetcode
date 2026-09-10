package pkg3000;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class LeetCode3100 {
    /**
     * 3115. 质数的最大距离
     * @param nums
     * @return
     */
    public int maximumPrimeDifference(int[] nums) {
        Set<Integer> primes = new HashSet<>(Arrays.asList(
            2, 3, 5, 7, 11,
            13, 17, 19, 23, 29,
            31, 37, 41, 43, 47,
            53, 59, 61, 67, 71,
            73, 79, 83, 89, 97
        ));

        int n = nums.length, i = -1, j = n;
        while (++i < n) {
            if (primes.contains(nums[i])) {
                break;
            }
        }
        while (--j >= 0) {
            if (primes.contains(nums[j])) {
                break;
            }
        }

        return j - i;
    }
}
