package io.github.xshadov.intellij.mappingfriend;

import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess;
import com.intellij.testFramework.PsiTestUtil;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import com.intellij.util.PathUtil;

import java.io.File;

public class BaseTestCase extends LightPlatformCodeInsightFixtureTestCase {
	private static final String THIRD_PARTY_LIB_DIRECTORY = "lib";
	private static final String LOMBOK_JAR_NAME = "lombok-1.18.16.jar";

	@Override
	public void setUp() throws Exception {
		super.setUp();
		loadLombok();
	}

	private void loadLombok() {
		final String lombokLibPath = PathUtil.toSystemIndependentName(new File(THIRD_PARTY_LIB_DIRECTORY).getAbsolutePath());
		VfsRootAccess.allowRootAccess(myFixture.getProjectDisposable(), lombokLibPath);
		PsiTestUtil.addLibrary(myFixture.getProjectDisposable(), getModule(), "Lombok Library", lombokLibPath, LOMBOK_JAR_NAME);
	}

	@Override
	protected String getTestDataPath() {
		return "src/test/resources/testData";
	}
}
