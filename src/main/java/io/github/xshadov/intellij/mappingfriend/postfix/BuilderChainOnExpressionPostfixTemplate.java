package io.github.xshadov.intellij.mappingfriend.postfix;

import com.intellij.codeInsight.template.postfix.templates.PostfixTemplate;
import com.intellij.codeInsight.template.postfix.util.JavaPostfixTemplatesUtils;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.codeStyle.CodeStyleManager;
import io.github.xshadov.intellij.mappingfriend.helpers.PsiElementsHelper;
import io.github.xshadov.intellij.mappingfriend.logic.BuilderGenerationResponse;
import io.github.xshadov.intellij.mappingfriend.logic.BuilderStringGenerator;
import org.jetbrains.annotations.NotNull;

public class BuilderChainOnExpressionPostfixTemplate extends PostfixTemplate {
	protected BuilderChainOnExpressionPostfixTemplate() {
		super("generateBuilderChainCall", "generate calls to every method in a builder of chosen class");
	}

	@Override
	public boolean isApplicable(@NotNull final PsiElement context, @NotNull final Document copyDocument, final int newOffset) {
		return PsiElementsHelper.isBuilderExpression(context);
	}

	@Override
	public void expand(@NotNull final PsiElement context, @NotNull final Editor editor) {
		final PsiExpression topmostExpression = JavaPostfixTemplatesUtils.getTopmostExpression(context);

		final BuilderGenerationResponse generatedBuilder = BuilderStringGenerator.fromExpression(topmostExpression);
		final String builderString = String.format("\n%s%s", generatedBuilder.getFieldChain(), generatedBuilder.getBuilderEnd());

		editor.getDocument().insertString(topmostExpression.getTextRange().getEndOffset(), builderString);

		PsiDocumentManager.getInstance(context.getProject()).commitDocument(editor.getDocument());
		CodeStyleManager.getInstance(context.getProject()).reformat(topmostExpression);
	}
}
