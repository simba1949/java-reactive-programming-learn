package vip.openpark.stream;

/**
 * Stream Pipeline 流管道、流水线
 * Intermediate operations 中定义的操作，不会修改流中的元素，而是生成一个新的流。
 * Terminate operations 终止操作，会修改流中的元素，或者生成一个结果。
 * <p>
 * Stream 中所有数据和操作都可以被组合成一个流管道。
 * <p>
 * 流管道组成：
 * 1. 数据源（可以是集合、数组、IO流、函数、生成器等）
 * 2. 零个或者多个中间操作（过滤、排序、映射、聚合、合并等）
 * 3. 一个终止操作（计算、输出、转换为数组、转换为字符串等）
 * <p>
 * 流是惰性的的，只有在启动终止操作才会执行。
 *
 * @author anthony
 * @since 2024/2/16 21:51
 */
public class StreamApplication {
	public static void main(String[] args) {
	
	}
}