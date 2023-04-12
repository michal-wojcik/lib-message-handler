package au.michalwojcik.messaging.configuration.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author michal-wojcik
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(MessagingImportSelector.class)
public @interface EnableMessaging {

    ReceiveStrategy[] receiver() default {};

    SendStrategy[] sender() default {};
}
