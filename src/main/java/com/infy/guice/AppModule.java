package com.infy.guice;

import com.google.inject.persist.PersistFilter;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.ServletModule;
import com.infy.service.BillingFacade;
import com.infy.service.BillingService;

public class AppModule extends ServletModule {

  @Override
  protected void configureServlets() {
    install(new JpaPersistModule("jpa-example"));
    filter("/*").through(PersistFilter.class);

    bind(BillingFacade.class).to(BillingService.class);
  }

}
