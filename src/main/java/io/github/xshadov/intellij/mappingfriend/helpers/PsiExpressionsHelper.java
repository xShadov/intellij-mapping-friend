package io.github.xshadov.intellij.mappingfriend.helpers;

import com.intellij.codeInsight.template.postfix.util.JavaPostfixTemplatesUtils;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiReturnStatement;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.Optional;

public class PsiExpressionsHelper {
	public static Optional<PsiExpression> builderMethodExpression(PsiElement context) {
		final PsiExpression topmostExpression = JavaPostfixTemplatesUtils.getTopmostExpression(context);
		if (topmostExpression != null)
			return Optional.of(topmostExpression);

		return getExpressionOnReturnStatement(context);
	}

	private static Optional<PsiExpression> getExpressionOnReturnStatement(PsiElement context) {
		return Optional.ofNullable(PsiTreeUtil.getParentOfType(context, PsiReturnStatement.class))
				.map(returnStat -> PsiTreeUtil.getChildOfType(returnStat, PsiExpression.class));
	}
}
