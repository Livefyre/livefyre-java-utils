package com.livefyre.validator;

import java.lang.reflect.Method;

import org.apache.commons.lang.StringUtils;

import com.livefyre.exceptions.LivefyreException;

public class ReflectiveValidator {
    public static <T> T validate(T data) {
        String dataClazzName = data.getClass().getSimpleName();
        String packageName = ReflectiveValidator.class.getPackage().getName();
        String validatorClazzName = packageName+"."+dataClazzName.replaceFirst("Data", "Validator");
        String message = null;
        try {
            Class<?> validator = Class.forName(validatorClazzName);
            Method m = validator.getDeclaredMethod("validate", data.getClass());
            Object obj = m.invoke(validator.newInstance(), data);
            if (obj != null && obj instanceof String) {
                message = (String) obj;
            }
        } catch (Exception e) {
            throw new LivefyreException("Something went horribly wrong. Contact us at tools@livefyre.com and attach: ", e);
        }
        
        if (StringUtils.isNotBlank(message)) {
            throw new IllegalArgumentException(message);
        }
        
        return data;
    }
}
