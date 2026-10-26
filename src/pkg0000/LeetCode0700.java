package pkg0000;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import util.TreeNode;

public class LeetCode0700 {
    /**
     * 700. 二叉搜索树中的搜索
     * @param root
     * @param val
     * @return
     */
    public TreeNode searchBST(TreeNode root, int val) {
        if (root == null) {
            return null;
        }

        if (val == root.val) {
            return root;
        } else if (val < root.val) {
            return searchBST(root.left, val);
        } else {
            return searchBST(root.right, val);
        }
    }

    /**
     * 701. 二叉搜索树中的插入操作
     * @param root
     * @param val
     * @return
     */
    public TreeNode insertIntoBST(TreeNode root, int val) {
        if (root == null) {
            return new TreeNode(val);
        }

        if (val < root.val) {
            root.left = insertIntoBST(root.left, val);
        } else {
            root.right = insertIntoBST(root.right, val);
        }
        return root;
    }

    /**
     * 715. Range 模块
     * 
     * 区间模块：维护一组不相交的半开区间 [left, right)，支持添加、查询和删除操作。
     *
     * 数据结构：
     *   使用 TreeMap<Integer, Integer> 存储区间，key 为区间左端点，value 为区间右端点。
     *   TreeMap 按 key（左端点）升序排列，便于快速查找与给定位置相交的区间。
     *
     * 区间表示：
     *   所有区间均为半开区间 [left, right)，即包含 left，不包含 right。
     *   例如 [10, 20) 表示覆盖 10 到 19 的所有整数（或实数范围内 10 ≤ x < 20）。
     */
    class RangeModule {

        // 存储所有不相交的区间，key=左端点，value=右端点
        TreeMap<Integer, Integer> intervals;

        /**
         * 初始化区间模块，创建一个空的 TreeMap。
         */
        public RangeModule() {
            intervals = new TreeMap<>();
        }

        /**
         * 添加区间 [left, right)，即将该范围内的所有点标记为"已覆盖"。
         *
         * 核心思路：
         *   1. 找到可能与 [left, right) 相交的已有区间。
         *   2. 如果左侧有重叠区间，向左扩展 left 并删除该区间。
         *   3. 向右遍历所有与 [left, right) 相交的区间，扩展 right 并删除它们。
         *   4. 最后插入合并后的新区间 [left, right)。
         *
         * @param left  区间左端点（包含）
         * @param right 区间右端点（不包含）
         */
        public void addRange(int left, int right) {
            // higherEntry(left)：找到左端点严格大于 left 的第一个区间
            // 这个区间可能与 [left, right) 相交（如果它的左端点 < right）
            Map.Entry<Integer, Integer> entry = intervals.higherEntry(left);

            // 如果 TreeMap 不为空，且找到的 entry 不是第一个区间，
            // 说明可能存在一个左端点 ≤ left 的区间与 [left, right) 相交
            if (entry != intervals.firstEntry()) {
                // start：左端点 ≤ left 的最大区间（即可能与 [left, right) 左侧相交的区间）
                // 如果 entry 为 null（所有区间的左端点都 ≤ left），则取最后一个区间
                Map.Entry<Integer, Integer> start = entry != null ? intervals.lowerEntry(entry.getKey()) : intervals.lastEntry();

                // 情况1：start 区间完全覆盖 [left, right)，无需任何操作
                // 即 start 的右端点 ≥ right，说明 [left, right) 已被完全包含
                if (start != null && start.getValue() >= right) {
                    return;
                }

                // 情况2：start 区间与 [left, right) 部分重叠（start 的右端点 ≥ left）
                // 需要将 left 扩展到 start 的左端点，并删除 start 区间
                if (start != null && start.getValue() >= left) {
                    // 向左扩展左边界
                    left = start.getKey();
                    // 删除原区间
                    intervals.remove(start.getKey());
                }
            }

            // 向右遍历，合并所有与 [left, right) 相交的区间
            // 相交条件：区间的左端点 ≤ right（因为区间是半开的，等于 right 时不相交）
            while (entry != null && entry.getKey() <= right) {
                // 扩展右边界：取当前区间右端点与 right 的较大值
                right = Math.max(right, entry.getValue());
                // 删除已合并的区间
                intervals.remove(entry.getKey());
                // 继续查找下一个左端点更大的区间
                entry = intervals.higherEntry(entry.getKey());
            }

            // 插入合并后的新区间 [left, right)
            intervals.put(left, right);
        }

        /**
         * 查询区间 [left, right) 是否被完全覆盖。
         *
         * 核心思路：
         *   1. 找到可能与 [left, right) 相交的区间。
         *   2. 检查该区间是否完全覆盖 [left, right)。
         *
         * @param left  区间左端点（包含）
         * @param right 区间右端点（不包含）
         * @return 如果 [left, right) 被完全覆盖则返回 true，否则返回 false
         */
        public boolean queryRange(int left, int right) {
            // higherEntry(left)：找到左端点严格大于 left 的第一个区间
            Map.Entry<Integer, Integer> entry = intervals.higherEntry(left);

            // 如果 entry 是第一个区间，说明没有任何区间的左端点 ≤ left，
            // 即不存在能覆盖 left 的区间，直接返回 false
            if (entry == intervals.firstEntry()) {
                return false;
            }

            // 找到左端点 ≤ left 的最大区间（即可能覆盖 left 的区间）
            // 如果 entry 为 null，说明所有区间的左端点都 ≤ left，取最后一个区间
            entry = entry != null ? intervals.lowerEntry(entry.getKey()) : intervals.lastEntry();

            // 判断该区间是否完全覆盖 [left, right)：
            // 1. entry 不为 null（存在这样的区间）
            // 2. 该区间的右端点 ≥ right（覆盖到 right）
            // 由于该区间的左端点 ≤ left（由 lowerEntry 保证），所以只需检查右端点
            return entry != null && right <= entry.getValue();
        }

        /**
         * 删除区间 [left, right)，即将该范围内的所有点标记为"未覆盖"。
         *
         * 核心思路：
         *   1. 找到可能与 [left, right) 相交的已有区间。
         *   2. 处理左侧可能相交的区间：可能需要截断或拆分。
         *   3. 向右遍历所有与 [left, right) 相交的区间：完全覆盖的删除，部分覆盖的截断。
         *
         * @param left  区间左端点（包含）
         * @param right 区间右端点（不包含）
         */
        public void removeRange(int left, int right) {
            // higherEntry(left)：找到左端点严格大于 left 的第一个区间
            Map.Entry<Integer, Integer> entry = intervals.higherEntry(left);

            // 如果 TreeMap 不为空，且找到的 entry 不是第一个区间，
            // 说明可能存在一个左端点 ≤ left 的区间与 [left, right) 相交
            if (entry != intervals.firstEntry()) {
                // start：左端点 ≤ left 的最大区间（即可能与 [left, right) 左侧相交的区间）
                Map.Entry<Integer, Integer> start = entry != null ? intervals.lowerEntry(entry.getKey()) : intervals.lastEntry();

                // 情况1：start 区间完全包含 [left, right)
                // 即 start 的右端点 ≥ right，需要将 start 区间拆分为两部分（或保留一部分）
                if (start != null && start.getValue() >= right) {
                    // 保存原区间的右端点
                    int ri = start.getValue();

                    // 处理左半部分：[start.getKey(), left)
                    if (start.getKey() == left) {
                        // 如果 start 的左端点恰好等于 left，左半部分为空，直接删除
                        intervals.remove(start.getKey());
                    } else {
                        // 否则保留左半部分 [start.getKey(), left)
                        intervals.put(start.getKey(), left);
                    }

                    // 处理右半部分：[right, ri)
                    if (right != ri) {
                        // 如果 right < ri，右半部分非空，插入 [right, ri)
                        intervals.put(right, ri);
                    }

                    // 删除操作完成，直接返回
                    return;
                }

                // 情况2：start 区间与 [left, right) 部分重叠（start 的右端点 > left 但 < right）
                // 只需保留左半部分 [start.getKey(), left)
                else if (start != null && start.getValue() > left) {
                    if (start.getKey() == left) {
                        // 如果 start 的左端点恰好等于 left，整个区间被删除
                        intervals.remove(start.getKey());
                    } else {
                        // 否则保留左半部分 [start.getKey(), left)
                        intervals.put(start.getKey(), left);
                    }
                }
            }

            // 向右遍历，处理所有左端点 < right 的区间
            // 这些区间的左端点 > left（由 higherEntry 保证），且可能与 [left, right) 相交
            while (entry != null && entry.getKey() < right) {
                if (entry.getValue() <= right) {
                    // 情况A：当前区间完全在 [left, right) 内，直接删除
                    intervals.remove(entry.getKey());
                    // 继续查找下一个区间
                    entry = intervals.higherEntry(entry.getKey());
                } else {
                    // 情况B：当前区间部分在 [left, right) 内，部分在右侧
                    // 保留右半部分 [right, entry.getValue())
                    intervals.put(right, entry.getValue());
                    // 删除原区间
                    intervals.remove(entry.getKey());
                    // 后续区间的左端点都 ≥ 当前区间的左端点 ≥ right，不可能再相交，跳出循环
                    break;
                }
            }
        }
    }

    /**
     * 720. 词典中最长的单词
     * @param words
     * @return
     */
    public String longestWord(String[] words) {
        // 1. 初始化字典树，并将所有单词插入到树中
        Trie trie = new Trie();
        for (String word : words) {
            trie.insert(word);
        }

        // 2. 遍历所有单词，寻找满足条件的最长单词
        String longest = "";
        for (String word : words) {
            // 核心判断：检查当前单词的每一个前缀是否都在字典树中
            if (trie.search(word)) {
                // 如果满足条件，再比较长度和字典序：
                // 1. 长度更长，则更新答案
                // 2. 长度相等，但字典序更小（compareTo < 0），也更新答案
                if (word.length() > longest.length() || (word.length() == longest.length() && word.compareTo(longest) < 0)) {
                    longest = word;
                }
            }
        }
        return longest;
    }

    /**
     * 字典树（Trie）实现
     */
    class Trie {
        // 存储子节点的数组，长度为26，对应26个小写英文字母
        Trie[] children;
        // 标记当前节点是否是一个单词的结尾
        boolean isEnd;

        public Trie() {
            children = new Trie[26];
            isEnd = false;
        }

        /**
         * 将单词插入到字典树中
         * @param word
         */
        public void insert(String word) {
            // 从根节点开始
            Trie node = this;
            for (int i = 0; i < word.length(); i++) {
                char c = word.charAt(i);
                // 计算字符对应的数组下标 (0~25)
                int idx = c - 'a';

                // 如果当前路径上的节点不存在，则创建新节点
                if (node.children[idx] == null) {
                    node.children[idx] = new Trie();
                }
                // 移动到下一个节点
                node = node.children[idx];
            }
            // 单词遍历完毕，在最后一个节点打上结束标记
            node.isEnd = true;
        }

        /**
         * 检查一个单词是否由字典中的单词逐步添加一个字母组成
         * 【核心逻辑】：不仅要求单词本身存在，还要求路径上的【每一个前缀节点】都是单词的结尾
         * @param word
         * @return
         */
        public boolean search(String word) {
            Trie node = this;
            for (int i = 0; i < word.length(); i++) {
                char c = word.charAt(i);
                int idx = c - 'a';
                // 如果子节点不存在，或者子节点不是一个单词的结尾，直接返回 false
                // 例如查找 "world"，如果 "worl" 不在词典中（即 !isEnd），则 "world" 不合法
                if (node.children[idx] == null || !node.children[idx].isEnd) {
                    return false;
                }
                // 继续向下匹配
                node = node.children[idx];
            }
            // 循环结束说明所有前缀都存在，最后确认当前节点确实是单词结尾
            return node != null && node.isEnd;
        }
    }

    /**
     * 728. 自除数
     */
    public List<Integer> selfDividingNumbers(int left, int right) {
        List<Integer> ans = new ArrayList<>();
        for (int i = left; i <= right; i++) {
            if (isSelfDividingNumbers(i)) {
                ans.add(i);
            }
        }
        return ans;
    }

    private boolean isSelfDividingNumbers(int num) {
        int x = num;
        while (x > 0) {
            int m = x % 10;
            if (m == 0 || num % m != 0) {
                return false;
            }
            x /= 10;
        }
        return true;
    }

    /**
     * 731. 我的日程安排表 II
     * MyCalendarTwo
     */
    class MyCalendarTwo {
        // 使用 HashMap 实现动态开点线段树，key 是节点索引，value 是长度为 2 的数组
        // value[0] 存储当前区间的最大预订次数（区间最大值）
        // value[1] 存储懒标记（延迟下传的值，表示该区间被整体增加/减少了多少次）
        private Map<Integer, int[]> tree;

        public MyCalendarTwo() {
            tree = new HashMap<Integer, int[]>();
        }
        
        /**
         * 尝试预订一个日程
         * @param startTime
         * @param endTime
         * @return
         */
        public boolean book(int startTime, int endTime) {
            // 1. 尝试将 [startTime, endTime - 1] 区间的预订次数 +1
            // 注意：题目中的时间是半开区间 [start, end)，转化为线段树的闭区间就是 [start, end - 1]
            update(startTime, endTime - 1, 1, 0, 1000000000, 1);

            // 2. 检查根节点（索引为 1）的最大预订次数
            tree.putIfAbsent(1, new int[2]);
            if (tree.get(1)[0] > 2) {
                // 3. 如果最大预订次数超过 2（即发生三重预订），说明本次预订失败
                // 必须进行回滚操作：将刚才 +1 的操作撤销（即 -1）
                update(startTime, endTime - 1, -1, 0, 1000000000, 1);
                return false;
            }
            // 4. 没有发生三重预订，预订成功
            return true;
        }

        /**
         * 动态开点线段树的区间修改操作（带懒标记）
         * @param start 目标修改区间的左端点
         * @param end 目标修改区间的右端点
         * @param val 修改的值（+1 表示新增预订，-1 表示回滚预订）
         * @param l 当前节点管理的区间左端点
         * @param r 当前节点管理的区间右端点
         * @param idx 当前节点在 HashMap 中的索引
         */
        private void update(int start, int end, int val, int l, int r, int idx) {
            // 1. 越界检查：如果当前节点管理的区间 [l, r] 与目标区间 [start, end] 完全没有交集，直接返回
            if (r < start || end < l) {
                return;
            }

            // 2. 动态开点：如果当前节点在 HashMap 中还不存在，则初始化它
            tree.putIfAbsent(idx, new int[2]);

            if (start <= l && r <= end) {
                // 3. 完全包含：如果目标区间完全覆盖了当前节点管理的区间
                // 直接更新当前节点的最大值和懒标记，不再向下递归（懒标记的核心思想）

                // 最大值增加 val
                tree.get(idx)[0] += val;
                // 懒标记增加 val
                tree.get(idx)[1] += val;
            } else {
                // 4. 部分重叠：需要向下递归处理子区间
                
                // 计算中点
                int m = (l + r) >> 1;

                // 递归处理左子树和右子树
                // 左子节点索引为 idx << 1 (即 idx * 2)
                // 右子节点索引为 idx << 1 | 1 (即 idx * 2 + 1)
                update(start, end, val, l, m, idx << 1);
                update(start, end, val, m + 1, r, idx << 1 | 1);

                // 确保左右子节点在 HashMap 中存在，防止后续 get 时出现空指针
                tree.putIfAbsent(idx << 1, new int[2]);
                tree.putIfAbsent(idx << 1 | 1, new int[2]);

                // 5. 自底向上回溯更新：当前节点的最大值 = 懒标记值 + 左右子树最大值的较大者
                tree.get(idx)[0] = tree.get(idx)[1] + Math.max(tree.get(idx << 1)[0], tree.get(idx << 1 | 1)[0]);
            }
        }
    }
}
