package pkg0000;

import util.TreeNode;

public class LeetCode0600 {
    /**
     * 687. 最长同值路径
     * @param root
     * @return
     */
    public int longestUnivaluePath(TreeNode root) {
        // 使用数组来模拟“引用传递”，用于在递归过程中记录全局最大值
        // 相比于类成员变量，这种写法没有副作用，更加安全
        int[] ans = {0};
        longestUnivaluePathDfs(root, ans);
        return ans[0];
    }

    /**
     * 辅助 DFS 函数：自底向上计算同值路径
     * @param root 当前遍历到的节点
     * @param ans 用于记录全局最长同值路径长度的数组
     * @return 返回从当前节点 root 向下延伸，所能形成的最长同值路径的边数（注意：只能选左或右的一条单线）
     */
    private int longestUnivaluePathDfs(TreeNode root, int[] ans) {
        // 1. 递归终止条件：如果当前节点为空，返回 0
        if (root == null) {
            return 0;
        }

        // 2. 递归处理左右子树，获取它们能向上提供的最长同值边数
        int left = longestUnivaluePathDfs(root.left, ans), right = longestUnivaluePathDfs(root.right, ans);
        
        // 3. 计算当前节点向左、向右的“有效延伸”长度
        // 只有当子节点存在，且子节点的值与当前节点相同时，才能将路径延长
        int left1 = 0, right1 = 0;
        if (root.left != null && root.val == root.left.val) {
            left1 = left + 1;
        }
        if (root.right != null && root.val == root.right.val) {
            right1 = right + 1;
        }

        // 4. 【更新全局答案】
        // 以当前节点 root 为最高点（转折点），向左右两边延伸形成的完整路径长度为 left1 + right1
        // 这种“V”型路径在当前节点内部是合法的，所以用它来更新全局最大值
        ans[0] = Math.max(ans[0], left1 + right1);

        // 5. 【向上层父节点汇报】
        // 因为合法的路径不能分叉，所以只能挑选左边和右边中较长的那条，返回给父节点
        // 父节点拿到这个值后，如果值相同，就可以在此基础上继续 +1
        return Math.max(left1, right1);
    }
}
