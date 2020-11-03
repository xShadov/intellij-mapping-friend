package io.github.xshadov.intellij.mappingfriend.helpers;

import com.intellij.psi.PsiElement;
import lombok.NonNull;

public class PsiElementsHelper {
	public static boolean isBuilderExpression(@NonNull PsiElement element) {
		return ElementPredicates.isBuilderExpression().test(element);
	}

	public static boolean isBuilderClass(@NonNull PsiElement element) {
		return ElementPredicates.isBuilderClass().test(element);
	}
}
