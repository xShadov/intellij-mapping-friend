package io.github.xshadov.intellij.mappingfriend.infrastructure.postfix;

import io.github.xshadov.intellij.mappingfriend.BaseTestCase;
import lombok.NonNull;

public class BuilderChainOnReturnPostfixTemplateTest extends BaseTestCase {
	public void testSimple() {
		testByName("Simple");
	}

	private void testByName(@NonNull String name) {
		myFixture.configureByFile("infrastructure/postfix/return/" + name + "Before.java");

		myFixture.type(".generateBuilderChainCall\t");

		myFixture.checkResultByFile("infrastructure/postfix/return/" + name + "After.java");
	}
}


