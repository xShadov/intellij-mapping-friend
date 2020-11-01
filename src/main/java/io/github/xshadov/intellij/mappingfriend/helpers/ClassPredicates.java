package io.github.xshadov.intellij.mappingfriend.helpers;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import lombok.NonNull;

import java.util.function.Predicate;

public class ClassPredicates {
	public static Predicate<PsiClass> isSystem() {
		return clazz -> !(clazz.getQualifiedName() == null || clazz.getQualifiedName().startsWith("java."));
	}
}
