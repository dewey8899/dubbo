package weixin;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @auther dewey
 * @date 2022/7/20 19:19
 */
@Data
@Builder
public class User implements Serializable {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 姓名
     */
    @DataMasking(maskFunc = DataMaskingFunc.ALL_MASK)
    private String name;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 邮箱
     */
    @DataMasking(maskFunc = DataMaskingFunc.ALL_MASK)
    private String email;

    public static void main(String[] args) {
        User user = User.builder().id(1L).age(28).name("dewey").build();
        System.out.println(user.toString());
    }
}