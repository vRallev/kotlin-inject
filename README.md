# LastMileKotlinInjectFork

This is a fork of the [kotlin-inject](https://github.com/evant/kotlin-inject) dependency injection
framework. The library is lacking frequent releases and many fixes are only part of the latest
snapshot builds. This fork uploads releases to our CodeArtifact repo to consume them earlier.

## Importing
```
plugins {
    id("org.jetbrains.kotlin.jvm") version "..."
    id("com.google.devtools.ksp") version "..."
}

dependencies {
    ksp("com.amazon.lastmile.fork.me.tatarka.inject:kotlin-inject-compiler-ksp:$version")
    implementation("com.amazon.lastmile.fork.me.tatarka.inject:kotlin-inject-runtime:$version")
}
```

## VersionSet
```
LastMileAppPlatform/mainline
```

## Building
```
brazil-build test assemble checkApple check --continue
```
Note that some tests may fail depending on which platforms you have installed.

## Merging upstream
Add the Github repository as remote:
```
> git remote add fork https://github.com/evant/kotlin-inject.git
```
Will result in:
```
> git remote -v

backup	ssh://git.amazon.com:2222/pkg/LastMileKotlinInjectFork/backup/rwo (fetch)
backup	ssh://git.amazon.com:2222/pkg/LastMileKotlinInjectFork/backup/rwo (push)
fork	https://github.com/evant/kotlin-inject.git (fetch)
fork	https://github.com/evant/kotlin-inject.git (push)
origin	ssh://git.amazon.com:2222/pkg/LastMileKotlinInjectFork (fetch)
origin	ssh://git.amazon.com:2222/pkg/LastMileKotlinInjectFork (push)
share	ssh://git.amazon.com:2222/pkg/LastMileKotlinInjectFork/share/rwo (fetch)
share	ssh://git.amazon.com:2222/pkg/LastMileKotlinInjectFork/share/rwo (push)
```
Then merge `fork/main` into `mainline`.

## Publishing releases
1. Ensure that all artifacts can be built and published into the local maven repo:
   ```
   ./scripts/publish-maven-local
   ```
2. Change the version in `gradle/libs.versions.toml`. The key is `kotlin-inject`. It's recommended to
   use the latest version of upstream plus the git hash:
   ```
   git rev-parse --short HEAD
   ```
   Could result in `0.7.0-9aa3e42`.
3. Commit the changes and create a tag, e.g.:
   ```
   git commit -am "Releasing 0.7.0-9aa3e42."
   git tag 0.7.0-9aa3e42
   ```
4. Push the artifacts to CodeArtifact.
   ```
   ./scripts/publish-release
   ```
5. Change the version in `gradle/libs.versions.toml` back to the old value and commit the change:
   ```
   git commit -am "Prepare next development version."
   ```
6. Push the two commits.
   ```
   git push && git push --tags
   ```

## Publishing snapshots
1. Verify in `gradle/libs.versions.toml` that the version has a `-SNAPSHOT` suffix.
2. Push the artifacts to CodeArtifact.
   ```
   ./scripts/publish-release
   ```

## Installing in Maven Local

```
./scripts/publish-maven-local
```
