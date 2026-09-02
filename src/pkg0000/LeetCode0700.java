package pkg0000;

import java.util.HashMap;
import java.util.Map;

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
