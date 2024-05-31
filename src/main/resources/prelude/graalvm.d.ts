declare namespace Java {
    export function type<T>(classPath: string): T;

    type Extension<T> = new (...args: any[]) => T;

    export function extend<P0>(p0: P0): Extension<P0>
    export function extend<P0, P1>(p0: P0, p1: P1): Extension<P0 & P1>
    export function extend<P0, P1, P2>(p0: P0, p1: P1, p2: P2): Extension<P0 & P1 & P2>
    export function extend<P0, P1, P2, P3>(p0: P0, p1: P1, p2: P2, p3: P3): Extension<P0 & P1 & P2 & P3>
    export function extend<P0, P1, P2, P3, P4>(p0: P0, p1: P1, p2: P2, p3: P3, p4: P4): Extension<P0 & P1 & P2 & P3 & P4>
}

type character = string