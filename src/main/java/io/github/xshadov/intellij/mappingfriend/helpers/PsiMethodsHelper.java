package io.github.xshadov.intellij.mappingfriend.helpers;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PsiMethodsHelper {
	public static List<PsiMethod> all(PsiClass clazz, Predicate<PsiMethod> predicate) {
		return filter(clazz.getMethods(), predicate);
	}

	public static boolean has(PsiClass clazz, Predicate<PsiMethod> predicate) {
		return !filter(clazz.getMethods(), predicate).isEmpty();
	}

	private static List<PsiMethod> filter(PsiMethod[] methods, Predicate<PsiMethod> predicate) {
		return Arrays.stream(methods)
				.filter(predicate)
				.collect(Collectors.toList());
	}
}
