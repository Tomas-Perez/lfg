package persistence.manager;

import java.util.List;

/**
 * @author Tomas Perez Molina
 */
public interface Manager<T> {

    int add(T entity);

    T get(int id);

    void delete(int id);

    default boolean exists(int id) {
        return get(id) != null;
    }

    default void wipe() {
        list().forEach(this::delete);
    }

    List<Integer> list();
}
