package arg;

public abstract class Arg<T> {

    T value;

    public Arg(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
