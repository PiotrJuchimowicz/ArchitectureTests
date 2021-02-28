package org.example;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mapstruct.Mapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

class LocationTest {

    private static JavaClasses javaClasses;

    @BeforeAll
    static void init() {
        javaClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_JARS)
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("org.example");
    }

    @Test
    void entityShouldHaveProperLocationTest() {
        //given
        ArchRule rule = classes().that().areAnnotatedWith(Entity.class)
                .should().resideInAPackage("org.example.model");

        //expect
        rule.check(javaClasses);
    }

    @Test
    void controllerShouldHaveProperLocationTest() {
        //given
        ArchRule rule = classes().that().areAnnotatedWith(RestController.class)
                .or().haveSimpleNameContaining("Controller")
                .should().resideInAPackage("org.example.controller");

        //expect
        rule.check(javaClasses);
    }

    @Test
    void configurationShouldHaveProperLocationTest() {
        //given
        ArchRule rule = classes().that().areAnnotatedWith(Configuration.class)
                .or().haveSimpleNameContaining("Configuration")
                .should().resideInAPackage("org.example.config");

        //expect
        rule.check(javaClasses);
    }

    @Test
    void repositoryShouldHaveProperLocationTest() {
        //given
        ArchRule rule = classes().that().areAnnotatedWith(Repository.class)
                .or().haveSimpleNameContaining("Repository")
                .should().resideInAPackage("org.example.repository");

        //expect
        rule.check(javaClasses);
    }

    @Test
    void serviceShouldHaveProperLocationTest() {
        //given
        ArchRule rule = classes().that().areAnnotatedWith(Service.class)
                .or().haveSimpleNameContaining("Service")
                .should().resideInAPackage("org.example.service");

        //expect
        rule.check(javaClasses);
    }
}
