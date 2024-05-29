import * as i0 from 'java.lang';

export abstract class Predicate {
    test(p0: i0.Object,): boolean;

    and(p0: Predicate,): Predicate;

    negate(): Predicate;

    or(p0: Predicate,): Predicate;

    static isEqual(p0: i0.Object,): Predicate;

    static not(p0: Predicate,): Predicate;
}