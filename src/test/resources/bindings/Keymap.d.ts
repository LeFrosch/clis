import * as i0 from 'com.intellij.openapi.options';
import * as i1 from 'com.intellij.openapi.actionSystem';
import * as i2 from 'java.lang';
import * as i3 from 'javax.swing';
import * as i4 from 'java.util';

export abstract class Keymap implements i0.Scheme {
    canModify(): boolean;

    hasActionId(p0: string, p1: i1.MouseShortcut,): boolean;

    getShortcuts(p0: string,): i1.Shortcut[];

    deriveKeymap(p0: string,): Keymap;

    getParent(): Keymap;

    getName(): string;

    getPresentableName(): string;

    getActionIds(): i2.String[];
    getActionIds(p0: i1.Shortcut,): i2.String[];
    getActionIds(p0: i3.KeyStroke,): i2.String[];
    getActionIds(p0: i3.KeyStroke, p1: i3.KeyStroke,): i2.String[];

    getActionIdList(): i4.Collection;
    getActionIdList(p0: i1.Shortcut,): i4.List;

    getActionIds(p0: i1.MouseShortcut,): i4.List;

    getConflicts(p0: string, p1: i1.KeyboardShortcut,): i4.Map;

    addShortcut(p0: string, p1: i1.Shortcut,): void;

    removeAllActionShortcuts(p0: string,): void;

    removeShortcut(p0: string, p1: i1.Shortcut,): void;
}