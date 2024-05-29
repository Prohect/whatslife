package property;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


public interface Tick {
    public void tick() throws CloneNotSupportedException, IllegalAccessException;
}
