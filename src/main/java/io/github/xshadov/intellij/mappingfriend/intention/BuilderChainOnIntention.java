package io.github.xshadov.intellij.mappingfriend.intention;

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
import io.github.xshadov.intellij.mappingfriend.helpers.PsiElementsHelper;
import io.github.xshadov.intellij.mappingfriend.logic.BuilderGenerationResponse;
import io.github.xshadov.intellij.mappingfriend.logic.BuilderStringGenerator;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class BuilderChainOnIntention extends PsiElementBaseIntentionAction {
	@Override
	public boolean isAvailable(@NotNull final Project project, final Editor editor, @NotNull final PsiElement element) {
		return PsiElementsHelper.isBuilderClass(element);
	}

	@Override
	public void invoke(@NotNull final Project project, final Editor editor, @NotNull final PsiElement element) throws IncorrectOperationException {
		final BuilderGenerationResponse generatedBuilder = BuilderStringGenerator.fromLocalVariable(element);

		final Document document = PsiDocumentManager.getInstance(project).getDocument(element.getContainingFile());
		final PsiElement declaration = PsiTreeUtil.getParentOfType(element, PsiDeclarationStatement.class);
		WriteCommandAction.runWriteCommandAction(project, () -> {
			final PsiElement lastToken = PsiTreeUtil.getDeepestLast(declaration);

			final int declarationEndIndex = declaration.getTextRange().getEndOffset();
			if (lastToken.getText().equals(";"))
				document.replaceString(declarationEndIndex - 1, declarationEndIndex, generatedBuilder.join());
			else
				document.insertString(declarationEndIndex, generatedBuilder.join());

			PsiDocumentManager.getInstance(project).commitDocument(document);
			CodeStyleManager.getInstance(project).reformat(declaration);
		});
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
