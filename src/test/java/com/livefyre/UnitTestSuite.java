package com.livefyre;

import org.junit.experimental.categories.Categories.IncludeCategory;
import org.junit.runner.RunWith;

import com.googlecode.junittoolbox.SuiteClasses;
import com.googlecode.junittoolbox.WildcardPatternSuite;
import com.livefyre.config.UnitTest;

@RunWith(WildcardPatternSuite.class)
@IncludeCategory(UnitTest.class)
@SuiteClasses("**/*Test.class")
public class UnitTestSuite {}
