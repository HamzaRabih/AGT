package com.example.tachesapp.Securite;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

import static org.springframework.security.authorization.AuthorityAuthorizationManager.hasRole;

@Configuration
@EnableWebSecurity
public class SecuriteConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .formLogin()
                .loginPage("/login")
                .usernameParameter("mail")
                .passwordParameter("motdepasse")
                .defaultSuccessUrl("/")
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .permitAll()
                .and()
                .authorizeRequests()
                .antMatchers("/img/**", "/css/**", "/js/**", "/fonts/**", "/js/app.js", "/js/feather.min.js").permitAll()
                .antMatchers("/forgetPasseWord","//forgetPasseWordProcess").permitAll()  // Ajout de cette ligne pour permettre l'accès à /forgetPasseWord à tous
                // Les droits d'accès admin
                .antMatchers(
                        "/deleteUtilisateur/**", "/CreateUtilisateur/**", "/gestUtilisateur/**", "/get-Utilisaeur/**",
                        "/get-departements-by-societe/**", "/societe/**", "/deleteSociete/**", "/CreateSociete/**",
                        "/get-Societe/**", "/gestionEquipe/**", "/get-utilisateur-by-societe/**",
                        "/get-Departement-by-societe/**", "/get-utilisateur-by-Departement/**", "/CreateEquipe/**",
                        "/deleteEquipe/**", "/utilisateurs/**", "/get-Equipe/**",
                        "/get-utilisateur-by-Selected-Departements/**", "/getAllIdresponsable/**", "/domaine/**",
                        "/CreateDomaine/**", "/deleteDomaine/**", "/get-Domaine/**", "/departement/**",
                        "/deleteDepartement/**", "/CreateDepartement/**", "/get-Departement/**","/GestionTacheAdmin/**"
                        ,"/get-tachesParents-by-idUtilisateur/**","/UpdateTacheAdmin/**"
                ).hasRole("ADMIN")
                // Le reste est autorisé
                .anyRequest().authenticated();

        http.exceptionHandling().accessDeniedPage("/403");

    }

    @Autowired
    private DataSource dataSource;
   /* @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // Utilisation de l'authentification JDBC
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .usersByUsernameQuery("SELECT mail, motdepasse, 1 as enabled FROM utilisateur WHERE mail = ? AND actif = 1")
                .authoritiesByUsernameQuery("SELECT u.mail, 'ROLE_'+ r.role as authority FROM utilisateur u INNER JOIN role r ON u.idrole = r.idrole WHERE u.mail = ?");
                //.passwordEncoder(passwordEncoder());
    }*/
   @Override
   protected void configure(AuthenticationManagerBuilder auth) throws Exception {
       auth.jdbcAuthentication()
               .dataSource(dataSource)
               .usersByUsernameQuery("SELECT mail, motdepasse, 1 as enabled FROM utilisateur WHERE mail = ? AND actif = 1")
               .authoritiesByUsernameQuery("SELECT u.mail, 'ROLE_'+ r.role as authority FROM utilisateur u INNER JOIN role r ON u.idrole = r.idrole WHERE u.mail = ?")
               .passwordEncoder(new PasswordEncoder() {
                   @Override
                   public String encode(CharSequence rawPassword) {
                       // Aucun encodage supplémentaire
                       return rawPassword.toString();
                   }

                   @Override
                   public boolean matches(CharSequence rawPassword, String encodedPassword) {
                       // Vous pouvez ajouter ici une logique pour comparer les mots de passe non cryptés
                       return rawPassword.toString().equals(encodedPassword);
                   }
               });
   }



    //@Bean :s'éxecuter au niveau de demarrage
    //pour cripter les mot de passes
    @Bean
    PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }


}

  /*@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //pour décrypter le password
        PasswordEncoder passwordEncoder =passwordEncoder();
        auth
                .inMemoryAuthentication()
                .withUser("user").password(passwordEncoder.encode("123")).roles("USER");
    }
   */