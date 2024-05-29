declare namespace java.util.function {
    import {Object} from "./java.lang.Object";

    export abstract class Predicate {
        test(p0: Object): boolean;

        and(p0: Predicate): Predicate;

        negate(): Predicate;

        or(p0: Predicate): Predicate;

        static isEqual(p0: Object): Predicate;

        static not(p0: Predicate): Predicate;
    }
}