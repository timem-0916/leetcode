package pkg0000;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class LeetCode0800 {
    /**
     * 841. 钥匙和房间
     * @param rooms
     * @return
     */
    public boolean canVisitAllRooms(List<List<Integer>> rooms) {
        int n = rooms.size();
        boolean[] open = new boolean[n];
        Queue<Integer> q = new ArrayDeque<>();
        q.offer(0);
        while (!q.isEmpty()) {
            int curr = q.poll();
            open[curr] = true;
            for (int next : rooms.get(curr)) {
                if (!open[next]) {
                    q.offer(next);
                }
            }
        }

        for (int i = 0; i < n; i++) {
            if (!open[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * 860. 柠檬水找零
     * @param bills
     * @return
     */
    public boolean lemonadeChange(int[] bills) {
        // cnt[0] -> 5, cnt[1] -> 10, cnt[2] -> 20
        int[] cnt = {0, 0, 0};
        int n = bills.length;
        for (int i = 0; i < n; i++) {
            int income = bills[i];
            if (income == 20) {
                cnt[2]++;
                if (cnt[1] > 0 && cnt[0] > 0) {
                    cnt[1]--;
                    cnt[0]--;
                } else if (cnt[0] > 2) {
                    cnt[0] -= 3;
                } else {
                    return false;
                }
            } else if (income == 10) {
                cnt[1]++;
                if (cnt[0] > 0) {
                    cnt[0]--;
                } else {
                    return false;
                }
            } else {
                cnt[0]++;
            }
        }
        return true;
    }

    /**
     * 877. 石子游戏
     * @param piles
     * @return
     */
    public boolean stoneGame(int[] piles) {
        int n = piles.length;
        // dp[i][j] 表示当剩下的石子堆为下标 i 到下标 j 时，即在下标范围 [i,j] 中，当前玩家与另一个玩家的石子数量之差的最大值，注意当前玩家不一定是先手 Alice
        int[][] dp = new int[n][n];
        for (int i = 0; i < n; i++) {
            dp[i][i] = piles[i];
        }

        for (int l = 2; l <= n; l++) {
            for (int i = 0; i <= n - l; i++) {
                int j = i + l - 1;
                dp[i][j] = Math.max(piles[i] - dp[i + 1][j], piles[j] - dp[i][j - 1]);
            }
        }
        return dp[0][n - 1] > 0;
    }
}
