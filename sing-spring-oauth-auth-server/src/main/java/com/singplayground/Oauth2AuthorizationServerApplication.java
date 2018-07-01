package com.singplayground;

 
import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.singplayground.model.Account;
 
@SpringBootApplication
@RestController
@EnableResourceServer
public class Oauth2AuthorizationServerApplication extends WebMvcConfigurerAdapter {
 
	public static void main(String[] args) {
		SpringApplication.run(Oauth2AuthorizationServerApplication.class, args);
	}
	
	
@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

  //@Autowired
  //AccountRepository accountRepository;
	
  @Override
  public void init(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService());
    
	  /*
    auth.inMemoryAuthentication()
    .withUser("a").roles("ADMIN").password("password")
    .and()
    .withUser("Mary").roles("BASIC").password("password");
    */
    
  }


  
  @Bean
  UserDetailsService userDetailsService() {
	  
    return new UserDetailsService() {

      @Override
      public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       // Account account = accountRepository.findByUsername(username);
       System.out.println("XXXXXXXXXX ");
       System.out.println("wwwwww " + username);
       Account account = new Account(); 
       account.setId("12345");
       account.setPassword("agentsecretx");
       account.setUsername("agentx");
      if(account != null) {
    
        return new User(account.getUsername(), account.getPassword(), true, true, true, true,
                AuthorityUtils.createAuthorityList("USER"));
        } else {
          throw new UsernameNotFoundException("could not find the user '"
                  + username + "'");
        }
      }
      
    };
    
    
    
  }
}



@EnableWebSecurity
@Configuration
class WebSecurityConfig extends WebSecurityConfigurerAdapter {
 
  @Override
  protected void configure(HttpSecurity http) throws Exception {
	  
    http.authorizeRequests().anyRequest().fullyAuthenticated().and().
    httpBasic().and().
    csrf().disable();
  }
}

	
 
	@Configuration
	@EnableAuthorizationServer
	//@EnableOAuth2Sso
	protected static class OAuth2Config extends AuthorizationServerConfigurerAdapter {
		@Autowired
		private AuthenticationManager authenticationManager;
 
		  @Bean
		  public TokenStore tokenStore() {
		      return new InMemoryTokenStore();
		  }
		
		@Override
		public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
			endpoints.authenticationManager(authenticationManager);
			endpoints.tokenStore(tokenStore());
		}
		
		@Autowired
		private ResourceServerProperties sso;
/*
		@Bean
		public ResourceServerTokenServices myUserInfoTokenServices() {
		    return new MyUserInfoTokenServices(sso.getUserInfoUri(), sso.getClientId());
		}
*/		
		@Override 
		   public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception { 
		   
				//oauthServer.realm("user").checkTokenAccess("isAuthenticated()");
				//oauthServer.realm("user").tokenKeyAccess("isAuthenticated()");
		        oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
		        System.out.println("********" + oauthServer.getCheckTokenAccess()) ;
		   }
 
		@Override
		public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
			clients.inMemory().withClient("test1").secret("test1secret")
					.authorizedGrantTypes("authorization_code", "refresh_token", "password").scopes("openid")
					.accessTokenValiditySeconds(30)
					.and()
					.withClient("test2").secret("test2secret")
					.authorizedGrantTypes("authorization_code", "refresh_token", "password").scopes("openid")
					.accessTokenValiditySeconds(30)
					;
		}
	}
 
	@RequestMapping("/user")
	public Principal user(Principal user) {
		System.out.println("--------" );
		System.out.println(user);
	
		OAuth2Authentication auth = (OAuth2Authentication) user;
        System.out.println(auth.getUserAuthentication().getDetails());
		System.out.println(user.getName());
		
		
		return user;
	}
 
}
	