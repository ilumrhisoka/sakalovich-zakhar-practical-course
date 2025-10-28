package minispring.core;

import minispring.annotations.Autowired;
import minispring.annotations.Component;
import minispring.annotations.Scope;
import minispring.lifecycle.InitializingBean;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MiniApplicationContext {
    private final Map<Class<?>, Object> singletonBeans = new HashMap<>();

    private final Map<Class<?>, Class<?>> componentClasses = new HashMap<>();

    public MiniApplicationContext(String basePackage) {
        Set<Class<?>> classes = ClasspathScanner.findClasses(basePackage);
        registerComponents(classes);
        instantiateSingletons();
    }

    public <T> T getBean(Class<T> type) {
        if(singletonBeans.containsKey(type)) {
            return (T) singletonBeans.get(type);
        }

        if(componentClasses.containsKey(type)) {
            Class<?> componentClass = componentClasses.get(type);
            Scope scopeAnnotation = componentClass.getAnnotation(Scope.class);
            boolean isPrototype = scopeAnnotation != null && "prototype".equalsIgnoreCase(scopeAnnotation.value());

            try {
                T bean = (T) createAndInitializeBean(componentClass);
                if (!isPrototype) {
                    singletonBeans.put(componentClass, bean);
                }
                return bean;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        throw new RuntimeException("Bean not found: " + type.getName());
    }

    private void registerComponents(Set<Class<?>> classes) {
        for (Class<?> clazz : classes) {
            if (clazz.isAnnotationPresent(Component.class)) {
                componentClasses.put(clazz, clazz);
                System.out.println("Found component: " + clazz.getName());
            }
        }
    }

    private void instantiateSingletons() {
        for (Class<?> clazz : componentClasses.keySet()) {
            Scope scopeAnnotation = clazz.getAnnotation(Scope.class);
            boolean isPrototype = scopeAnnotation != null && "prototype".equalsIgnoreCase(scopeAnnotation.value());

            if (!isPrototype) {
                try {
                    Object beanInstance = createAndInitializeBean(clazz);
                    singletonBeans.put(clazz, beanInstance);
                    System.out.println("Instantiated singleton: " + clazz.getSimpleName());
                } catch (Exception e) {
                    throw new RuntimeException("Failed to instantiate singleton bean: " + clazz.getName(), e);
                }
            }
        }
    }

    private Object createAndInitializeBean(Class<?> clazz) throws Exception {
        Object instance = clazz.getDeclaredConstructor().newInstance();

        injectDependencies(instance, clazz);

        callLifecycleMethods(instance);

        return instance;
    }

    private void injectDependencies(Object instance, Class<?> clazz) throws IllegalAccessException {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Autowired.class)) {
                Class<?> dependencyType = field.getType();

                Object dependency = getBean(dependencyType);

                field.setAccessible(true);
                field.set(instance, dependency);
                System.out.println("   -> Injected dependency: " + dependencyType.getSimpleName() + " into " + clazz.getSimpleName());
            }
        }
    }

    private void callLifecycleMethods(Object instance) throws Exception {
        if (instance instanceof InitializingBean) {
            System.out.println("   -> Calling afterPropertiesSet() for " + instance.getClass().getSimpleName());
            ((InitializingBean) instance).afterPropertiesSet();
        }
    }
}
