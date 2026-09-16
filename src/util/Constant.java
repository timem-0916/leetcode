package util;

/**
 * 项目常量定义类
 * 所有常量均为不可变的全局常量
 * Constant
 */
public final class Constant {
    // 私有构造函数，防止实例化
    private Constant() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // ==================== 数学常量 ====================
    public static final int MOD = 1_000_000_007;
}
