package com.example.userservice.config;

import lombok.Getter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;

@Configuration
@Getter
public class ApplicationConfig {
    @Value("${tempUser.lifespan:12}")
    private int tempUserLifespanInHour;

    @Value("${message.email.from}")
    private String senderEmail;

    @Autowired
    private MessageSource messageSource;

    /**
     * Create ModelMapper
     *
     * @return
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    /**
     * Encode password
     *
     * @return BCryptPasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Create and config  SpringTemplateEngine (implementation of ITemplateEngine in Thymeleaf)
     * It's used to handle template Thymeleaf
     * Create and config SpringResourceTemplateResolver (an implementation of ITemplateResolver in Thymeleaf)
     * It's used to identify template Thymeleaf and provide configs like prefix, suffix, ... of template files, ...
     * messageSource is message source for templates
     *
     * @return
     */
    @Bean
    public SpringTemplateEngine thymeleafTemplateEngine(ApplicationContext applicationContext) {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setApplicationContext(applicationContext);
        resolver.setPrefix("classpath:/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCacheable(false);

        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(resolver);
        templateEngine.setMessageSource(messageSource);
        return templateEngine;
    }

    // /**
    //  * Swagger config
    //  * http://localhost:8080/api/swagger-ui/index.html
    //  *
    //  * @return
    //  */
    // @Bean
    // public GroupedOpenApi controllerApi() {
    //     return GroupedOpenApi.builder()
    //             .group("Api")
    //             .packagesToScan("com.example.userservice.controller") // Specify the package to scan
    //             .build();
    // }


}
