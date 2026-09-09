package pkg2000;

import util.TreeNode;

public class LeetCode2100 {

    /**
     * 2196. 根据描述创建二叉树
     * @param descriptions
     * @return
     */
    public TreeNode createBinaryTree(int[][] descriptions) {
        // 数值与对应节点的哈希表
        TreeNode[] nodes = new TreeNode[100001];
        // 数值对应的节点是否存在父节点的哈希表
        boolean[] existParent = new boolean[100001];

        for (int[] desc : descriptions) {
            int p = desc[0], c = desc[1], l = desc[2];
            // 创建或更新节点
            if (nodes[p] == null) {
                nodes[p] = new TreeNode(p);
            }
            if (nodes[c] == null) {
                nodes[c] = new TreeNode(c);
            }

            if (l == 1) {
                nodes[p].left = nodes[c];
            } else {
                nodes[p].right = nodes[c];
            }

            existParent[c] = true;
        }

        // 寻找根节点
        for (int i = 1; i <= 100000; i++) {
            if (nodes[i] != null && !existParent[i]) {
                return nodes[i];
            }
        }

        return null;
    }

}
