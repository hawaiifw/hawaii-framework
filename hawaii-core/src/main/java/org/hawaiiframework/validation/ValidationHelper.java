package org.hawaiiframework.validation;

import org.apache.commons.lang3.StringUtils;
import org.hamcrest.beans.PropertyUtil;
import org.hawaiiframework.exception.HawaiiException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.greaterThan;

/**
 * Created by hans.lammers on 8-6-2016.
 */
public class ValidationHelper {

    private Object objectToValidate;
    private ValidationResult validationResult;
    private Map<String, Integer> maxLengthStrings = new HashMap<>();

    /**
     * Creates a validation helper instance for the object that has to be validated and the validationresult
     * @param objectToValidate The object to validate
     * @param validationResult the contextual state about the validation process
     */
    public ValidationHelper(Object objectToValidate, ValidationResult validationResult) {
        this.objectToValidate = objectToValidate;
        this.validationResult = validationResult;
    }

    /**
     * Adds a field with a maximum length validation to the map.
     *
     * @param fieldName the name of the field to validate
     * @param maxLength the contextual state about the validation process
     */
    public ValidationHelper withMaxLengthField(String fieldName, Integer maxLength) {
        maxLengthStrings.put(fieldName, maxLength);
        return this;
    }

    /**
     * Perform the validation
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public void validate() throws InvocationTargetException, IllegalAccessException {
        for (Map.Entry<String, Integer> entry : maxLengthStrings.entrySet()) {
            Method getter = PropertyUtil.getPropertyDescriptor(entry.getKey(), objectToValidate).getReadMethod();
            if (getter.getReturnType() != String.class) {
                throw new HawaiiException(String.format("Field %s is not of type String", entry.getKey()));
            }
            Object value = getter.invoke(objectToValidate);
            String strValue = (String) value;
            if (StringUtils.isBlank(strValue)) {
                validationResult.rejectValue(entry.getKey(), "required");
            } else {
                validationResult.rejectValueIf(strValue.length(), greaterThan(entry.getValue()), entry.getKey(), "length");
            }
        }
    }

}
