package com.github.danielflower.mavenplugins.release;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Test;
import scaffolding.TestProject;

import java.io.IOException;

import static com.github.danielflower.mavenplugins.release.AnnotatedTagFinderTest.saveFileInModule;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.is;

public class DiffDetectorTest {

    @Test
    public void canDetectIfFilesHaveBeenChangedForAModuleSinceSomeSpecificTag() throws Exception {
        TestProject project = TestProject.independentVersionsProject();

        AnnotatedTag tag1 = saveFileInModule(project, "console-app", "1.2", "3");
        AnnotatedTag tag2 = saveFileInModule(project, "core-utils", "2", "0");
        AnnotatedTag tag3 = saveFileInModule(project, "console-app", "1.2", "4");

        DiffDetector detector = new DiffDetector(project.local.getRepository());

        assertThat(detector.hasChangedSince("core-utils", asList(tag2)), is(false));
        assertThat(detector.hasChangedSince("console-app", asList(tag2)), is(true));
        assertThat(detector.hasChangedSince("console-app", asList(tag3)), is(false));
    }

    @Test
    public void canDetectChangesAfterTheLastTag() throws IOException, GitAPIException {
        TestProject project = TestProject.independentVersionsProject();

        saveFileInModule(project, "console-app", "1.2", "3");
        saveFileInModule(project, "core-utils", "2", "0");
        AnnotatedTag tag3 = saveFileInModule(project, "console-app", "1.2", "4");
        project.commitRandomFile("console-app");

        DiffDetector detector = new DiffDetector(project.local.getRepository());
        assertThat(detector.hasChangedSince("console-app", asList(tag3)), is(true));
    }
}