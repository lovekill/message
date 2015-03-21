package com.hh.sdk.net;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2014/12/8.
 */
public abstract class StringRequest implements Request {
    HttpEngine.Method method  = HttpEngine.Method.GET;
    private IHttpResponse mResponse ;
    @Override
    public abstract String getUrl();

    @Override
    public HttpEngine.Method getMethod() {
        return method;
    }
    public void setResponse(IHttpResponse response){
       this.mResponse = response ;
    }

    @Override
    public Map<String, String> getParams() {
        Class clazz = getClass();
        Map<String,String> paramMap = new HashMap<String, String>() ;
        Field[] field = clazz.getDeclaredFields();
        try {
            for (Field f : field) {
                f.setAccessible(true);
                if (f.get(this) != null) {
                    paramMap.put(f.getName(),f.get(this).toString()) ;
                }
            }
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return  paramMap ;
    }

    @Override
    public IHttpResponse getHttpResponse() {
        return mResponse;
    }
}
