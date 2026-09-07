package pkg0000;

import util.TreeNode;

public class LeetCode0400 {
    // 将最高位的二进制位编号作为常量，避免魔法数字
    static final int HIGH_BIT = 30;

    /**
     * 421. 数组中两个数的最大异或值
     * @param nums
     * @return
     */
    public int findMaximumXOR(int[] nums) {
        int n = nums.length;
        // 将字典树的根节点作为局部变量，不占用类的成员空间
        Trie root = new Trie();
        int x = 0;

        for (int i = 1; i < n; i++) {
            // 将 nums[i-1] 放入字典树，此时 nums[0 .. i-1] 都在字典树中
            add(root, nums[i - 1]);
            // 将 nums[i] 看作 ai，找出最大的 x 更新答案
            x = Math.max(x, check(root, nums[i]));
        }

        return x;
    }

    /**
     * 将数字 num 添加到字典树中
     * @param root 字典树的根节点
     * @param num 要添加的数字
     */
    private void add(Trie root, int num) {
        Trie cur = root;
        // 从最高位向最低位遍历
        for (int k = HIGH_BIT; k >= 0; k--) {
            int bit = (num >> k) & 1;
            if (bit == 0) {
                if (cur.left == null) {
                    cur.left = new Trie();
                }
                cur = cur.left;
            } else {
                if (cur.right == null) {
                    cur.right = new Trie();
                }
                cur = cur.right;
            }
        }
    }

    /**
     * 在字典树中查找与 num 异或能得到的最大值
     * @param root 字典树的根节点
     * @param num 当前要参与异或的数字
     * @return 异或得到的最大值
     */
    private int check(Trie root, int num) {
        Trie cur = root;
        int x = 0;
        // 从最高位向最低位遍历，贪心地寻找与当前位相反的节点
        for (int k = HIGH_BIT; k >= 0; k--) {
            int bit = (num >> k) & 1;
            if (bit == 0) {
                // a_i 的第 k 个二进制位为 0，应当往表示 1 的子节点 right 走
                if (cur.right != null) {
                    cur = cur.right;
                    // 该位异或结果为 1
                    x = x << 1 | 1;
                } else {
                    cur = cur.left;
                    // 该位异或结果为 0
                    x = x << 1;
                }
            } else {
                // a_i 的第 k 个二进制位为 1，应当往表示 0 的子节点 left 走
                if (cur.left != null) {
                    cur = cur.left;
                    // 该位异或结果为 1
                    x = x << 1 | 1;
                } else {
                    cur = cur.right;
                    // 该位异或结果为 0
                    x = x << 1;
                }
            }
        }
        return x;
    }

    /**
     * 字典树节点类
     * Trie
     */
    class Trie {
        // 左子树指向表示 0 的子节点
        Trie left = null;
        // 右子树指向表示 1 的子节点
        Trie right = null;
    }

    /**
     * 440. 字典序的第K小数字
     * @param n
     * @param k
     * @return
     */
    public int findKthNumber(int n, int k) {
        // 1. 初始化当前数字为 1（字典序的起点）
        int curr = 1;

        // 2. 将 k 减 1，因为 curr = 1 已经占据了第 1 个位置
        // 此时 k 表示我们还需要在字典树中“往下走”多少步
        k--;

        // 3. 循环寻找，直到 k 减为 0
        while (k > 0) {
            // 核心步骤：计算以 curr 为根的子树中，有多少个节点（包含 curr 本身）
            int steps = getSteps(curr, n);

            // 情况 A：以 curr 为根的子树节点总数 <= 剩余的步数 k
            // 说明第 k 个数不在 curr 的子树里，我们需要跳过整个 curr 子树，去兄弟节点 curr + 1 找
            if (steps <= k) {
                // 减去跳过的节点数
                k -= steps;
                // 指针右移，指向下一个兄弟节点（例如从 1 跳到 2）
                curr++;
            } else {
                // 情况 B：以 curr 为根的子树节点总数 > 剩余的步数 k
                // 说明第 k 个数就在 curr 的子树里！我们需要向下深入一层

                // 指针下移，指向 curr 的第一个子节点（例如从 1 变成 10）
                curr *= 10;
                // 向下走了一步，消耗掉 1 个步数
                k--;
            }
        }

        return curr;
    }

    /**
     * 计算以 curr 为前缀的数字，在 [1, n] 范围内一共有多少个
     * 本质上是在计算字典树中，curr 这棵子树的节点总数
     * @param curr 当前前缀（例如 1）
     * @param n 数字的上限
     * @return
     */
    private int getSteps(int curr, long n) {
        int steps = 0;
        // first 表示当前层的最左节点，last 表示当前层的最右节点
        long first = curr, last = curr;

        // 逐层向下遍历字典树，直到最左节点 first 超过了上限 n
        while (first <= n) {
            // 核心计算：当前层的节点数 = 最右节点 - 最左节点 + 1
            // 注意：最右节点不能超过上限 n，所以取 Math.min(last, n)
            steps += Math.min(last, n) - first + 1;

            // 移动到下一层
            first *= 10;
            last *= 10;
            last += 9;
        }
        
        return steps;
    }

    /**
     * 450. 删除二叉搜索树中的节点
     * @param root
     * @param key
     * @return
     */
    public TreeNode deleteNode(TreeNode root, int key) {
        if (root == null) {
            return null;
        }

        if (key < root.val) {
            root.left = deleteNode(root.left, key);
        } else if (key > root.val) {
            root.right = deleteNode(root.right, key);
        } else {
            if (root.left == null) {
                return root.right;
            }
            if (root.right == null) {
                return root.left;
            }
            TreeNode successor = root.right;
            while (successor.left != null) {
                successor = successor.left;
            }
            root.right = deleteNode(root.right, successor.val);
            successor.right = root.right;
            successor.left = root.left;
            return successor;
        }

        return root;
    }
}
