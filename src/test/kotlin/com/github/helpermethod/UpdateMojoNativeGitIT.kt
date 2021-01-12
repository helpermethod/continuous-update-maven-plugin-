package com.github.helpermethod

import com.soebes.itf.jupiter.extension.*
import com.soebes.itf.extension.assertj.MavenExecutionResultAssert.assertThat as mavenAssertThat
import com.soebes.itf.jupiter.maven.MavenExecutionResult
import com.soebes.itf.jupiter.maven.MavenProjectResult
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.eclipse.jgit.api.Git
import org.junit.jupiter.api.BeforeEach
import java.io.File
import java.util.stream.Collectors.toList

@MavenJupiterExtension
@MavenProject("nativeProviderIsSet")
internal class UpdateMojoNativeGitIT {
    lateinit var repo: Git

    @BeforeEach
    internal fun setUp(result: MavenProjectResult) {
        repo =  Git.open(result.targetProjectDirectory)

        repo.add().addFilepattern(".").call();
        repo.commit().setAuthor("Schorsch", "georg@email.com").setMessage("interpolate version").call()
    }

    @MavenTest
    @MavenDebug
    @MavenGoal("\${project.groupId}:\${project.artifactId}:\${project.version}:update")
    fun nativeProviderIsSet(result: MavenExecutionResult) {

        val branchList = repo.branchList().call().stream().map { it.name }.collect(toList())

        mavenAssertThat(result)
                .describedAs("the build should have been successful")
                .isSuccessful()

        assertThat(branchList)
                .describedAs("feature branch for junit-jupiter should be created")
                .filteredOn { it.startsWith("dependency-update/org.junit.jupiter-junit-jupiter") }
                .hasSize(1)
    }
}