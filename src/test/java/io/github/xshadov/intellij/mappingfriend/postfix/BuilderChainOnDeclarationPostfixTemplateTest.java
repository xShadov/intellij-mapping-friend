package io.github.xshadov.intellij.mappingfriend.postfix;

import io.github.xshadov.intellij.mappingfriend.BaseTestCase;
import lombok.NonNull;

public class BuilderChainOnDeclarationPostfixTemplateTest extends BaseTestCase {
	public void testSimple() {
		testByName("Simple");
	}

	private void testByName(@NonNull String name) {
		myFixture.configureByFile("declarationIntention/" + name + "Before.java");

		myFixture.launchAction(myFixture.findSingleIntention("Generate builder chain call"));

		myFixture.checkResultByFile("declarationIntention/" + name + "After.java");
	}
}


