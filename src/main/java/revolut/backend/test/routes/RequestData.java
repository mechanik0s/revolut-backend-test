package revolut.backend.test.routes;

import java.util.List;
import java.util.Map;

public interface RequestData {

    Map<String, List<String>> params();

    String param(String paramName);

    List<String> paramAllValues(String paramName);

    byte[] postBody();

    <T> T postBodyAs(Class<T> tClass);

}
