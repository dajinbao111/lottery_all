package org.wisestar.lottery.util;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangxu
 */
public class GroovyEngine {

    private static final Logger logger = LoggerFactory.getLogger(GroovyEngine.class);
    private static Map<String, GroovyObject> cache = new HashMap<>();

    private static class GroovyEngineHolder {
        private static final GroovyEngine INSTANCE = new GroovyEngine();
    }

    public static GroovyEngine getInstance() {
        return GroovyEngineHolder.INSTANCE;
    }

    public String executeScript(String script, String method, Object... param) {
        GroovyObject groovyObject = cache.get(script);
        if (groovyObject == null) {

            String content = null;
            try {
                Resource resource = new ClassPathResource("script/" + script + ".groovy");
                content = IOUtils.toString(resource.getInputStream(), "utf-8");
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }

            GroovyClassLoader loader = new GroovyClassLoader();
            Class groovyClass = loader.parseClass(content);
            try {
                groovyObject = (GroovyObject) groovyClass.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            cache.put(script, groovyObject);
        }

        Object result = groovyObject.invokeMethod(method, param);
        return result.toString();
    }

}
