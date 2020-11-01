package io.github.xshadov.intellij.mappingfriend.helpers;

import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import lombok.NonNull;

import java.util.function.Predicate;

public class MethodPredicates {
	public static Predicate<PsiMethod> isStatic() {
		return method -> method.hasModifierProperty(PsiModifier.STATIC);
	}

	public static Predicate<PsiMethod> isNamed(@NonNull String name) {
		return method -> method.getName().equals(name);
	}

	public static Predicate<PsiMethod> isBuilder() {
		return isStatic().and(method -> method.getName().equals("builder"));
	}

	public static Predicate<PsiMethod> isBuild() {
		return method -> method.getName().equals("build");
	}

	public static Predicate<PsiMethod> isConstructor() {
		return PsiMethod::isConstructor;
	}

	public static Predicate<PsiMethod> isToString() {
		return method -> method.getName().equals("toString");
	}

	public static Predicate<PsiMethod> builderField() {
		return isConstructor().negate().and(isToString().negate()).and(isBuild().negate());
	}
}
