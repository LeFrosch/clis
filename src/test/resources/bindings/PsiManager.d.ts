import { UserDataHolderBase } from "./com.intellij.openapi.util.UserDataHolderBase";
import { Computable } from "./com.intellij.openapi.util.Computable";
import { Object } from "./java.lang.Object";
import { PsiElement } from "./com.intellij.psi.PsiElement";
import { Project } from "./com.intellij.openapi.project.Project";
import { VirtualFile } from "./com.intellij.openapi.vfs.VirtualFile";
import { FileViewProvider } from "./com.intellij.psi.FileViewProvider";
import { PsiDirectory } from "./com.intellij.psi.PsiDirectory";
import { PsiFile } from "./com.intellij.psi.PsiFile";
import { PsiModificationTracker } from "./com.intellij.psi.util.PsiModificationTracker";
import { PsiTreeChangeListener } from "./com.intellij.psi.PsiTreeChangeListener";
import { Disposable } from "./com.intellij.openapi.Disposable";

export abstract class PsiManager extends UserDataHolderBase {

    constructor();

    runInBatchFilesMode(p0: Computable): Object;

    areElementsEquivalent(p0: PsiElement, p1: PsiElement): boolean;

    isDisposed(): boolean;

    isInProject(p0: PsiElement): boolean;

    getProject(): Project;

    findCachedViewProvider(p0: VirtualFile): FileViewProvider;

    findViewProvider(p0: VirtualFile): FileViewProvider;

    findDirectory(p0: VirtualFile): PsiDirectory;

    findFile(p0: VirtualFile): PsiFile;

    getModificationTracker(): PsiModificationTracker;

    addPsiTreeChangeListener(p0: PsiTreeChangeListener): void;

    addPsiTreeChangeListener(p0: PsiTreeChangeListener, p1: Disposable): void;

    dropPsiCaches(): void;

    dropResolveCaches(): void;

    finishBatchFilesProcessingMode(): void;

    reloadFromDisk(p0: PsiFile): void;

    removePsiTreeChangeListener(p0: PsiTreeChangeListener): void;

    startBatchFilesProcessingMode(): void;

    static getInstance(p0: Project): PsiManager;
}