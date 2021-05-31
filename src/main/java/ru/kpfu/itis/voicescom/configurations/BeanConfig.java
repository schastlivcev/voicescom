package ru.kpfu.itis.voicescom.configurations;

import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Configuration
public class BeanConfig {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${formatter.time}")
    private String timeFormat;
    @Value("${formatter.date}")
    private String dateFormat;
    @Value("${formatter.datetime}")
    private String dateTimeFormat;

    @Bean
    public Algorithm algorithm() {
        return Algorithm.HMAC256(secret);
    }

    @Bean
    public DateTimeFormatter timeFormatter() {
        return DateTimeFormatter.ofPattern(timeFormat);
    }

    @Bean
    public DateTimeFormatter dateFormatter() {
        return DateTimeFormatter.ofPattern(dateFormat);
    }

    @Bean
    public DateTimeFormatter dateTimeFormatter() {
        return DateTimeFormatter.ofPattern(dateTimeFormat);
    }

    @Bean
    public LocaleResolver headerLocaleResolver() {
        AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        localeResolver.setDefaultLocale(Locale.US);
        Locale.setDefault(Locale.US);

        localeResolver.setSupportedLocales(List.of(Locale.US, new Locale("ru")));
        return localeResolver;
    }

//    @Bean(name = "multipartResolver")
//    public CommonsMultipartResolver multipartResolver() {
//        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
//        multipartResolver.setMaxUploadSize(5242881); // 5MB
//        return multipartResolver;
//    }
}
