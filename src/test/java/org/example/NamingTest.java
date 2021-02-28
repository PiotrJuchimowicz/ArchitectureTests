package org.example;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

class NamingTest {

    private static JavaClasses javaClasses;

    @BeforeAll
    static void init() {
        javaClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_JARS)
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("org.example");
    }

    @Test
    void entitiesShouldBeInCorrectPackage() {
        //given
        var rule = classes().that().areAnnotatedWith(Entity.class)
                .should().haveSimpleNameContaining("Entity");

        //expect
        rule.check(javaClasses);
    }

    @Test
    void controllersShouldBeInCorrectPackage() {
        //given
        var rule = classes().that().areAnnotatedWith(RestController.class)
                .or().areAnnotatedWith(Controller.class)
                .should().haveSimpleNameContaining("Controller");

        //expect
        rule.check(javaClasses);
    }

    @Test
    void configurationShouldBeInCorrectPackage() {
        //given
        var rule = classes().that().areAnnotatedWith(Configuration.class)
                .should().haveSimpleNameContaining("Config");

        //expect
        rule.check(javaClasses);
    }

    @Test
    void repositoriesShouldBeInCorrectPackage() {
        //given
        var rule = classes().that().areAnnotatedWith(Repository.class)
                .should().haveSimpleNameContaining("Repository");

        //expect
        rule.check(javaClasses);
    }

    @Test
    void servicesShouldBeInCorrectPackage() {
        //given
        var rule = classes().that().areAnnotatedWith(Service.class)
                .should().haveSimpleNameContaining("Service");

        //expect
        rule.check(javaClasses);
    }
}
