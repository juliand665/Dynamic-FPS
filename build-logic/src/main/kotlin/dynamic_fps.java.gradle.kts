plugins {
    id("java")
}

java {
    withSourcesJar()

    toolchain {
        languageVersion = JavaLanguageVersion.of(16)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"

    javaCompiler = javaToolchains.compilerFor {
        languageVersion = JavaLanguageVersion.of(16)
    }
}
