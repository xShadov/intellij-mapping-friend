package io.github.xshadov.intellij.mappingfriend.helpers;

import com.intellij.psi.PsiField;
import com.intellij.psi.PsiPrimitiveType;
import lombok.NonNull;

import java.util.function.Predicate;

public class FieldPredicates {
	public static Predicate<PsiField> hasAnnotation(@NonNull String annotation) {
		return param -> param.hasAnnotation(annotation);
	}

	public static Predicate<PsiField> hasJavaxNotNull() {
		return hasAnnotation("javax.validation.constraints.NotNull");
	}

	public static Predicate<PsiField> isPrimitive() {
		return field -> field.getType() instanceof PsiPrimitiveType;
	}

	public static Predicate<PsiField> isRequired() {
		return field -> {
			final boolean javaxRequired = hasJavaxNotNull().test(field);
			final boolean isPrimitive = isPrimitive().test(field);

			return isPrimitive || javaxRequired;
		};
	}
}
