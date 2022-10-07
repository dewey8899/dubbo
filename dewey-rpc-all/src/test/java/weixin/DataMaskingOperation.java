package weixin;

/**
 * @auther dewey
 * @date 2022/7/20 19:21
 */
public interface DataMaskingOperation {
    String MASK_CHAR = "*";

    String mask(String content, String maskChar);
}
