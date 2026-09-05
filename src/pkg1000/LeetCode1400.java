package pkg1000;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LeetCode1400 {
    /**
     * 1489. 找到最小生成树里的关键边和伪关键边
     * @param n
     * @param edges
     * @return
     */
    public List<List<Integer>> findCriticalAndPseudoCriticalEdges(int n, int[][] edges) {
        int m = edges.length;

        // 1. 预处理：将原始边信息复制到新数组，并在末尾追加原始索引
        // 因为后续需要对边进行排序，排序后原始索引会乱，必须提前保存以便最后返回结果
        int[][] newEdges = new int[m][4];
        for (int i = 0; i < m; i++) {
            newEdges[i][0] = edges[i][0];
            newEdges[i][1] = edges[i][1];
            newEdges[i][2] = edges[i][2];
            // 第4个位置存储原始索引
            newEdges[i][3] = i;
        }

        // 2. 按边的权重从小到大排序（Kruskal 算法的贪心基础），按权重(第3个元素)升序排列
        Arrays.sort(newEdges, (a, b) -> a[2] - b[2]);

        // 3. 计算标准最小生成树(MST)的总权重 value
        UnionFind uFind = new UnionFind(n);
        int value = 0;
        for (int i = 0; i < m; i++) {
            // 尝试合并当前边，如果合并成功（未形成环），则累加权重
            if (uFind.unite(newEdges[i][0], newEdges[i][1])) {
                value += newEdges[i][2];
            }
        }

        // 4. 初始化结果集：ans[0] 存关键边，ans[1] 存伪关键边
        List<List<Integer>> ans = new ArrayList<>();
        ans.add(new ArrayList<Integer>());
        ans.add(new ArrayList<Integer>());

        // 5. 遍历每一条边，逐一判断它的属性
        for (int i = 0; i < m; i++) {
            // ================= 判断是否为【关键边】 =================
            // 关键边定义：如果去掉这条边后，图的 MST 总权重变大，或者图不再连通，
            // 说明这条边是所有 MST 都必须包含的边。
            UnionFind uf = new UnionFind(n);
            int v = 0;
            for (int j = 0; j < m; j++) {
                // 跳过第 i 条边，尝试用剩下的边构建 MST
                if (i != j && uf.unite(newEdges[j][0], newEdges[j][1])) {
                    v += newEdges[j][2];
                }
            }
            // 如果去掉该边后，图不连通(setCount != 1)，或者 MST 总权重变大(v > value)
            if (uf.setCount != 1 || v > value) {
                // 记录原始索引
                ans.get(0).add(newEdges[i][3]);
                // 关键边一定不是伪关键边，直接跳过后续判断
                continue;
            }

            // ================= 判断是否为【伪关键边】 =================
            // 伪关键边定义：去掉它 MST 权重不变，但如果强制先选它，依然能构成权重为 value 的 MST。
            uf = new UnionFind(n);
            // 强制先选中第 i 条边（直接合并它的两个顶点）
            uf.unite(newEdges[i][0], newEdges[i][1]);
            // 初始权重即为该边的权重
            v = newEdges[i][2];

            // 继续用剩余的边尝试构建 MST
            for (int j = 0; j < m; j++) {
                if (i != j && uf.unite(newEdges[j][0], newEdges[j][1])) {
                    v += newEdges[j][2];
                }
            }
            // 如果强制选它之后，最终构建的 MST 总权重依然等于标准 value
            // 说明存在一种 MST 是包含这条边的，它就是伪关键边
            if (v == value) {
                ans.get(1).add(newEdges[i][3]);
            }
        }

        return ans;
    }

    // 并查集模板（带路径压缩和按秩合并优化）
    class UnionFind {
        // 记录每个节点的父节点
        int[] parent;
        // 记录每个集合的大小（用于按秩合并，保持树平衡）
        int[] size;
        // 节点总数
        int n;
        // 当前连通分量的数目
        int setCount;

        public UnionFind(int n) {
            this.n = n;
            // 初始化每个节点都是独立的集合
            this.setCount = n;
            this.parent = new int[n];
            this.size = new int[n];
            // 初始化每个集合大小为 1
            Arrays.fill(size, 1);
            for (int i = 0; i < n; i++) {
                // 初始化父节点为自己
                parent[i] = i;
            }
        }

        // 查找根节点，带路径压缩优化
        public int findSet(int x) {
            return parent[x] == x ? x : (parent[x] = findSet(parent[x]));
        }

        // 合并两个集合，如果成功合并返回 true，如果已在同一集合返回 false
        public boolean unite(int x, int y) {
            x = findSet(x);
            y = findSet(y);

            // 已经在同一个连通分量中，合并会产生环
            if (x == y) {
                return false;
            }

            // 按秩合并：将小树挂到大树下，降低树的高度
            if (size[x] < size[y]) {
                int t = x;
                x = y;
                y = t;
            }
            parent[y] = x;
            size[x] += size[y];
            // 成功合并，连通分量减 1
            setCount--;
            return true;
        }

        // 判断两个节点是否在同一个连通分量中
        public boolean connected(int x, int y) {
            x = findSet(x);
            y = findSet(y);
            return x == y;
        }
    }
    
}
