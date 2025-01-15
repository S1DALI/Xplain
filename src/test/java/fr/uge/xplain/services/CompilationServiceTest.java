package fr.uge.xplain.services;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompilationServiceTest {

    private static Object invokeStaticMethod(Method method, Object... args) {
        try {
            return method.invoke(null, args);
        } catch (IllegalAccessException e) {
            throw (IllegalAccessError) new IllegalAccessError().initCause(e);
        } catch (InvocationTargetException e) {
            var cause = e.getCause();
            switch (cause) {
                case RuntimeException rte -> throw rte;
                case Error error -> throw error;
                default -> throw new UndeclaredThrowableException(cause);
            }
        }
    }

    @Nested
    class ExtractClassName {
        private Method getMethod() {
            try {
                var extractClassName = CompilationService.class.getDeclaredMethod("extractClassName", String.class);
                extractClassName.setAccessible(true);
                return extractClassName;
            } catch (NoSuchMethodException e) {
                throw new AssertionError(e);
            }
        }

        @Test
        void records() {
            final var extractClassName = getMethod();
            assertAll(
                    () -> assertEquals("FamousRecord", invokeStaticMethod(extractClassName, "public record FamousRecord(String name) {}")),
                    () -> assertEquals("defaultName", invokeStaticMethod(extractClassName, "record PackageRecord(String name) {}")),
                    () -> assertEquals("defaultName", invokeStaticMethod(extractClassName, "private record HiddenRecord(String name) {}")),
                    () -> assertEquals("ParamRecord", invokeStaticMethod(extractClassName, "public record ParamRecord<T>(T value) {}"))
            );
        }

        @Test
        void classes() {
            final var extractClassName = getMethod();
            assertAll(
                    () -> assertEquals("FamousClass", invokeStaticMethod(extractClassName, "public class FamousClass {}")),
                    () -> assertEquals("defaultName", invokeStaticMethod(extractClassName, "class PackageClass {}")),
                    () -> assertEquals("defaultName", invokeStaticMethod(extractClassName, "private class HiddenClass {}")),
                    () -> assertEquals("ParamClass", invokeStaticMethod(extractClassName, "public class ParamClass<T> {}"))
            );
        }

        @Test
        void enums() {
            final var extractClassName = getMethod();
            assertAll(
                    () -> assertEquals("FamousEnum", invokeStaticMethod(extractClassName, "public enum FamousEnum { ONE, TWO, THREE }")),
                    () -> assertEquals("defaultName", invokeStaticMethod(extractClassName, "enum PackageEnum { ONE, TWO, THREE }"))
            );
        }
    }

    @Nested
    class Compile {
        @Test
        void success() {
            var sourceCode = "public class FamousClass {}";
            assertTrue(CompilationService.compile(sourceCode).isEmpty());
        }

        @Test
        void failure() {
            var sourceCode = "public classs FamousClass {}";
            assertFalse(CompilationService.compile(sourceCode).isEmpty());
        }
    }

}