import { Object } from "./java.lang.Object";
import { Keymap } from "./com.intellij.openapi.keymap.Keymap";
import { KeymapManagerListener } from "./com.intellij.openapi.keymap.KeymapManagerListener";

export abstract class KeymapManager extends Object {

    static readonly DEFAULT_IDEA_KEYMAP: string;

    static readonly GNOME_KEYMAP: string;

    static readonly KDE_KEYMAP: string;

    static readonly MAC_OS_X_10_5_PLUS_KEYMAP: string;

    static readonly MAC_OS_X_KEYMAP: string;

    static readonly X_WINDOW_KEYMAP: string;

    constructor();

    getActiveKeymap(): Keymap;

    getKeymap(p0: string): Keymap;

    removeKeymapManagerListener(p0: KeymapManagerListener): void;

    static getInstance(): KeymapManager;
}