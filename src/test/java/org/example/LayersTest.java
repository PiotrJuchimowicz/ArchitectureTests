package org.example;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class LayersTest {

    private static JavaClasses javaClasses;

    @BeforeAll
    static void init() {
        javaClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_JARS)
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("org.example");
    }

    @Test
    void controllersShouldNotDependOnRepositories() {
        //given
        var rule = noClasses().that().resideInAPackage("..controller..")
                .should().dependOnClassesThat().resideInAPackage("..repository..");

        //expect
        rule.check(javaClasses);
    }

    @Test
    void controllersShouldNotDependOnEntities() {
        //given
        var rule = noClasses().that().resideInAPackage("..controller..")
                .should().dependOnClassesThat().resideInAPackage("..model..");

        //expect
        rule.check(javaClasses);
    }

    @Test
    void controllersShouldNotDependOnConfig() {
        //given
        var rule = noClasses().that().resideInAPackage("..controller..")
                .should().dependOnClassesThat().resideInAPackage("..config..");

        //expect
        rule.check(javaClasses);
    }

    @Test
    void servicesShouldNotDependOnConfigurations() {
        //given
        var rule = noClasses().that().resideInAPackage("..service..")
                .should().dependOnClassesThat().resideInAPackage("..config..");

        //expect
        rule.check(javaClasses);
    }

    @Test
    void servicesShouldNotDependOnControllers() {
        //given
        var rule = noClasses().that().resideInAPackage("..service..")
                .should().dependOnClassesThat().resideInAPackage("..controller..");

        //expect
        rule.check(javaClasses);
    }

    @Test
    void repositoriesShouldNotDependOnServices() {
        //given
        var rule = noClasses().that().resideInAPackage("..repository..")
                .should().dependOnClassesThat().resideInAPackage("..service..");

        //expect
        rule.check(javaClasses);
    }

    @Test
    void repositoriesShouldNotDependOnControllers() {
        //given
        var rule = noClasses().that().resideInAPackage("..repository..")
                .should().dependOnClassesThat().resideInAPackage("..controller..");

        //expect
        rule.check(javaClasses);
    }

    @Test
    void repositoriesShouldNotDependOnConfig() {
        //given
        var rule = noClasses().that().resideInAPackage("..repository..")
                .should().dependOnClassesThat().resideInAPackage("..config..");

        //expect
        rule.check(javaClasses);
    }

    @Test
    void modelShouldNotDependOnControllers() {
        //given
        var rule = noClasses().that().resideInAPackage("..model..")
                .should().dependOnClassesThat().resideInAPackage("..controller..");

        //expect
        rule.check(javaClasses);
    }

    @Test
    void modelShouldNotDependOnConfigurations() {
        //given
        var rule = noClasses().that().resideInAPackage("..model..")
                .should().dependOnClassesThat().resideInAPackage("..config..");

        //expect
        rule.check(javaClasses);
    }

    @Test
    void modelShouldNotDependOnRepositories() {
        //given
        var rule = noClasses().that().resideInAPackage("..model..")
                .should().dependOnClassesThat().resideInAPackage("..repository..");

        //expect
        rule.check(javaClasses);
    }

    @Test
    void modelShouldNotDependOnServices() {
        //given
        var rule = noClasses().that().resideInAPackage("..model..")
                .should().dependOnClassesThat().resideInAPackage("..service..");

        //expect
        rule.check(javaClasses);
    }
}
