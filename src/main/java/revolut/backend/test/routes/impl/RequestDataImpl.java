package revolut.backend.test.routes.impl;

import revolut.backend.test.routes.RequestData;
import revolut.backend.test.utils.Json;

import java.util.List;
import java.util.Map;

public class RequestDataImpl implements RequestData {

    private final Map<String, List<String>> requestParams;
    private final byte[] postBodyBytes;

    public RequestDataImpl(Map<String, List<String>> requestParams, byte[] postBodyBytes) {
        this.requestParams = requestParams;
        this.postBodyBytes = postBodyBytes;
    }

    @Override
    public Map<String, List<String>> params() {
        return requestParams;
    }

    @Override
    public String param(String paramName) {
        return requestParams.get(paramName).get(0);
    }

    @Override
    public List<String> paramAllValues(String paramName) {
        return requestParams.get(paramName);
    }

    @Override
    public byte[] postBody() {
        return postBodyBytes;
    }

    @Override
    public <T> T postBodyAs(Class<T> tClass) {
        return Json.decodeValue(postBodyBytes, tClass);
    }

}
