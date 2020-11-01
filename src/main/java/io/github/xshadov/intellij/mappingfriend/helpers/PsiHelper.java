package io.github.xshadov.intellij.mappingfriend.helpers;

import com.intellij.openapi.editor.Document;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiTypesUtil;
import lombok.NonNull;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class PsiHelper {
	public static Optional<PsiClass> containingClassOfElement(PsiElement psiElement) {
		final PsiClass psiClass = PsiTreeUtil.getParentOfType(psiElement, PsiClass.class, false);

		if (psiClass != null)
			return Optional.of(psiClass);

		final PsiFile containingFile = psiElement.getContainingFile();
		if (containingFile instanceof PsiClassOwner) {
			final PsiClass[] classes = ((PsiClassOwner) containingFile).getClasses();
			if (classes.length == 1)
				return Optional.of(classes[0]);
		}

		return Optional.empty();
	}

	public static Optional<PsiClass> classOfLocalVariable(@NonNull PsiElement element) {
		final PsiElement psiParent = PsiTreeUtil.getParentOfType(element, PsiLocalVariable.class);
		if (psiParent == null)
			return Optional.empty();

		final PsiLocalVariable psiLocal = (PsiLocalVariable) psiParent;
		if (!(psiLocal.getParent() instanceof PsiDeclarationStatement))
			return Optional.empty();

		return Optional.ofNullable(PsiTypesUtil.getPsiClass(psiLocal.getType()));
	}

	public static void addImportToFile(PsiDocumentManager psiDocumentManager, PsiJavaFile containingFile, Document document, Set<String> newImportList) {
		newImportList.removeIf(s -> s.startsWith("java.lang"));

		if (newImportList.isEmpty())
			return;

		final String importStatements = Arrays.stream(containingFile.getImportList().getImportStatements())
				.map(PsiImportStatement::getQualifiedName)
				.distinct()
				.map(q -> "import " + q + ";")
				.collect(Collectors.joining("\n"));

		int start = 0;
		PsiPackageStatement packageStatement = containingFile.getPackageStatement();
		if (packageStatement != null) {
			start = packageStatement.getTextLength() + packageStatement.getTextOffset();
		}

		if (StringUtils.isNotBlank(importStatements)) {
			document.insertString(start, importStatements);
			psiDocumentManager.commitDocument(document);
		}
	}
}
