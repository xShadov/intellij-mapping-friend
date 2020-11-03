package io.github.xshadov.intellij.mappingfriend.infrastructure.intention;

import io.github.xshadov.intellij.mappingfriend.BaseTestCase;
import lombok.NonNull;

public class BuilderChainOnIntentionTest extends BaseTestCase {
	public void testSimple() {
		testByName("Simple");
	}

	private void testByName(@NonNull String name) {
		myFixture.configureByFile("infrastructure/intention/" + name + "Before.java");

		myFixture.launchAction(myFixture.findSingleIntention("Generate builder chain call"));

		myFixture.checkResultByFile("infrastructure/intention/" + name + "After.java");
	}
}


