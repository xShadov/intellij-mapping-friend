package io.github.xshadov.intellij.mappingfriend.logic;

import com.google.common.base.Joiner;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTypesUtil;
import io.github.xshadov.intellij.mappingfriend.helpers.MethodPredicates;
import io.github.xshadov.intellij.mappingfriend.helpers.PsiFieldsHelper;
import io.github.xshadov.intellij.mappingfriend.helpers.PsiHelper;
import io.github.xshadov.intellij.mappingfriend.helpers.PsiMethodsHelper;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BuilderStringGenerator {
	private static final String BUILDER_METHOD_CALL_TEMPLATE = ".%s()\n";
	private static final String MULTIPLE_PARAMETERS_BUILDER_METHOD_CALL_TEMPLATE = ".%s() // (%s)\n";

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

		final Map<OptionalityType, List<String>> linesWithOptionalities = PsiMethodsHelper.all(builderClass, MethodPredicates.builderField()).stream()
				.map(method -> fieldString(fieldOptionalities, method))
				.collect(Collectors.groupingBy(Pair::getLeft, Collectors.mapping(Pair::getRight, Collectors.toList())));

		final List<String> finalLines = Lists.newArrayList();
		for (OptionalityType type : OptionalityType.values()) {
			if (!linesWithOptionalities.getOrDefault(type, Collections.emptyList()).isEmpty()) {
				// adding \t here because of formatting option 'keep comment in first column when reformatting'
				// this bypasses this option to allow comment to be formatted same as builder method calls
				finalLines.add(String.format("\t// (%s)\n", type.name().toLowerCase()));
				finalLines.addAll(linesWithOptionalities.get(type));
			}
		}

		return Joiner.on("").join(finalLines);
	}

	private static String buildFinish() {
		return ".build();";
	}

	private static Pair<OptionalityType, String> fieldString(Map<String, Boolean> fields, PsiMethod method) {
		if (method.getParameterList().getParametersCount() == 0)
			return Pair.of(OptionalityType.NO_PARAMETERS, String.format(BUILDER_METHOD_CALL_TEMPLATE, method.getName()));

		final List<OptionalityType> fieldsFromParameters = Arrays.stream(method.getParameterList().getParameters())
				.map(param -> fields.get(param.getName()))
				.map(OptionalityType::of)
				.collect(Collectors.toList());

		if (fieldsFromParameters.size() == 1)
			return Pair.of(fieldsFromParameters.get(0), String.format(BUILDER_METHOD_CALL_TEMPLATE, method.getName()));

		final String requirements = Joiner.on(", ").join(fieldsFromParameters.stream()
				.map(val -> val.name().toLowerCase()).collect(Collectors.toList()));
		return Pair.of(OptionalityType.MULTIPLE_PARAMETERS,
				String.format(MULTIPLE_PARAMETERS_BUILDER_METHOD_CALL_TEMPLATE, method.getName(), requirements));
	}
}
