package com.example.fishmail.Config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

        @Bean
        public CsrfTokenRepository csrfTokenRepository() {
        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
        repository.setHeaderName("X-XSRF-TOKEN");
        return repository;
    }

    @Bean
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    private static final String[] WHITELIST = {
        "/",
        "/kontakt",
        "/polityka-prywatnosci",
        "/zaloguj"
    };


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        // CORS
        http.cors(Customizer.withDefaults());
        // Ogólne
        http.authorizeHttpRequests(customizer -> {
            customizer.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll(); // Zezwól na wszystkie zapytania lokalnych zasobów
            customizer.requestMatchers(HttpMethod.GET,"/image/**").permitAll(); // Zezwól na wszystkie zapytania lokalnych zdjęć
            customizer.requestMatchers(WHITELIST).permitAll(); // Zezwól na wszystkie zapytania z białej listy
            // USER OBSŁUGA PROFILU
            customizer.requestMatchers(HttpMethod.GET,"/profil").hasAnyRole("USER","ADMIN"); // Podgląd podstrony profilu
            customizer.requestMatchers(HttpMethod.POST,"/zapisz-profil").hasAnyRole("USER","ADMIN"); // Zapis profilu
            customizer.requestMatchers(HttpMethod.GET,"/profil/edytuj/**").hasAnyRole("USER","ADMIN"); // Podgląd podstrony edycji profilu
            customizer.requestMatchers(HttpMethod.POST,"/edytuj-profil/**").hasAnyRole("USER","ADMIN"); // Procesowanie zmiany danych profilowych
            //
            // USER OBSŁUGA KAMPANII
            customizer.requestMatchers(HttpMethod.GET,"/kampanie").hasAnyRole("USER","ADMIN"); // Podgląd wszystkich kampanii danego usera
            customizer.requestMatchers(HttpMethod.GET,"/nowa-kampania").hasAnyRole("USER","ADMIN"); // Formularz utworzenia nowej kampanii
            customizer.requestMatchers(HttpMethod.POST,"/zapisz-kampanie").hasAnyRole("USER","ADMIN"); // Procesowanie utworzenia nowej kampanii
            customizer.requestMatchers(HttpMethod.GET,"/kampania/**").hasAnyRole("USER","ADMIN"); // Wyświetl konkretną kampanie

            customizer.requestMatchers(HttpMethod.GET,"/kampania/ksiega-korespondencji/**").hasAnyRole("USER","ADMIN"); // Wyświetl listę książki wychodzącej

            customizer.requestMatchers(HttpMethod.GET,"/kampania/email/**").hasAnyRole("USER","ADMIN"); // Wyświetl danego maila z kampanii
            customizer.requestMatchers(HttpMethod.POST,"/kampania/email-edytuj/**").hasAnyRole("USER","ADMIN"); // Procesuj edycje danego maila
            customizer.requestMatchers(HttpMethod.GET,"/kampania/dodaj-email/**").hasAnyRole("USER","ADMIN"); // Wyświetl formularz dodania wiadomości
            customizer.requestMatchers(HttpMethod.POST,"/kampania/zapisz-email/**").hasAnyRole("USER","ADMIN"); // Procesuj dodanie emaili do kampanii
            customizer.requestMatchers(HttpMethod.POST,"/kampania/usun/email/**").hasAnyRole("USER","ADMIN"); // Procesuj usuwanie emaili do kampanii
            customizer.requestMatchers(HttpMethod.GET,"/kampania/odbiorcy/**").hasAnyRole("USER","ADMIN"); // Wyświetl listę odbiorców danej kampanii
            customizer.requestMatchers(HttpMethod.POST,"/kampania/usun/**").hasAnyRole("USER","ADMIN"); // Usunięcie danej kampanii
            customizer.requestMatchers(HttpMethod.GET,"/kampania/edytuj/**").hasAnyRole("USER","ADMIN"); // Edycja danej kampanii kampanii
            customizer.requestMatchers(HttpMethod.POST,"/kampania/edytuj-kampanie/**").hasAnyRole("USER","ADMIN"); // Procesowanie edycji kampanii

            customizer.requestMatchers(HttpMethod.GET,"/kampania/odbiorcy/**").hasAnyRole("USER","ADMIN"); // Podgląd odbiorców danej kampanii
            customizer.requestMatchers(HttpMethod.POST,"/kampania/edytuj-odbiorcow/**").hasAnyRole("USER","ADMIN"); // Edycja odbiorców danej kampanii

            // DZIAŁANIE SERWISU
            customizer.requestMatchers(HttpMethod.GET,"/track/opened**").permitAll(); // Otwieranie zdjecia przyczepionego do emaila(TrackId)
            customizer.requestMatchers(HttpMethod.GET,"/aktywuj-konto**").permitAll(); // Pierwszy etap aktywacji konta poprzez wiadomość email
            customizer.requestMatchers(HttpMethod.POST,"/aktywuj/**").permitAll(); // Procesuj aktywacje konta
            customizer.requestMatchers(HttpMethod.GET,"/aktywuj-konto/ustaw-haslo**").permitAll(); // Drugi etap aktywacji konta poprzez ustawienia hasła przez użytkownika
            customizer.requestMatchers(HttpMethod.POST,"/aktywuj-konto/zapisz-haslo/**").permitAll(); // Procesuj ustawienie hasła po aktywacji konta
            customizer.requestMatchers(HttpMethod.GET,"/wyslij-link-aktywacyjny**").permitAll(); // Wyślij ponownie link aktywacyjny do konta
            customizer.requestMatchers(HttpMethod.POST,"/link-aktywacyjny**").permitAll(); // Procesuj wysłanie ponownego wysłania linku aktywacyjnego
            customizer.requestMatchers(HttpMethod.GET,"/link-aktywacyjny-wygasl").permitAll(); // Pokaż stronę w przypadku wygasłego linku aktywacyjnego

            //
            // ADMIN OBSŁUGA SERWISU
            customizer.requestMatchers(HttpMethod.GET,"/admin/fishmail").hasAnyRole("ADMIN"); // Strona admina dashboard
            customizer.requestMatchers(HttpMethod.GET,"/admin/fishmail/nowe-konto").hasAnyRole("ADMIN"); // Formularz utworzenia konta dla Admina
            customizer.requestMatchers(HttpMethod.POST,"/admin/fishmail/stworz-konto").hasAnyRole("ADMIN"); // Procesowanie założenia nowego konta
            customizer.requestMatchers(HttpMethod.GET,"/admin/fishmail/konto/**").hasAnyRole("ADMIN"); // Podgląd konta użytkownika dla Admina
            customizer.requestMatchers(HttpMethod.POST,"/admin/fishmail/konto-przedluz/**").hasAnyRole("ADMIN"); // Procesuj przedłużenie konta użytkownika o 30 dni
            customizer.requestMatchers(HttpMethod.POST,"/admin/fishmail/konto-zablokuj/**").hasAnyRole("ADMIN"); // Procesuj zablokowanie konta użytkownika
            customizer.requestMatchers(HttpMethod.POST,"/admin/fishmail/konto-usun/**").hasAnyRole("ADMIN"); // Procesuj usunięcie konta użytkownika
            // customizer.requestMatchers(HttpMethod.GET,"/admin/fishmail/edytuj-konto/**").hasAnyRole("ADMIN"); // Podgląd formularza do edycji konta dla Admina
            // customizer.requestMatchers(HttpMethod.POST,"/admin/fishmail/usun-konto/**").hasAnyRole("ADMIN"); // Procesowanie usunięcia konta dla Admina
            customizer.requestMatchers(HttpMethod.GET,"/admin/fishmail/uzytkownicy-wyslij-wiadomosc").hasAnyRole("ADMIN"); // Podgląd formularza do wysłania emaili do wszystkich użytkowników serwisu przez Admina

            //

            //  customizer.anyRequest().permitAll();
        });


        // Logowanie
        http.formLogin(customizer -> {
            customizer.loginPage("/zaloguj")
            .loginProcessingUrl("/zaloguj")
            .usernameParameter("email")
            .passwordParameter("password")
            .defaultSuccessUrl("/kampanie",true).failureUrl("/zaloguj?error=true").permitAll();
        });
        // Wylogowanie
        http.logout(customizer -> {
            customizer.logoutUrl("/wyloguj").invalidateHttpSession(true).logoutSuccessUrl("/zaloguj?wyloguj").permitAll();
        });

        // Sesje
        http.sessionManagement((sessions) -> {
            sessions.sessionConcurrency((session) -> {
                session.maximumSessions(1).expiredUrl("/zaloguj?wyloguj");
            }).sessionCreationPolicy(SessionCreationPolicy.ALWAYS);
        });

        http.headers(Customizer.withDefaults());

        http.csrf(customizer -> {
            customizer.csrfTokenRepository(csrfTokenRepository());
            // customizer.ignoringRequestMatchers("/mail/api/polo-send-email**","/mail/api/nclean-send-email**","/narko-mailing/send**");
        });

        return http.build();
    }
    
}
