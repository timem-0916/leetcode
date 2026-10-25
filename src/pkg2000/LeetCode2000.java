package pkg2000;

import java.util.ArrayList;
import java.util.List;

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
