package io.github.xshadov.intellij.mappingfriend.actions;

import com.google.common.base.Joiner;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
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
import java.util.stream.Collectors;

public class BuilderStringGenerator {
	private static final Logger logger = Logger.getInstance(BuilderStringGenerator.class);
	private static final String BUILDER_METHOD_CALL_TEMPLATE = ".%s() // (%s)\n";

	@NotNull
	public static String generate(final @NotNull PsiElement element) {
		final PsiClass classOfLocalVariable = PsiHelper.classOfLocalVariable(element)
				.orElseThrow(() -> new IllegalStateException("isAvailable returned true and this situation should not occur"));

		final List<PsiMethod> builderMethods = PsiMethodsHelper.all(classOfLocalVariable, MethodPredicates.isBuilder());
		if (builderMethods.size() != 1)
			throw new IllegalStateException("Should have exactly 1 builder method");

		final PsiMethod builderMethod = builderMethods.get(0);
		final PsiClass classOfBuilder = PsiTypesUtil.getPsiClass(builderMethod.getReturnType());

		// start the builder
		final StringBuilder builderString = new StringBuilder(builderStart(classOfLocalVariable));

		// list all the fields
		builderString.append(fieldChain(classOfLocalVariable, classOfBuilder));

		// finish builder
		builderString.append(buildFinish());

		return builderString.toString();
	}

	public static String builderStart(final PsiClass topClass) {
		return String.format("=%s.builder()\n", topClass.getQualifiedName());
	}

	public static String fieldChain(final PsiClass topClass, final PsiClass builderClass) {
		final Map<String, Boolean> fieldOptionalities = PsiFieldsHelper.fieldOptionalities(topClass, builderClass);

		return PsiMethodsHelper.all(builderClass, MethodPredicates.builderField()).stream()
				.map(method -> fieldString(fieldOptionalities, method))
				.collect(Collectors.joining());
	}

	public static String buildFinish() {
		return ".build();";
	}

	private static String fieldString(final Map<String, Boolean> fields, final PsiMethod method) {
		final List<String> fieldsFromParameters = Arrays.stream(method.getParameterList().getParameters())
				.map(param -> fields.get(param.getName()))
				.map(BuilderStringGenerator::optionalityOfField)
				.collect(Collectors.toList());

		if (fieldsFromParameters.size() == method.getParameterList().getParametersCount())
			return String.format(BUILDER_METHOD_CALL_TEMPLATE, method.getName(), Joiner.on(", ").join(fieldsFromParameters));

		final Boolean foundField = fields.get(method.getName());
		if (foundField != null)
			return String.format(BUILDER_METHOD_CALL_TEMPLATE, method.getName(), optionalityOfField(foundField));

		return String.format(BUILDER_METHOD_CALL_TEMPLATE, method.getName(), "unknown");
	}

	@NotNull
	private static String optionalityOfField(final Boolean fieldOptionality) {
		return fieldOptionality == null ? "unknown" : fieldOptionality ? "required" : "optional";
	}


}
