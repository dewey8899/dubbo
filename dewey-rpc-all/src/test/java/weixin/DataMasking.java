package weixin;

import java.lang.annotation.*;

/**
 * @auther dewey
 * @date 2022/7/20 19:25
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataMasking {

    DataMaskingFunc maskFunc() default DataMaskingFunc.NO_MASK;

}
