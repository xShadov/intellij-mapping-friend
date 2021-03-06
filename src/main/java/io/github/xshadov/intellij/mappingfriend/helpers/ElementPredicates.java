package io.github.xshadov.intellij.mappingfriend.helpers;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTypesUtil;

import java.util.function.Predicate;

public class ElementPredicates {
	public static Predicate<PsiElement> isBuilderExpression() {
		return element -> PsiExpressionsHelper.builderMethodExpression(element)
				.map(expr -> PsiTypesUtil.getPsiClass(expr.getType()))
				.map(builderClass -> PsiMethodsHelper.all(builderClass, MethodPredicates.isBuild()))
				.filter(methods -> methods.size() == 1)
				.map(methods -> methods.get(0))
				.map(buildMethod -> PsiTypesUtil.getPsiClass(buildMethod.getReturnType()))
				.map(ownerClass -> PsiMethodsHelper.has(ownerClass, MethodPredicates.isBuilder()))
				.orElse(false);
	}

	public static Predicate<PsiElement> isBuilderClass() {
		return element -> PsiHelper.classOfLocalVariable(element)
				.map(clazz -> PsiMethodsHelper.has(clazz, MethodPredicates.isBuilder()))
				.orElse(false);
	}
}
