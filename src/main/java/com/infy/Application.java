package com.infy;

import javax.ws.rs.ApplicationPath;

import com.infy.guice.JsonMapper;
import com.infy.guice.ThrowableMapper;
import com.infy.resource.AccountResource;
import com.infy.resource.TransferResource;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/")
public class Application extends ResourceConfig {

   public Application() {
      register(AccountResource.class);
      register(TransferResource.class);
      register(JsonMapper.class);
      register(ThrowableMapper.class);
   }
}
