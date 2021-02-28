package org.example;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noMethods;

class MethodTest {

    private static JavaClasses javaClasses;


    @BeforeAll
    static void init() {
        javaClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_JARS)
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("org.example");
    }

    @Test
    void controllersShouldReturnResponseEntity() {
        //given
        var rule = methods().that().areNotPrivate()
                .and().areDeclaredInClassesThat().areAnnotatedWith(RestController.class)
                .should().haveRawReturnType(ResponseEntity.class);

        //expect
        rule.check(javaClasses);
    }

    @Test
    void methodsReturningOptionalShouldHaveCorrectName() {
        //given
        var rule = methods().that().arePublic()
                .and().haveRawReturnType(Optional.class)
                .should().haveNameMatching(".*[fF]ind.*");

        //expect
        rule.check(javaClasses);
    }

    @Test
    void transactionalMethodsShouldBePublic() {
        //given
        var rule = methods().that().areAnnotatedWith(Transactional.class)
                .should().bePublic();

        //expect
        rule.check(javaClasses);
    }

    @Test
    void asyncMethodShouldBePublic() {
        //given
        var rule = methods().that().areAnnotatedWith(Async.class)
                .should().bePublic();

        //expect
        rule.check(javaClasses);
    }

    @Test
    void preAuthorizeAnnotationShouldNotBeUsedOnPrivateMethods() {
        //given
        var rule = noMethods().that().areAnnotatedWith(PreAuthorize.class)
                .should().bePrivate();

        //expect
        rule.check(javaClasses);
    }

    @Test
    void methodWithBeanAnnotationShouldReturnType() {
        //given
        var rule = methods().that().areAnnotatedWith(Bean.class)
                .should().notHaveRawReturnType(Void.TYPE);
        //expect
        rule.check(javaClasses);
    }
}
