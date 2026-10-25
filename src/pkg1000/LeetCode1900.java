package pkg1000;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class LeetCode1900 {

    /**
     * 1912. 设计电影租借系统
     */
    class MovieRentingSystem {

        // 按电影维护未租店铺
        Map<Integer, TreeSet<int[]>> available;
        // 维护所有已租记录
        TreeSet<int[]> rented;
        // 快速定位 (shop, movie) 的价格
        Map<Long, int[]> info;

        public MovieRentingSystem(int n, int[][] entries) {
            available = new HashMap<>();
            rented = new TreeSet<>((a, b) -> {
                if (a[0] != b[0]) return a[0] - b[0];
                if (a[1] != b[1]) return a[1] - b[1];
                return a[2] - b[2];
            });
            info = new HashMap<>();

            for (int[] entry : entries) {
                int shop = entry[0], movie = entry[1], price = entry[2];
                available.computeIfAbsent(movie, k -> new TreeSet<>((a, b) -> {
                    if (a[0] != b[0]) return a[0] - b[0];
                    return a[1] - b[1];
                })).add(new int[]{price, shop});

                info.put(key(shop, movie), new int[]{price, shop, movie});
            }
        }

        private long key(int shop, int movie) {
            return 10000L * shop + movie;
        }
        
        public List<Integer> search(int movie) {
            List<Integer> ans = new ArrayList<>();
            TreeSet<int[]> set = available.get(movie);
            if (set == null) {
                return ans;
            }

            int count = 0;
            for (int[] item : set) {
                if (count++ >= 5) {
                    break;
                }
                ans.add(item[1]);
            }
            return ans;
        }
        
        public void rent(int shop, int movie) {
            int[] record = info.get(key(shop, movie));
            if (record == null) return;

            TreeSet<int[]> set = available.get(movie);
            if (set != null) {
                set.remove(new int[]{record[0], shop});
            }
            rented.add(record);
        }
        
        public void drop(int shop, int movie) {
            int[] record = info.get(key(shop, movie));
            if (record == null) {
                return;
            }

            rented.remove(record);
            available.computeIfAbsent(movie, k -> new TreeSet<>((a, b) -> {
                if (a[0] != b[0]) return a[0] - b[0];
                return a[1] - b[1];
            })).add(new int[]{record[0], shop});
        }
        
        public List<List<Integer>> report() {
            List<List<Integer>> ans = new ArrayList<>();
            int count = 0;
            for (int[] rent : rented) {
                if (count++ >= 5) break;
                List<Integer> item = new ArrayList<>();
                item.add(rent[1]);
                item.add(rent[2]);
                ans.add(item);
            }
            return ans;
        }
    }

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
