package com.loopperfect.buckaroo;

import com.google.common.base.Preconditions;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Either<L, R> {

    private enum LeftOrRight {
        LEFT,
        RIGHT
    }

    private final LeftOrRight which;

    // l or r will be null depending on the value of which
    private final L l;
    private final R r;

    private Either(final LeftOrRight which, final L l, final R r) {
        this.which = which;
        this.l = l;
        this.r = r;
    }

    public boolean isLeft() {
        return which == LeftOrRight.LEFT;
    }

    public boolean isRight() {
        return which == LeftOrRight.RIGHT;
    }

    public Optional<L> left() {
        return join(Optional::of, x -> Optional.empty());
    }

    public Optional<R> right() {
        return join(x -> Optional.empty(), Optional::of);
    }

    public <T, U> Either<T, U> map(final Function<L, T> f, final Function<R, U> g) {
        Preconditions.checkNotNull(f);
        Preconditions.checkNotNull(g);
        return which == LeftOrRight.LEFT ?
            Either.left(f.apply(l)) :
            Either.right(g.apply(r));
    }

    public <T, U> Either<T, U> flatMap(final Function<L, Either<T, U>> f, final Function<R, Either<T, U>> g) {
        Preconditions.checkNotNull(f);
        Preconditions.checkNotNull(g);
        return which == LeftOrRight.LEFT ?
            f.apply(l) :
            g.apply(r);
    }

    public <T> Either<T, R> leftMap(final Function<L, T> f) {
        Preconditions.checkNotNull(f);
        return which == LeftOrRight.LEFT ?
            Either.left(f.apply(l)) :
            Either.right(r);
    }

    public <T> Either<L, T> rightMap(final Function<R, T> f) {
        Preconditions.checkNotNull(f);
        return which == LeftOrRight.LEFT ?
            Either.left(l) :
            Either.right(f.apply(r));
    }

    public <T> T join(final Function<L, T> f, final Function<R, T> g) {
        Preconditions.checkNotNull(f);
        Preconditions.checkNotNull(g);
        return which == LeftOrRight.LEFT ?
            f.apply(l) :
            g.apply(r);
    }

    public Optional<R> toOptional() {
        return which == LeftOrRight.LEFT ? Optional.empty() : Optional.of(r);
    }

    @Override
    public int hashCode() {
        return which == LeftOrRight.LEFT ? Objects.hash(l) : Objects.hash(r);
    }

    @Override
    public boolean equals(final Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj == null || !(obj instanceof Either)) {
            return false;
        }

        final Either<?, ?> other = (Either<?, ?>) obj;

        return Objects.equals(which, other.which) &&
            (which == LeftOrRight.LEFT ?
                Objects.equals(l, other.l) :
                Objects.equals(r, other.r));
    }

    @Override
    public String toString() {
        return which == LeftOrRight.LEFT ?
            "[Left " + l + "]" :
            "[Right " + r + "]";
    }

    public static <L, R> Either<L, R> left(final L x) {
        Preconditions.checkNotNull(x);
        return new Either<>(LeftOrRight.LEFT, x, null);
    }

    public static <L, R> Either<L, R> right(final R x) {
        Preconditions.checkNotNull(x);
        return new Either<>(LeftOrRight.RIGHT, null, x);
    }

    public static <L extends Throwable, R> R orThrow(final Either<L, R> either) throws L {
        Preconditions.checkNotNull(either);
        return either.right().orElseThrow(() -> either.left().get());
    }

    public static <L, R, E extends Throwable> R orThrow(final Either<L, R> either, final E exception) throws E {
        Preconditions.checkNotNull(either);
        return either.right().orElseThrow(() -> exception);
    }

    public static <L, R, E extends Throwable> R orThrow(final Either<L, R> either, final Supplier<E> exception) throws E {
        Preconditions.checkNotNull(either);
        return either.right().orElseThrow(exception);
    }

    public static <T> T join(final Either<? extends T, ? extends T> either) {
        Preconditions.checkNotNull(either);
        return either.which == LeftOrRight.LEFT ? either.l : either.r;
    }

    public static <L, R, T> T join(final Either<L, R> either, final Function<L, ? extends T> f, final Function<R, ? extends T> g) {
        Preconditions.checkNotNull(either);
        Preconditions.checkNotNull(f);
        Preconditions.checkNotNull(g);
        return either.which == LeftOrRight.LEFT ?
            f.apply(either.l) :
            g.apply(either.r);
    }

    public static <A, B, C> Function<Either<A, C>, Either<B, C>> liftLeft(final Function<A, B> f) {
        Preconditions.checkNotNull(f);
        return e -> {
            Preconditions.checkNotNull(e);
            return e.leftMap(f);
        };
    }

    public static <A, B, C> Function<Either<C, A>, Either<C, B>> liftRight(final Function<A, B> f) {
        Preconditions.checkNotNull(f);
        return e -> {
            Preconditions.checkNotNull(e);
            return e.rightMap(f);
        };
    }
}
