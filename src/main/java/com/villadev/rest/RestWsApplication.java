package com.villadev.rest;

import java.util.Locale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;

@SpringBootApplication
public class RestWsApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestWsApplication.class, args);
	}
	
	@Bean
	public LocaleResolver localeResolver() {
		//AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
		//localeResolver.setDefaultLocale(Locale.US);
		//return localeResolver;
		return new FixedLocaleResolver(new Locale("pt", "BR"));
	}
	
	@Bean
	public ResourceBundleMessageSource messageSource() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasename("messages");
		return messageSource;
	}
}
