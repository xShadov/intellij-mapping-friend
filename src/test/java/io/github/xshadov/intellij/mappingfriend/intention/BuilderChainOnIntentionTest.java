package io.github.xshadov.intellij.mappingfriend.intention;

import io.github.xshadov.intellij.mappingfriend.BaseTestCase;
import lombok.NonNull;

public class BuilderChainOnIntentionTest extends BaseTestCase {
	public void testSimple() {
		testByName("Simple");
	}

	private void testByName(@NonNull String name) {
		myFixture.configureByFile("intention/" + name + "Before.java");

		myFixture.launchAction(myFixture.findSingleIntention("Generate builder chain call"));

		myFixture.checkResultByFile("intention/" + name + "After.java");
	}
}


