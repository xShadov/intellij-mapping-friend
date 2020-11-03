package io.github.xshadov.intellij.mappingfriend;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.JavaSdk;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.LanguageLevelModuleExtension;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess;
import com.intellij.pom.java.LanguageLevel;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.PsiTestUtil;
import com.intellij.testFramework.fixtures.DefaultLightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import com.intellij.util.PathUtil;

import java.io.File;

public abstract class BaseTestCase extends LightPlatformCodeInsightFixtureTestCase {
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

	@Override
	protected LightProjectDescriptor getProjectDescriptor() {
		return new DefaultLightProjectDescriptor() {
			@Override
			public Sdk getSdk() {
				return JavaSdk.getInstance().createJdk("java 1.8", "lib/mockJDK-1.8", false);
			}

			@Override
			public void configureModule(Module module, ModifiableRootModel model, ContentEntry contentEntry) {
				model.getModuleExtension(LanguageLevelModuleExtension.class).setLanguageLevel(LanguageLevel.JDK_1_8);
			}
		};
	}
}
