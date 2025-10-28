package minispring.lifecycle;

public interface InitializingBean {
    void afterPropertiesSet() throws Exception;
}
