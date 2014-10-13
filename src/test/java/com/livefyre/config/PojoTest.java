package com.livefyre.config;

import java.lang.reflect.ParameterizedType;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.PojoValidator;
import com.openpojo.validation.rule.impl.GetterMustExistRule;
import com.openpojo.validation.rule.impl.NoNestedClassRule;
import com.openpojo.validation.rule.impl.NoPrimitivesRule;
import com.openpojo.validation.rule.impl.NoPublicFieldsRule;
import com.openpojo.validation.rule.impl.NoStaticExceptFinalRule;
import com.openpojo.validation.rule.impl.SetterMustExistRule;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;

public class PojoTest<T> extends LfTest {
    @Test
    @Category(UnitTest.class)
    @SuppressWarnings("unchecked")
    public void testPojoStructureAndBehavior() {
        if (this.getClass() == PojoTest.class) {
            return;
        }
        Class<T> typeParameterClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        
        PojoValidator pojoValidator = new PojoValidator();

        // Create Rules to validate structure for POJO_PACKAGE
        pojoValidator.addRule(new NoPublicFieldsRule());
        pojoValidator.addRule(new NoPrimitivesRule());
        pojoValidator.addRule(new NoStaticExceptFinalRule());
        pojoValidator.addRule(new GetterMustExistRule());
        pojoValidator.addRule(new SetterMustExistRule());
        pojoValidator.addRule(new NoNestedClassRule());

        // Create Testers to validate behaviour for POJO_PACKAGE
        pojoValidator.addTester(new SetterTester());
        pojoValidator.addTester(new GetterTester());
        
        PojoClass pojoClass = PojoClassFactory.getPojoClass(typeParameterClass);
        pojoValidator.runValidation(pojoClass);
    }
}
