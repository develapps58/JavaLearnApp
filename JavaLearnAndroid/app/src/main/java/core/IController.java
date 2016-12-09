package core;

/**
 * Created by Дмитрий on 06.11.2016.
 */

public interface IController<T> {
    boolean add(T item);
    boolean remove(T item);
}
