package io.lithium.pokerstore.repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Interface for all OrientDB repositories
 */
public interface ORepository {

    default <T> List<T> toList(Iterator<T> iterator) {
        List<T> list = new ArrayList<>();
        iterator.forEachRemaining(list::add);
        return list;
    }
}
