package io.github.xshadov.intellij.mappingfriend.helpers;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import lombok.NonNull;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class PsiFieldsHelper {
	public static boolean isJavaxRequired(@NonNull PsiField param) {
		return FieldPredicates.hasJavaxNotNull().test(param);
	}

	public static Map<String, Boolean> fieldOptionalities(final PsiClass classOfLocalVariable, final PsiClass classOfBuilder) {
		final Map<String, PsiField> classFields = Arrays.stream(classOfLocalVariable.getFields())
				.collect(Collectors.toMap(PsiField::getName, field -> field));

		final Map<String, Boolean> builderClassFields = Arrays.stream(classOfBuilder.getFields())
				.collect(Collectors.toMap(PsiField::getName, PsiFieldsHelper::isJavaxRequired));

		classFields.forEach((name, field) -> {
			builderClassFields.merge(name, PsiFieldsHelper.isJavaxRequired(field), (oldValue, newValue) -> oldValue || newValue);
		});

		return builderClassFields;
	}
}
