package Tests.losPollosAnnotations;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface UseTestingDatabase {
}
