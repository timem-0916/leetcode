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
}
