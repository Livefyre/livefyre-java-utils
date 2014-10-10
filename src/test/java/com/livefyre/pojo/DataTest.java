package com.livefyre.pojo;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.livefyre.config.UnitTest;
import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.filters.FilterPackageInfo;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.PojoValidator;
import com.openpojo.validation.affirm.Affirm;
import com.openpojo.validation.rule.impl.GetterMustExistRule;
import com.openpojo.validation.rule.impl.NoNestedClassRule;
import com.openpojo.validation.rule.impl.NoPrimitivesRule;
import com.openpojo.validation.rule.impl.NoPublicFieldsRule;
import com.openpojo.validation.rule.impl.NoStaticExceptFinalRule;
import com.openpojo.validation.rule.impl.SetterMustExistRule;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;

@Category(UnitTest.class)
public class DataTest {
    private static final int EXPECTED_CLASS_COUNT = 4;

    // The package to test
    private static final String POJO_PACKAGE = "com.livefyre.model";

    private List<PojoClass> pojoClasses;
    private PojoValidator pojoValidator;

    @Before
    public void setup() {
        pojoClasses = PojoClassFactory.getPojoClasses(POJO_PACKAGE, new FilterPackageInfo());

        pojoValidator = new PojoValidator();

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
    }

    @Test
    public void ensureExpectedPojoCount() {
        Affirm.affirmEquals("Classes added / removed?", EXPECTED_CLASS_COUNT, pojoClasses.size());
    }

    @Test
    public void testPojoStructureAndBehavior() {
        for (PojoClass pojoClass : pojoClasses) {
            pojoValidator.runValidation(pojoClass);
        }
    }
}
