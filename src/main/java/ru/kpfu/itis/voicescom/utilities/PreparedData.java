package ru.kpfu.itis.voicescom.utilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.kpfu.itis.voicescom.models.*;
import ru.kpfu.itis.voicescom.repositories.*;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Configuration
public class PreparedData {
    @Value("${prepared.db}")
    private String prepared;
    @Value("${prepared.admin.email}")
    private String adminEmail;
    @Value("${prepared.admin.password}")
    private String adminPassword;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    @Qualifier("timeFormatter")
    private DateTimeFormatter timeFormatter;
    @Autowired
    @Qualifier("dateFormatter")
    private DateTimeFormatter dateFormatter;
    @Autowired
    @Qualifier("dateTimeFormatter")
    private DateTimeFormatter dateTimeFormatter;

    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private AccentsRepository accentsRepository;
    @Autowired
    private LanguagesRepository languagesRepository;
    @Autowired
    private StylesRepository stylesRepository;
    @Autowired
    private CategoriesRepository categoriesRepository;

    @PostConstruct
    private void prepareDB() throws InterruptedException {
        if(!prepared.equals("true")) {
            return;
        }

        Accent accent1 = Accent.builder()
                .name("english").build();
        Accent accent2 = Accent.builder()
                .name("spanish").build();
        Accent accent3 = Accent.builder()
                .name("french").build();
        Accent accent4 = Accent.builder()
                .name("russian").build();
        Accent accent5 = Accent.builder()
                .name("german").build();
        accentsRepository.saveAll(List.of(accent1, accent2, accent3, accent4, accent5));

        Language language1 = Language.builder()
                .name("English").build();
        Language language2 = Language.builder()
                .name("Spanish").build();
        Language language3 = Language.builder()
                .name("French").build();
        Language language4 = Language.builder()
                .name("Russian").build();
        Language language5 = Language.builder()
                .name("German").build();
        languagesRepository.saveAll(List.of(language1, language2, language3, language4, language5));

        Style style1 = Style.builder()
                .name("professional").build();
        Style style2 = Style.builder()
                .name("friendly").build();
        Style style3 = Style.builder()
                .name("conversational").build();
        Style style4 = Style.builder()
                .name("explosive").build();
        Style style5 = Style.builder()
                .name("smooth").build();
        stylesRepository.saveAll(List.of(style1, style2, style3, style4, style5));

        Category category1 = Category.builder()
                .name("videogame").build();
        Category category2 = Category.builder()
                .name("podcast").build();
        Category category3 = Category.builder()
                .name("television").build();
        Category category4 = Category.builder()
                .name("animation").build();
        Category category5 = Category.builder()
                .name("business").build();
        categoriesRepository.saveAll(List.of(category1, category2, category3, category4, category5));

        User admin = User.builder()
                .role(User.Role.ADMIN)
                .name("Admin")
                .surname("Adminov")
                .birthday(LocalDate.parse("01-01-2000", dateFormatter))
                .email(adminEmail)
                .password(passwordEncoder.encode(adminPassword)).build();

        User user1 = User.builder()
                .role(User.Role.USER)
                .name("Alice")
                .surname("Aliceova")
                .birthday(LocalDate.parse("01-01-1999", dateFormatter))
                .email("alice@example.com")
                .password(passwordEncoder.encode("12345678")).build();
        User user2 = User.builder()
                .role(User.Role.USER)
                .name("Bob")
                .surname("Bobov")
                .birthday(LocalDate.parse("01-02-1999", dateFormatter))
                .email("bob@example.com")
                .password(passwordEncoder.encode("12345678")).build();
        User user3 = User.builder()
                .role(User.Role.USER)
                .name("Carl")
                .surname("Carlov")
                .birthday(LocalDate.parse("01-03-1999", dateFormatter))
                .email("carl@example.com")
                .password(passwordEncoder.encode("12345678")).build();
        usersRepository.saveAll(List.of(admin, user1, user2, user3));

        User actor1 = User.builder()
                .role(User.Role.ACTOR)
                .name("David")
                .surname("Davidov")
                .birthday(LocalDate.parse("01-04-1999", dateFormatter))
                .email("david@example.com")
                .voice(Voice.builder()
                        .accents(Set.of(accent1, accent2))
                        .categories(Set.of(category1, category2, category3))
                        .languages(Set.of(language1, language2))
                        .styles(Set.of(style1, style2)).build())
                .password(passwordEncoder.encode("12345678")).build();
        User actor2 = User.builder()
                .role(User.Role.ACTOR)
                .name("Eve")
                .surname("Eveova")
                .birthday(LocalDate.parse("01-04-1999", dateFormatter))
                .email("eve@example.com")
                .voice(Voice.builder()
                        .accents(Set.of(accent3, accent2))
                        .categories(Set.of(category4, category5, category3))
                        .languages(Set.of(language3, language2, language4))
                        .styles(Set.of(style3, style2, style4)).build())
                .password(passwordEncoder.encode("12345678")).build();
        User actor3 = User.builder()
                .role(User.Role.ACTOR)
                .name("Fred")
                .surname("Fredov")
                .birthday(LocalDate.parse("01-04-1999", dateFormatter))
                .email("fred@example.com")
                .voice(Voice.builder()
//                        .accents(Set.of(accent3, accent4))
                        .categories(Set.of(category1, category3, category4))
                        .languages(Set.of(language5, language4))
                        .styles(Set.of(style5, style3)).build())
                .password(passwordEncoder.encode("12345678")).build();
        usersRepository.saveAll(List.of(actor1, actor2, actor3));

        User mod1 = User.builder()
                .role(User.Role.MOD)
                .name("Gabe")
                .surname("Gabeov")
                .birthday(LocalDate.parse("01-03-1999", dateFormatter))
                .email("gabe@example.com")
                .password(passwordEncoder.encode("12345678")).build();
        usersRepository.saveAll(List.of(mod1));

        Thread.sleep(1000);
        usersRepository.delete(actor1);
    }
}
