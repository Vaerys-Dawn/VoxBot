import java.lang.annotation.*;

/**
 * Created by Vaerys on 04/06/2016.
 */

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandAnnotation {
    String name();
    String type() default "general";
    String channel() default "any";
    String description() default "Has no Description Set Yet";
}
