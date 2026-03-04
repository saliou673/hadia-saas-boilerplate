package com.hadiasaas.infrastructure.adapter.out.persistence;

import com.hadiasaas.domain.exceptions.DataBaseException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.dao.DataAccessException;

import java.util.function.Supplier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
/** Utility class that wraps JPA operations and converts {@link org.springframework.dao.DataAccessException} into {@link com.hadiasaas.domain.exceptions.DataBaseException}. */
public final class AdapterPersistenceUtils {

    public static <T> T executeDbOperation(Supplier<T> operation, String errorMessage) {
        try {
            return operation.get();
        } catch (DataAccessException e) {
            throw new DataBaseException(errorMessage, e);
        }
    }

    public static void executeDbOperation(Runnable operation, String errorMessage) {
        try {
            operation.run();
        } catch (DataAccessException e) {
            throw new DataBaseException(errorMessage, e);
        }
    }

}
