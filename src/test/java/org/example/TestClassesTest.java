package org.example;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

class TestClassesTest {

    private static JavaClasses javaClasses;

    @BeforeAll
    static void init() {
        javaClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.ONLY_INCLUDE_TESTS)
                .importPackages("org.example");
    }

    @Test
    void testMethodsShouldBeDeclaredInClassesWithTestSuffix() {
        //given
        var rule = methods().that().areAnnotatedWith(Test.class)
                .should().beDeclaredInClassesThat().haveSimpleNameEndingWith("Test");

        //expect
        rule.check(javaClasses);
    }

    @Test
    void testMethodsShouldBeDeclaredInPackagePrivateClasses() {
        //given
        var rule = methods().that().areAnnotatedWith(Test.class)
                .should().beDeclaredInClassesThat().arePackagePrivate();

        //expect
        rule.check(javaClasses);
    }

    @Test
    void testMethodsShouldBePackagePrivate() {
        //given
        var rule = methods().that().areAnnotatedWith(Test.class)
                .or().areAnnotatedWith(BeforeEach.class).or().areAnnotatedWith(BeforeAll.class)
                .or().areAnnotatedWith(AfterEach.class).or().areAnnotatedWith(AfterAll.class)
                .should().notBePublic();

        //expect
        rule.check(javaClasses);
    }
}
