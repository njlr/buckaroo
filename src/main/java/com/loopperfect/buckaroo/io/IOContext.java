package com.loopperfect.buckaroo.io;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import com.loopperfect.buckaroo.Either;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.FileVisitOption;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface IOContext {

    void println(final String x);

    Optional<String> readln();

    boolean isFile(final Path path);

    boolean exists(final Path path);

    Either<IOException, String> readFile(final Path path);

    Optional<IOException> writeFile(final Path path, final String content);

    Either<IOException, ImmutableList<Path>> listFiles(final Path path);

    static IOContext actual() {
        return new IOContext() {

            @Override
            public void println(final String x) {
                System.out.println(x);
            }

            @Override
            public Optional<String> readln() {
                if (System.console() != null) {
                    return Optional.of(System.console().readLine());
                }
                final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                try {
                    return Optional.of(reader.readLine());
                } catch (final IOException e) {
                    return Optional.empty();
                }
            }

            @Override
            public boolean isFile(final Path path) {
                return path.toFile().isFile();
            }

            @Override
            public boolean exists(final Path path) {
                Preconditions.checkNotNull(path);
                return path.toFile().exists();
            }

            @Override
            public Either<IOException, String> readFile(final Path path) {
                Preconditions.checkNotNull(path);
                try {
                    final String content = Files.asCharSource(path.toFile(), Charset.defaultCharset()).read();
                    return Either.right(content);
                } catch (final IOException e) {
                    return Either.left(e);
                }
            }

            @Override
            public Optional<IOException> writeFile(final Path path, final String content) {
                Preconditions.checkNotNull(path);
                Preconditions.checkNotNull(content);
                try {
                    Files.write(content, path.toFile(), Charset.defaultCharset());
                    return Optional.empty();
                } catch (final IOException e) {
                    return Optional.of(e);
                }
            }

            @Override
            public Either<IOException, ImmutableList<Path>> listFiles(final Path path) {
                Preconditions.checkNotNull(path);
                try (Stream<Path> paths = java.nio.file.Files.walk(path, 1, FileVisitOption.FOLLOW_LINKS)) {
                    return Either.right(ImmutableList.copyOf(paths.collect(Collectors.toList())));
                } catch (final IOException e) {
                    return Either.left(e);
                }
            }
        };
    }
}
