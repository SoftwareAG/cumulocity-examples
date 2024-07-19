package c8y.example.exceptions;

import com.google.common.base.Optional;
import lombok.experimental.UtilityClass;

import java.util.concurrent.Callable;

@UtilityClass
public class ExceptionHandler {

    public static <T> Optional<T> handled(Callable<T> callable) {
        try {
            return Optional.fromNullable(callable.call());
        } catch (Exception e) {
            return Optional.absent();
        }
    }
}