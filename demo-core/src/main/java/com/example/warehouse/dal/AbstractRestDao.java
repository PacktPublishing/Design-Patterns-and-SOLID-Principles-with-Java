package com.example.warehouse.dal;

import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.StreamSupport.stream;

public class AbstractRestDao {

    protected Stream<JSONObject> getArray(String url) throws UnirestException {
        JSONArray array = Unirest.get(url)
            .asJson()
            .getBody()
            .getArray();
        return stream(array.spliterator(), false)
            .map(JSONObject.class::cast);
    }

    protected JSONObject getObject(String url) throws UnirestException {
        return Unirest.get(url)
            .asJson()
            .getBody()
            .getObject();
    }

    protected void postObject(String url, Map<String, Object> params) throws UnirestException {
        Unirest.post(url)
            .fields(params)
            .asEmpty();
    }
}
