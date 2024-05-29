import * as i0 from 'java.lang';
import * as i1 from 'com.intellij.openapi.project';
import * as i2 from 'com.intellij.openapi.util';
import * as i3 from 'javax.swing';
import * as i4 from 'java.util.function';
import * as i5 from 'com.intellij.openapi';
import * as i6 from 'java.util';

export abstract class AnAction extends i0.Object implements i1.PossiblyDumbAware, ActionUpdateThreadAware {
    static readonly EMPTY_ARRAY: AnAction[];
    static readonly ACTIONS_KEY: i2.Key;

    constructor();
    constructor(p0: string,);
    constructor(p0: string, p1: string, p2: i3.Icon,);
    constructor(p0: i4.Supplier,);
    constructor(p0: i4.Supplier, p1: i4.Supplier,);
    constructor(p0: i4.Supplier, p1: i4.Supplier, p2: i4.Supplier,);
    constructor(p0: i4.Supplier, p1: i4.Supplier, p2: i3.Icon,);
    constructor(p0: i4.Supplier, p1: i3.Icon,);
    constructor(p0: i3.Icon,);

    actionPerformed(p0: AnActionEvent,): void;

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

    copyFrom(p0: AnAction,): void;

    copyShortcutFrom(p0: AnAction,): void;

    registerCustomShortcutSet(p0: ShortcutSet, p1: i3.JComponent,): void;
    registerCustomShortcutSet(p0: ShortcutSet, p1: i3.JComponent, p2: i5.Disposable,): void;
    registerCustomShortcutSet(p0: number, p1: number, p2: i3.JComponent,): void;
    registerCustomShortcutSet(p0: i3.JComponent, p1: i5.Disposable,): void;

    unregisterCustomShortcutSet(p0: i3.JComponent,): void;

    toString(): string;

    getSynonyms(): i6.List;

    static getEventProject(p0: AnActionEvent,): i1.Project;

    addSynonym(p0: i4.Supplier,): void;

    addTextOverride(p0: string, p1: string,): void;
    addTextOverride(p0: string, p1: i4.Supplier,): void;

    applyTextOverride(p0: AnActionEvent,): void;
    applyTextOverride(p0: string, p1: Presentation,): void;

    beforeActionPerformedUpdate(p0: AnActionEvent,): void;

    copyActionTextOverride(p0: string, p1: string, p2: string,): void;

    setDefaultIcon(p0: boolean,): void;

    setInjectedContext(p0: boolean,): void;

    setShortcutSet(p0: ShortcutSet,): void;

    update(p0: AnActionEvent,): void;
}