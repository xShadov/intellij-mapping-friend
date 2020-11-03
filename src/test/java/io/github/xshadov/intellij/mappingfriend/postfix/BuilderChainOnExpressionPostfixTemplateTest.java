package io.github.xshadov.intellij.mappingfriend.postfix;

import io.github.xshadov.intellij.mappingfriend.BaseTestCase;
import lombok.NonNull;

public class BuilderChainOnExpressionPostfixTemplateTest extends BaseTestCase {
	public void testSimple() {
		testByName("Simple");
	}

	private void testByName(@NonNull String name) {
		myFixture.configureByFile("postfix/expression/" + name + "Before.java");

		myFixture.type(".generateBuilderChainCall\t");

		myFixture.checkResultByFile("postfix/expression/" + name + "After.java");
	}
}


