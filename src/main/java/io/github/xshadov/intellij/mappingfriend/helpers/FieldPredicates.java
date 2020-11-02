package io.github.xshadov.intellij.mappingfriend.helpers;

import com.intellij.psi.PsiField;
import lombok.NonNull;

import java.util.function.Predicate;

public class FieldPredicates {
	public static Predicate<PsiField> hasAnnotation(@NonNull String annotation) {
		return param -> param.hasAnnotation(annotation);
	}

	public static Predicate<PsiField> hasJavaxNotNull() {
		return hasAnnotation("javax.validation.constraints.NotNull");
	}
}
