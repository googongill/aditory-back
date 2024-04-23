package com.googongill.aditory;

import java.lang.reflect.Field;

public class TestUtils {
    public static <T> Long setEntityId(Long id, T entity) throws NoSuchFieldException, IllegalAccessException {
        Class<?> entityClass = entity.getClass();
        Field idField = entityClass.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(entity, id);
        return (Long) idField.get(entity);
    }
}
