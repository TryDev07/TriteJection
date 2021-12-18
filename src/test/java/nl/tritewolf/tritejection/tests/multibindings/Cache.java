package nl.tritewolf.tritejection.tests.multibindings;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class Cache {

    @Getter
    private static Map<Class<?>, Integer> bindings = new HashMap<>();

}
