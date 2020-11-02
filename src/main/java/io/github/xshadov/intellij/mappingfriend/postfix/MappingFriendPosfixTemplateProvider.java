package io.github.xshadov.intellij.mappingfriend.postfix;

import com.google.common.collect.Sets;
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplate;
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class MappingFriendPosfixTemplateProvider implements PostfixTemplateProvider {
	@NotNull
	@Override
	public Set<PostfixTemplate> getTemplates() {
		return Sets.newHashSet(new BuilderChainOnExpressionPostfixTemplate(), new BuilderChainOnDeclarationPostfixTemplate());
	}

	@Override
	public boolean isTerminalSymbol(final char currentChar) {
		return currentChar == '.';
	}

	@Override
	public void preExpand(@NotNull final PsiFile file, @NotNull final Editor editor) {

	}

	@Override
	public void afterExpand(@NotNull final PsiFile file, @NotNull final Editor editor) {

	}

	@NotNull
	@Override
	public PsiFile preCheck(@NotNull final PsiFile copyFile, @NotNull final Editor realEditor, final int currentOffset) {
		return copyFile;
	}
}
