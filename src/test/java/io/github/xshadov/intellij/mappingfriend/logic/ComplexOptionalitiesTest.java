package io.github.xshadov.intellij.mappingfriend.logic;

import io.github.xshadov.intellij.mappingfriend.BaseTestCase;
import lombok.NonNull;

public class ComplexOptionalitiesTest extends BaseTestCase {
	public void testComplex() {
		testByName("ComplexOptionalities");
	}

	private void testByName(@NonNull String name) {
		myFixture.configureByFile("logic/" + name + "Before.java");

		myFixture.type(".generateBuilderChainCall\t");

		myFixture.checkResultByFile("logic/" + name + "After.java");
	}
}
