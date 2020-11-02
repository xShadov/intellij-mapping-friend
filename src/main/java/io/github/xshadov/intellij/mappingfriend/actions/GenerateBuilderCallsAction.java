package io.github.xshadov.intellij.mappingfriend.actions;

import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDeclarationStatement;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import io.github.xshadov.intellij.mappingfriend.helpers.MethodPredicates;
import io.github.xshadov.intellij.mappingfriend.helpers.PsiHelper;
import io.github.xshadov.intellij.mappingfriend.helpers.PsiMethodsHelper;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class GenerateBuilderCallsAction extends PsiElementBaseIntentionAction {
	@Override
	public void invoke(@NotNull final Project project, final Editor editor, @NotNull final PsiElement element) throws IncorrectOperationException {
		final String builder = BuilderStringGenerator.generate(element);

		final Document document = PsiDocumentManager.getInstance(project).getDocument(element.getContainingFile());
		final PsiElement declaration = PsiTreeUtil.getParentOfType(element, PsiDeclarationStatement.class);
		WriteCommandAction.runWriteCommandAction(project, () -> {
			final PsiElement lastToken = PsiTreeUtil.getDeepestLast(declaration);
			if (lastToken.getText().equals(";"))
				document.replaceString(declaration.getTextRange().getEndOffset() - 1, declaration.getTextRange().getEndOffset(), builder);
			else
				document.insertString(declaration.getTextRange().getEndOffset(), builder);

			PsiDocumentManager.getInstance(project).commitDocument(document);
			CodeStyleManager.getInstance(project).reformat(declaration);
		});
	}

	@Override
	public boolean isAvailable(@NotNull final Project project, final Editor editor, @NotNull final PsiElement element) {
		// has static builder method
		return PsiHelper.classOfLocalVariable(element)
				.map(clazz -> PsiMethodsHelper.has(clazz, MethodPredicates.isBuilder()))
				.orElse(false);
	}

	@Nls(capitalization = Nls.Capitalization.Sentence)
	@NotNull
	@Override
	public String getFamilyName() {
		return "Generate builder chain call";
	}

	@NotNull
	@Override
	public String getText() {
		return "Generate builder chain call";
	}
}
