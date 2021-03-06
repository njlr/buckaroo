package com.loopperfect.buckaroo;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

import java.util.Objects;
import java.util.Optional;

public final class RecipeVersion {

    public final Either<GitCommit, RemoteArchive> source;
    public final Optional<String> target;
    public final Optional<DependencyGroup> dependencies;
    public final Optional<RemoteFile> buckResource;

    private RecipeVersion(
            final Either<GitCommit, RemoteArchive> source,
            final Optional<String> target,
            final Optional<DependencyGroup> dependencies,
            final Optional<RemoteFile> buckResource) {

        this.source = Preconditions.checkNotNull(source);
        this.target = Preconditions.checkNotNull(target);
        this.dependencies = Preconditions.checkNotNull(dependencies);
        this.buckResource = Preconditions.checkNotNull(buckResource);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, target, dependencies);
    }

    @Override
    public boolean equals(final Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj == null || !(obj instanceof RecipeVersion)) {
            return false;
        }

        final RecipeVersion other = (RecipeVersion) obj;

        return Objects.equals(source, other.source) &&
                Objects.equals(target, other.target) &&
                Objects.equals(dependencies, other.dependencies) &&
                Objects.equals(buckResource, other.buckResource);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("source", source)
                .add("target", target)
                .add("dependencies", dependencies)
                .add("buckResource", buckResource)
                .toString();
    }

    public static RecipeVersion of(final Either<GitCommit, RemoteArchive> source, final Optional<String> target, final Optional<RemoteFile> buckResource) {
        return new RecipeVersion(source, target, Optional.empty(), buckResource);
    }

    public static RecipeVersion of(final Either<GitCommit, RemoteArchive> source, final Optional<String> target,
                                   final Optional<DependencyGroup> dependencies, final Optional<RemoteFile> buckResource) {
        return new RecipeVersion(source, target, dependencies, buckResource);
    }

    public static RecipeVersion of(final Either<GitCommit, RemoteArchive> source, final Optional<String> target,
                                   final DependencyGroup dependencies, final Optional<RemoteFile> buckResource) {
        return new RecipeVersion(source, target, Optional.of(dependencies), buckResource);
    }

    public static RecipeVersion of(final RemoteArchive source, final Optional<String> target,
                                   final DependencyGroup dependencies, final Optional<RemoteFile> buckResource) {
        return new RecipeVersion(Either.right(source), target, Optional.of(dependencies), buckResource);
    }

    public static RecipeVersion of(final GitCommit source, final Optional<String> target,
            final DependencyGroup dependencies, final Optional<RemoteFile> buckResource) {
        return new RecipeVersion(Either.left(source), target, Optional.of(dependencies), buckResource);
    }

    @Deprecated
    public static RecipeVersion of(final GitCommit source, final Optional<String> target,
                                   final DependencyGroup dependencies) {
        return new RecipeVersion(Either.left(source), target, Optional.of(dependencies), Optional.empty());
    }

    @Deprecated
    public static RecipeVersion of(final GitCommit source, final DependencyGroup dependencies) {
        return new RecipeVersion(Either.left(source), Optional.empty(), Optional.of(dependencies), Optional.empty());
    }

    public static RecipeVersion of(final RemoteArchive source) {
        return new RecipeVersion(Either.right(source), Optional.empty(), Optional.empty(), Optional.empty());
    }

    public static RecipeVersion of(final GitCommit source, final String target) {
        return new RecipeVersion(Either.left(source), Optional.of(target), Optional.empty(), Optional.empty());
    }

    public static RecipeVersion of(final GitCommit source) {
        return new RecipeVersion(Either.left(source), Optional.empty(), Optional.empty(), Optional.empty());
    }
}
