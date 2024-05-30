import { Object } from "./java.lang.Object";
import { PossiblyDumbAware } from "./com.intellij.openapi.project.PossiblyDumbAware";
import { ActionUpdateThreadAware } from "./com.intellij.openapi.actionSystem.ActionUpdateThreadAware";
import { Key } from "./com.intellij.openapi.util.Key";
import { Icon } from "./javax.swing.Icon";
import { Supplier } from "./java.util.function.Supplier";
import { AnActionEvent } from "./com.intellij.openapi.actionSystem.AnActionEvent";
import { ActionUpdateThread } from "./com.intellij.openapi.actionSystem.ActionUpdateThread";
import { Presentation } from "./com.intellij.openapi.actionSystem.Presentation";
import { ShortcutSet } from "./com.intellij.openapi.actionSystem.ShortcutSet";
import { JComponent } from "./javax.swing.JComponent";
import { Disposable } from "./com.intellij.openapi.Disposable";
import { List } from "./java.util.List";
import { Project } from "./com.intellij.openapi.project.Project";

export abstract class AnAction extends Object implements PossiblyDumbAware, ActionUpdateThreadAware{

    static readonly EMPTY_ARRAY: AnAction[];

    static readonly ACTIONS_KEY: Key;

    constructor();

    constructor(p0: string);

    constructor(p0: string, p1: string, p2: Icon);

    constructor(p0: Supplier);

    constructor(p0: Supplier, p1: Supplier);

    constructor(p0: Supplier, p1: Supplier, p2: Supplier);

    constructor(p0: Supplier, p1: Supplier, p2: Icon);

    constructor(p0: Supplier, p1: Icon);

    constructor(p0: Icon);

    actionPerformed(p0: AnActionEvent): void;

    displayTextInToolbar(): boolean;

    isDefaultIcon(): boolean;

    isDumbAware(): boolean;

    isInInjectedContext(): boolean;

    useSmallerFontForTextInToolbar(): boolean;

    getActionUpdateThread(): ActionUpdateThread;

    isEnabledInModalContext(): boolean;

    getTemplatePresentation(): Presentation;

    getShortcutSet(): ShortcutSet;

    getTemplateText(): string;

    copyFrom(p0: AnAction): void;

    copyShortcutFrom(p0: AnAction): void;

    registerCustomShortcutSet(p0: ShortcutSet, p1: JComponent): void;

    registerCustomShortcutSet(p0: ShortcutSet, p1: JComponent, p2: Disposable): void;

    registerCustomShortcutSet(p0: number, p1: number, p2: JComponent): void;

    registerCustomShortcutSet(p0: JComponent, p1: Disposable): void;

    unregisterCustomShortcutSet(p0: JComponent): void;

    toString(): string;

    getSynonyms(): List;

    static getEventProject(p0: AnActionEvent): Project;

    addSynonym(p0: Supplier): void;

    addTextOverride(p0: string, p1: string): void;

    addTextOverride(p0: string, p1: Supplier): void;

    applyTextOverride(p0: AnActionEvent): void;

    applyTextOverride(p0: string, p1: Presentation): void;

    beforeActionPerformedUpdate(p0: AnActionEvent): void;

    copyActionTextOverride(p0: string, p1: string, p2: string): void;

    setDefaultIcon(p0: boolean): void;

    setInjectedContext(p0: boolean): void;

    setShortcutSet(p0: ShortcutSet): void;

    update(p0: AnActionEvent): void;
}