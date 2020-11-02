package io.github.xshadov.intellij.mappingfriend.postfix;

import com.intellij.codeInsight.template.postfix.templates.PostfixTemplate;
import com.intellij.codeInsight.template.postfix.util.JavaPostfixTemplatesUtils;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiTypesUtil;
import io.github.xshadov.intellij.mappingfriend.actions.BuilderStringGenerator;
import io.github.xshadov.intellij.mappingfriend.helpers.MethodPredicates;
import io.github.xshadov.intellij.mappingfriend.helpers.PsiMethodsHelper;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BuilderChainOnExpressionPostfixTemplate extends PostfixTemplate {
	private static final Logger logger = Logger.getInstance(BuilderStringGenerator.class);

	protected BuilderChainOnExpressionPostfixTemplate() {
		super("generateBuilderChainCall", "generate calls to every method in a builder of chosen class");
	}

	@Override
	public boolean isApplicable(@NotNull final PsiElement context, @NotNull final Document copyDocument, final int newOffset) {
		final PsiExpression topmostExpression = JavaPostfixTemplatesUtils.getTopmostExpression(context);
		if (topmostExpression == null)
			return false;

		final PsiClass builderClass = PsiTypesUtil.getPsiClass(topmostExpression.getType());
		if (builderClass == null)
			return false;

		final List<PsiMethod> hasBuildMethod = PsiMethodsHelper.all(builderClass, MethodPredicates.isBuild());
		if (hasBuildMethod.size() != 1)
			return false;

		final PsiMethod buildMethod = hasBuildMethod.get(0);

		final PsiClass ownerClass = PsiTypesUtil.getPsiClass(buildMethod.getReturnType());
		return ownerClass != null && PsiMethodsHelper.has(ownerClass, MethodPredicates.isBuilder());
	}

	@Override
	public void expand(@NotNull final PsiElement context, @NotNull final Editor editor) {
		final PsiExpression topmostExpression = JavaPostfixTemplatesUtils.getTopmostExpression(context);
		if (topmostExpression == null)
			return;

		final PsiClass builderClass = PsiTypesUtil.getPsiClass(topmostExpression.getType());

		final List<PsiMethod> hasBuildMethod = PsiMethodsHelper.all(builderClass, MethodPredicates.isBuild());
		if (hasBuildMethod.size() != 1)
			return;

		final PsiMethod buildMethod = hasBuildMethod.get(0);

		final PsiClass ownerClass = PsiTypesUtil.getPsiClass(buildMethod.getReturnType());

		final String fieldChain = BuilderStringGenerator.fieldChain(ownerClass, builderClass);
		final String builderString = String.format("\n%s%s", fieldChain, BuilderStringGenerator.buildFinish());

		editor.getDocument().insertString(topmostExpression.getTextRange().getEndOffset(), builderString);

		PsiDocumentManager.getInstance(context.getProject()).commitDocument(editor.getDocument());
		CodeStyleManager.getInstance(context.getProject()).reformat(topmostExpression);
	}
}
