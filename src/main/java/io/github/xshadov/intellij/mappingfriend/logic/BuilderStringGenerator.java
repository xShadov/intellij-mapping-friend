package io.github.xshadov.intellij.mappingfriend.logic;

import com.google.common.base.Joiner;
import com.intellij.codeInsight.template.postfix.util.JavaPostfixTemplatesUtils;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTypesUtil;
import io.github.xshadov.intellij.mappingfriend.helpers.MethodPredicates;
import io.github.xshadov.intellij.mappingfriend.helpers.PsiFieldsHelper;
import io.github.xshadov.intellij.mappingfriend.helpers.PsiHelper;
import io.github.xshadov.intellij.mappingfriend.helpers.PsiMethodsHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class BuilderStringGenerator {
	private static final String BUILDER_METHOD_CALL_TEMPLATE = ".%s() // (%s)\n";

	public static BuilderGenerationResponse fromExpression(@NotNull PsiExpression expression) {
		final PsiClass builderClass = PsiTypesUtil.getPsiClass(expression.getType());

		final List<PsiMethod> hasBuildMethod = PsiMethodsHelper.all(builderClass, MethodPredicates.isBuild());
		if (hasBuildMethod.size() != 1)
			throw new IllegalArgumentException("Builder needs exactly 1 build method");

		final PsiMethod buildMethod = hasBuildMethod.get(0);

		final PsiClass ownerClass = PsiTypesUtil.getPsiClass(buildMethod.getReturnType());

		return fromTopClass(ownerClass);
	}

	public static BuilderGenerationResponse fromLocalVariable(@NotNull PsiElement localVariable) {
		final PsiClass ownerClass = PsiHelper.classOfLocalVariable(localVariable)
				.orElseThrow(() -> new IllegalArgumentException("Input is not a local variable element"));

		return fromTopClass(ownerClass);
	}

	public static BuilderGenerationResponse fromTopClass(@NotNull PsiClass topClass) {
		final List<PsiMethod> builderMethods = PsiMethodsHelper.all(topClass, MethodPredicates.isBuilder());
		if (builderMethods.size() != 1)
			throw new IllegalStateException("Should have exactly 1 builder method");

		final PsiMethod builderMethod = builderMethods.get(0);
		final PsiClass builderClass = PsiTypesUtil.getPsiClass(builderMethod.getReturnType());

		return BuilderGenerationResponse.builder()
				.builderStart(builderStart(topClass))
				.fieldChain(fieldChain(topClass, builderClass))
				.builderEnd(buildFinish())
				.build();
	}

	private static String builderStart(PsiClass topClass) {
		return String.format("=%s.builder()\n", topClass.getQualifiedName());
	}

	private static String fieldChain(PsiClass topClass, PsiClass builderClass) {
		final Map<String, Boolean> fieldOptionalities = PsiFieldsHelper.fieldOptionalities(topClass, builderClass);

		return PsiMethodsHelper.all(builderClass, MethodPredicates.builderField()).stream()
				.map(method -> fieldString(fieldOptionalities, method))
				.collect(Collectors.joining());
	}

	private static String buildFinish() {
		return ".build();";
	}

	private static String fieldString(Map<String, Boolean> fields, PsiMethod method) {
		final List<String> fieldsFromParameters = Arrays.stream(method.getParameterList().getParameters())
				.map(param -> fields.get(param.getName()))
				.filter(Objects::nonNull)
				.map(BuilderStringGenerator::optionalityOfField)
				.collect(Collectors.toList());

		if (fieldsFromParameters.size() == method.getParameterList().getParametersCount())
			return String.format(BUILDER_METHOD_CALL_TEMPLATE, method.getName(), Joiner.on(", ").join(fieldsFromParameters));

		final Boolean foundField = fields.get(method.getName());
		if (foundField != null)
			return String.format(BUILDER_METHOD_CALL_TEMPLATE, method.getName(), optionalityOfField(foundField));

		return String.format(BUILDER_METHOD_CALL_TEMPLATE, method.getName(), "unknown");
	}

	private static String optionalityOfField(Boolean fieldOptionality) {
		return fieldOptionality == null ? "unknown" : fieldOptionality ? "required" : "optional";
	}
}
