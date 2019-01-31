package revolut.backend.test.routes;

@FunctionalInterface
public interface Handler<E> {
    void handle(E event);
}
