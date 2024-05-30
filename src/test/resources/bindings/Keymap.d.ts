import { Scheme } from "./com.intellij.openapi.options.Scheme";
import { MouseShortcut } from "./com.intellij.openapi.actionSystem.MouseShortcut";
import { Shortcut } from "./com.intellij.openapi.actionSystem.Shortcut";
import { KeyStroke } from "./javax.swing.KeyStroke";
import { Collection } from "./java.util.Collection";
import { List } from "./java.util.List";
import { KeyboardShortcut } from "./com.intellij.openapi.actionSystem.KeyboardShortcut";
import { Map } from "./java.util.Map";

export abstract class Keymap implements Scheme{

    canModify(): boolean;

    hasActionId(p0: string, p1: MouseShortcut): boolean;

    getShortcuts(p0: string): Shortcut[];

    deriveKeymap(p0: string): Keymap;

    getParent(): Keymap;

    getName(): string;

    getPresentableName(): string;

    getActionIds(): string[];

    getActionIds(p0: Shortcut): string[];

    getActionIds(p0: KeyStroke): string[];

    getActionIds(p0: KeyStroke, p1: KeyStroke): string[];

    getActionIdList(): Collection;

    getActionIdList(p0: Shortcut): List;

    getActionIds(p0: MouseShortcut): List;

    getConflicts(p0: string, p1: KeyboardShortcut): Map;

    addShortcut(p0: string, p1: Shortcut): void;

    removeAllActionShortcuts(p0: string): void;

    removeShortcut(p0: string, p1: Shortcut): void;
}