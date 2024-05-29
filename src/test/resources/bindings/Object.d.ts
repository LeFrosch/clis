declare namespace java.lang {
    export class Object {
        constructor();

        equals(p0: Object): boolean;

        getClass(): Class;

        notify(): void;

        notifyAll(): void;

        wait(p0: number): void;
        wait(): void;
        wait(p0: number, p1: number): void;

        toString(): string;

        hashCode(): number;
    }
}