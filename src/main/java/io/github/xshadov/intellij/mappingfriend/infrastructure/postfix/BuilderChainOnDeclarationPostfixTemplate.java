package io.github.xshadov.intellij.mappingfriend.infrastructure.postfix;

import com.intellij.codeInsight.template.postfix.templates.PostfixTemplate;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import io.github.xshadov.intellij.mappingfriend.helpers.PsiElementsHelper;
import io.github.xshadov.intellij.mappingfriend.logic.BuilderGenerationResponse;
import io.github.xshadov.intellij.mappingfriend.logic.BuilderStringGenerator;
import org.jetbrains.annotations.NotNull;

public class BuilderChainOnDeclarationPostfixTemplate extends PostfixTemplate {
	protected BuilderChainOnDeclarationPostfixTemplate() {
		super("generateBuilderChainCall", "generate calls to every method in a builder of chosen class");
	}

	@Override
	public boolean isApplicable(@NotNull PsiElement context, @NotNull Document copyDocument, int newOffset) {
		return PsiElementsHelper.isBuilderClass(context);
	}

	@Override
	public void expand(@NotNull PsiElement context, @NotNull Editor editor) {
		final BuilderGenerationResponse generatedBuilder = BuilderStringGenerator.fromLocalVariable(context);

		editor.getDocument().insertString(context.getTextRange().getEndOffset(),
				String.format("\n%s%s", generatedBuilder.getFieldChain(), generatedBuilder.getBuilderEnd()));

		PsiDocumentManager.getInstance(context.getProject()).commitDocument(editor.getDocument());
		CodeStyleManager.getInstance(context.getProject()).reformat(PsiTreeUtil.getParentOfType(context, PsiLocalVariable.class));
	}
}
