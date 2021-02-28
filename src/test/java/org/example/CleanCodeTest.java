package org.example;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import com.tngtech.archunit.library.GeneralCodingRules;
import io.swagger.v3.oas.annotations.Operation;
import org.example.model.AbstractEntity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;
import java.util.Date;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

class CleanCodeTest {

    private static JavaClasses javaClasses;

    @BeforeAll
    static void init() {
        javaClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_JARS)
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("org.example");
    }

    @Test
    void beansShouldBeInCorrectPackage() {
        //given
        var rule = fields().that().areDeclaredInClassesThat().areAnnotatedWith(RestController.class)
                .or().areDeclaredInClassesThat().areAnnotatedWith(Controller.class)
                .or().areDeclaredInClassesThat().areAnnotatedWith(Service.class)
                .or().areDeclaredInClassesThat().areAnnotatedWith(Component.class)
                .and()
                .areDeclaredInClassesThat(haveParametrisedConstructors())
                .should().beFinal();

        //expect
        rule.check(javaClasses);
    }

    @Test
    void classesShouldBeHermetic() {
        //given
        var rule = fields().that().areDeclaredInClassesThat().areNotInterfaces()
                .and().areDeclaredInClassesThat().areNotEnums()
                .and().areNotFinal().and().areNotStatic()
                .should().notBePublic();

        //expect
        rule.check(javaClasses);
    }

    @Test
    void entityShouldExtendsAbstractEntity() {
        //given
        var rule = classes().that().areAnnotatedWith(Entity.class)
                .should().beAssignableTo(AbstractEntity.class);

        //expect
        rule.check(javaClasses);
    }

    @Test
    void controllerMethodShouldBeDocumentedBySwagger() {
        //given
        var rule = methods().that().areDeclaredInClassesThat().areAnnotatedWith(RestController.class)
                .and().haveRawReturnType(ResponseEntity.class)
                .should().beAnnotatedWith(Operation.class);

        //expect
        rule.check(javaClasses);
    }

    @Test
    void configurationClassesShouldBePackagePrivate() {
        //given
        var rule = classes().that().areAnnotatedWith(Configuration.class)
                .should().bePackagePrivate();

        //expect
        rule.check(javaClasses);
    }

    @Test
    void controllerClassesShouldBePackagePrivate() {
        //given
        var rule = classes().that().areAnnotatedWith(RestController.class)
                .should().bePackagePrivate();

        //expect
        rule.check(javaClasses);
    }

    @Test
    void classesWithOnlyStaticMethodsShouldHaveOnlyPrivateConstructor() {
        //given
        var rule = classes().that(haveOnlyStaticMethods()).and().doNotHaveSimpleName("App")
                .should().haveOnlyPrivateConstructors();

        //expect
        rule.check(javaClasses);
    }

    @Test
    void methodsShouldNotDeclareThrowingRuntimeException() {
        //given
        var rule = methods().should(notDeclareThrowingRuntimeException());

        //expect
        rule.check(javaClasses);
    }

    @Test
    void equalsAndHashCodeShouldBePresentWhenOneIsDeclared() {
        //given
        var rule = classes().should(haveBothHashCodeAndEqualsPresent());

        //expect
        rule.check(javaClasses);
    }

    @Test
    void loggerShouldBePrivateStaticFinal() {
        //given
        var rule = fields().that()
                .haveRawType(Logger.class)
                .should().bePrivate()
                .andShould().beFinal()
                .andShould().beStatic();

        //expect
        rule.check(javaClasses);
    }

    @Test
    void javaDateShouldNotBeUsed() {
        //given
        var ruleForFields = fields()
                .should().notHaveRawType(Date.class);

        var ruleForMethods = methods()
                .should().notHaveRawParameterTypes(Date.class)
                .andShould().notHaveRawReturnType(Date.class);

        //expect
        ruleForFields.check(javaClasses);
        ruleForMethods.check(javaClasses);
    }

    @Test
    void writingToConsoleShouldNotBeUsed() {
        //given
        var rule = GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;

        //expect
        rule.check(javaClasses);
    }

    @Test
    void classesShouldNotUseFieldInjection() {
        //given
        var rule = GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION;


        //expect
        rule.check(javaClasses);
    }

    @Test
    void classesShouldNotUseJavaUtilLogging() {
        //given
        var rule = GeneralCodingRules.NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING;


        //expect
        rule.check(javaClasses);
    }

    @Test
    void classesShouldNotUseJodaTime() {
        //given
        var rule = GeneralCodingRules.NO_CLASSES_SHOULD_USE_JODATIME;


        //expect
        rule.check(javaClasses);
    }

    private DescribedPredicate<JavaClass> haveParametrisedConstructors() {
        return new DescribedPredicate<>("with parametrised constructors") {
            @Override
            public boolean apply(JavaClass javaClass) {
                return javaClass.getAllConstructors().stream()
                        .anyMatch(constructor -> constructor.getRawParameterTypes().size() > 0);
            }
        };
    }

    private DescribedPredicate<JavaClass> haveOnlyStaticMethods() {
        return new DescribedPredicate<>("with all method static") {
            @Override
            public boolean apply(JavaClass javaClass) {
                return !javaClass.getMethods().isEmpty() && javaClass.getMethods().stream()
                        .filter(method -> !method.isConstructor())
                        .allMatch(method -> method.getModifiers().contains(JavaModifier.STATIC));
            }
        };
    }

    private ArchCondition<JavaMethod> notDeclareThrowingRuntimeException() {
        return new ArchCondition<>("not declare throwing runtime exception") {
            @Override
            public void check(JavaMethod javaMethod, ConditionEvents conditionEvents) {
                if (javaMethod.getExceptionTypes().stream()
                        .anyMatch(type -> type.isAssignableTo(RuntimeException.class))) {
                    conditionEvents.add(SimpleConditionEvent
                            .violated(javaMethod,
                                    "Method declare throwing runtime exception " + javaMethod.getFullName()));
                }
            }
        };
    }

    private ArchCondition<JavaClass> haveBothHashCodeAndEqualsPresent() {
        return new ArchCondition<>("have only hashCode or only equals") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents conditionEvents) {
                final boolean isHashCodePresent =
                        javaClass.getMethods().stream().anyMatch(method -> method.getName().equals("hashCode"));
                final boolean isEqualsPresent =
                        javaClass.getMethods().stream().anyMatch(method -> method.getName().equals("equals"));

                if (isHashCodePresent != isEqualsPresent) {
                    conditionEvents.add(SimpleConditionEvent.violated(javaClass,
                            "Class should have both equals and hashCode defined " + javaClass.getName()));
                }
            }
        };
    }
}
