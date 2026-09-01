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
}
