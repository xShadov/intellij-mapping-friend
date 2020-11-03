package io.github.xshadov.intellij.mappingfriend.helpers;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.util.PsiTypesUtil;
import lombok.NonNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class FieldPredicates {
	private static final List<String> WRAPPER_TYPES = Arrays.asList(
			"Integer", "Byte", "Short", "Long", "String", "Character", "Float", "Double", "Boolean");

	public static Predicate<PsiField> hasAnnotation(@NonNull String annotation) {
		return param -> param.hasAnnotation(annotation);
	}

	public static Predicate<PsiField> hasJavaxNotNull() {
		return hasAnnotation("javax.validation.constraints.NotNull");
	}

	public static Predicate<PsiField> isPrimitive() {
		return field -> field.getType() instanceof PsiPrimitiveType;
	}

	public static Predicate<PsiField> isComplex() {
		return field -> {
			if (isPrimitive().test(field))
				return false;

			final PsiType type = field.getType();
			if (!(type instanceof PsiClassReferenceType))
				return false;

			final PsiClassReferenceType classReference = (PsiClassReferenceType) type;

			final PsiClass psiClass = PsiTypesUtil.getPsiClass(classReference);
			if (psiClass == null || psiClass.getQualifiedName() == null) // can be null for some JDK related reasons
				return !WRAPPER_TYPES.contains(classReference.getName());

			return !psiClass.getQualifiedName().startsWith("java.lang");
		};
	}

	public static Predicate<PsiField> isRequired() {
		return field -> {
			final boolean javaxRequired = hasJavaxNotNull().test(field);
			final boolean isPrimitive = isPrimitive().test(field);

			return isPrimitive || javaxRequired;
		};
	}
}
