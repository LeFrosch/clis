import * as i0 from 'com.intellij.openapi.util';
import * as i1 from 'java.lang';
import * as i2 from 'com.intellij.openapi.project';
import * as i3 from 'com.intellij.openapi.vfs';
import * as i4 from 'com.intellij.psi.util';
import * as i5 from 'com.intellij.openapi';

export abstract class PsiManager extends i0.UserDataHolderBase {
    constructor();

    runInBatchFilesMode(p0: i0.Computable,): i1.Object;

    areElementsEquivalent(p0: PsiElement, p1: PsiElement,): boolean;

    isDisposed(): boolean;

    isInProject(p0: PsiElement,): boolean;

    getProject(): i2.Project;

    findCachedViewProvider(p0: i3.VirtualFile,): FileViewProvider;

    findViewProvider(p0: i3.VirtualFile,): FileViewProvider;

    findDirectory(p0: i3.VirtualFile,): PsiDirectory;

    findFile(p0: i3.VirtualFile,): PsiFile;

    getModificationTracker(): i4.PsiModificationTracker;

    addPsiTreeChangeListener(p0: PsiTreeChangeListener,): void;
    addPsiTreeChangeListener(p0: PsiTreeChangeListener, p1: i5.Disposable,): void;

    dropPsiCaches(): void;

    dropResolveCaches(): void;

    finishBatchFilesProcessingMode(): void;

    reloadFromDisk(p0: PsiFile,): void;

    removePsiTreeChangeListener(p0: PsiTreeChangeListener,): void;

    startBatchFilesProcessingMode(): void;

    static getInstance(p0: i2.Project,): PsiManager;
}