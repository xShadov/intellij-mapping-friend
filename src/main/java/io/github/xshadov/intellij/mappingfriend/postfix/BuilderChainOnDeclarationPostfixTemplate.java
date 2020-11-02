package io.github.xshadov.intellij.mappingfriend.postfix;

import com.intellij.codeInsight.template.postfix.templates.PostfixTemplate;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiTypesUtil;
import io.github.xshadov.intellij.mappingfriend.actions.BuilderStringGenerator;
import io.github.xshadov.intellij.mappingfriend.helpers.MethodPredicates;
import io.github.xshadov.intellij.mappingfriend.helpers.PsiHelper;
import io.github.xshadov.intellij.mappingfriend.helpers.PsiMethodsHelper;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class BuilderChainOnDeclarationPostfixTemplate extends PostfixTemplate {
	protected BuilderChainOnDeclarationPostfixTemplate() {
		super("generateBuilderChainCall", "generate calls to every method in a builder of chosen class");
	}

	@Override
	public boolean isApplicable(@NotNull final PsiElement context, @NotNull final Document copyDocument, final int newOffset) {
		return PsiHelper.classOfLocalVariable(context)
				.map(clazz -> PsiMethodsHelper.has(clazz, MethodPredicates.isBuilder()))
				.orElse(false);
	}

	@Override
	public void expand(@NotNull final PsiElement context, @NotNull final Editor editor) {
		final PsiClass ownerClass = PsiHelper.classOfLocalVariable(context)
				.orElseThrow(() -> new IllegalStateException("Template was applicable, this should not occur"));

		final List<PsiMethod> builderMethods = PsiMethodsHelper.all(ownerClass, MethodPredicates.isBuilder());
		if (builderMethods.size() != 1)
			throw new IllegalStateException("Should have exactly 1 builder method");

		final PsiMethod builderMethod = builderMethods.get(0);
		final PsiClass builderClass = PsiTypesUtil.getPsiClass(builderMethod.getReturnType());

		final String fieldChain = BuilderStringGenerator.fieldChain(ownerClass, builderClass);
		final String builderString = String.format("\n%s%s", fieldChain, BuilderStringGenerator.buildFinish());

		editor.getDocument().insertString(context.getTextRange().getEndOffset(), builderString);

		PsiDocumentManager.getInstance(context.getProject()).commitDocument(editor.getDocument());
		CodeStyleManager.getInstance(context.getProject()).reformat(PsiTreeUtil.getParentOfType(context, PsiLocalVariable.class));
	}
}
