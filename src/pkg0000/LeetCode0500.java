package pkg0000;

import util.TreeNode;

public class LeetCode0500 {
    /**
     * 543. 二叉树的直径
     * @param root
     * @return
     */
    public int diameterOfBinaryTree(TreeNode root) {
        int[] max = {0};
        depth(root, max);
        return max[0];
    }

    /**
     * 递归求二叉树的深度
     * @param root
     * @return
     */
    private int depth(TreeNode root, int[] max) {
        if (root == null) {
            return 0;
        }
        int dl = depth(root.left, max), dr = depth(root.right, max);
        int sum = dl + dr;
        max[0] = Math.max(max[0], sum);
        return Math.max(dl, dr) + 1;
    }
}
