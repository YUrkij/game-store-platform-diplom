package com.gamestore.platform.config;

import com.gamestore.platform.model.*;
import com.gamestore.platform.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final GenreRepository genreRepository;
    private final GameRepository gameRepository;
    private final CommentRepository commentRepository;
    private final LibraryItemRepository libraryItemRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository,
                           GenreRepository genreRepository, GameRepository gameRepository,
                           CommentRepository commentRepository, LibraryItemRepository libraryItemRepository,
                           PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.genreRepository = genreRepository;
        this.gameRepository = gameRepository;
        this.commentRepository = commentRepository;
        this.libraryItemRepository = libraryItemRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        initRoles();
        initGenres();
        initUsers();
        initGames();
        initLibraryItems();
        initComments();

        System.out.println("✅ База данных успешно инициализирована!");
        System.out.println("👤 Тестовые пользователи:");
        System.out.println("   USER:     testuser / password123");
        System.out.println("   ADMIN:    admin / admin123");
        System.out.println("   SUPERADMIN: superadmin / super123");
    }

    private void initRoles() {
        if (roleRepository.count() == 0) {
            Role userRole = new Role();
            userRole.setName("USER");
            roleRepository.save(userRole);

            Role adminRole = new Role();
            adminRole.setName("ADMIN");
            roleRepository.save(adminRole);

            Role superAdminRole = new Role();
            superAdminRole.setName("SUPERADMIN");
            roleRepository.save(superAdminRole);

            System.out.println("✅ Роли созданы: USER, ADMIN, SUPERADMIN");
        }
    }

    private void initGenres() {
        if (genreRepository.count() == 0) {
            String[] genreNames = {"RPG", "Action", "Adventure", "Strategy", "Shooter", "Sports", "Simulation", "Horror"};

            for (String name : genreNames) {
                Genre genre = new Genre();
                genre.setName(name);
                genreRepository.save(genre);
            }

            System.out.println("✅ Жанры созданы: " + Arrays.toString(genreNames));
        }
    }

    private void initUsers() {
        if (userRepository.count() == 0) {
            // Обычный пользователь
            User user = new User();
            user.setUsername("testuser");
            user.setEmail("user@example.com");
            user.setPassword(passwordEncoder.encode("password123")); // Правильное хеширование!
            user.setRole(roleRepository.findByName("USER").orElseThrow());
            user.setIsBanned(false);
            user.setBalance(BigDecimal.valueOf(1500.0));
            userRepository.save(user);

            // Администратор
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin123")); // Правильное хеширование!
            admin.setRole(roleRepository.findByName("ADMIN").orElseThrow());
            admin.setIsBanned(false);
            admin.setBalance(BigDecimal.valueOf(5000.0));
            userRepository.save(admin);

            // Суперадминистратор
            User superAdmin = new User();
            superAdmin.setUsername("superadmin");
            superAdmin.setEmail("superadmin@example.com");
            superAdmin.setPassword(passwordEncoder.encode("super123")); // Правильное хеширование!
            superAdmin.setRole(roleRepository.findByName("SUPERADMIN").orElseThrow());
            superAdmin.setIsBanned(false);
            superAdmin.setBalance(BigDecimal.valueOf(10000.0));
            userRepository.save(superAdmin);

            // Забаненный пользователь (для тестирования)
            User bannedUser = new User();
            bannedUser.setUsername("banneduser");
            bannedUser.setEmail("banned@example.com");
            bannedUser.setPassword(passwordEncoder.encode("banned123"));
            bannedUser.setRole(roleRepository.findByName("USER").orElseThrow());
            bannedUser.setIsBanned(true);
            bannedUser.setBalance(BigDecimal.ZERO);
            userRepository.save(bannedUser);

            System.out.println("✅ Пользователи созданы");
        }
    }

    private void initGames() {
        if (gameRepository.count() == 0) {
            // Получаем жанры из базы
            Genre rpg = genreRepository.findByName("RPG").orElseThrow();
            Genre action = genreRepository.findByName("Action").orElseThrow();
            Genre adventure = genreRepository.findByName("Adventure").orElseThrow();
            Genre strategy = genreRepository.findByName("Strategy").orElseThrow();
            Genre shooter = genreRepository.findByName("Shooter").orElseThrow();

            // Игра 1: The Witcher 3
            Game witcher = new Game();
            witcher.setTitle("The Witcher 3: Wild Hunt");
            witcher.setDescription("Эпическая RPG в фэнтезийном мире. Ведите Геральта из Ривии, ведьмака-мутанта, в его опасном путешествии по огромному открытому миру.");
            witcher.setPrice(new BigDecimal("1999.99"));
            witcher.setReleaseDate(LocalDate.of(2015, 5, 19));
            witcher.setDeveloper("CD Projekt Red");
            witcher.setPublisher("CD Projekt");
            witcher.setImageUrl("https://via.placeholder.com/400x300/6c63ff/ffffff?text=Witcher+3");
            witcher.setGenres(new HashSet<>(Arrays.asList(rpg, adventure)));
            gameRepository.save(witcher);

            // Игра 2: Cyberpunk 2077
            Game cyberpunk = new Game();
            cyberpunk.setTitle("Cyberpunk 2077");
            cyberpunk.setDescription("Футуристический экшен с открытым миром в ночном городе будущего. Станьте наемником Ви и ищите уникальный имплант, дающий бессмертие.");
            cyberpunk.setPrice(new BigDecimal("2999.99"));
            cyberpunk.setReleaseDate(LocalDate.of(2020, 12, 10));
            cyberpunk.setDeveloper("CD Projekt Red");
            cyberpunk.setPublisher("CD Projekt");
            cyberpunk.setImageUrl("https://via.placeholder.com/400x300/ff6b9d/ffffff?text=Cyberpunk");
            cyberpunk.setGenres(new HashSet<>(Arrays.asList(rpg, action, shooter)));
            gameRepository.save(cyberpunk);

            // Игра 3: Half-Life 2
            Game halfLife = new Game();
            halfLife.setTitle("Half-Life 2");
            halfLife.setDescription("Легендарный научно-фантастический шутер от первого лица. Примите на себя роль Гордона Фримена и сражайтесь с инопланетными захватчиками.");
            halfLife.setPrice(new BigDecimal("499.99"));
            halfLife.setReleaseDate(LocalDate.of(2004, 11, 16));
            halfLife.setDeveloper("Valve");
            halfLife.setPublisher("Valve");
            halfLife.setImageUrl("https://via.placeholder.com/400x300/8b5cf6/ffffff?text=Half-Life+2");
            halfLife.setGenres(new HashSet<>(Arrays.asList(shooter, action)));
            gameRepository.save(halfLife);

            // Игра 4: Civilization VI
            Game civ = new Game();
            civ.setTitle("Sid Meier's Civilization VI");
            civ.setDescription("Культовая стратегическая игра, в которой вы строите империю, способную выдержать испытание временем.");
            civ.setPrice(new BigDecimal("1599.99"));
            civ.setReleaseDate(LocalDate.of(2016, 10, 21));
            civ.setDeveloper("Firaxis Games");
            civ.setPublisher("2K");
            civ.setImageUrl("https://via.placeholder.com/400x300/51cf66/ffffff?text=Civilization+VI");
            civ.setGenres(new HashSet<>(Arrays.asList(strategy)));
            gameRepository.save(civ);

            // Игра 5: The Elder Scrolls V: Skyrim
            Game skyrim = new Game();
            skyrim.setTitle("The Elder Scrolls V: Skyrim");
            skyrim.setDescription("Эпическая фэнтези-RPG в огромном открытом мире. Станьте Довакином и сразитесь с драконами в провинции Скайрим.");
            skyrim.setPrice(new BigDecimal("1499.99"));
            skyrim.setReleaseDate(LocalDate.of(2011, 11, 11));
            skyrim.setDeveloper("Bethesda Game Studios");
            skyrim.setPublisher("Bethesda Softworks");
            skyrim.setImageUrl("https://via.placeholder.com/400x300/ffd93d/000000?text=Skyrim");
            skyrim.setGenres(new HashSet<>(Arrays.asList(rpg, adventure)));
            gameRepository.save(skyrim);

            System.out.println("✅ Игры созданы: 5 игр с жанрами");
        }
    }

    private void initLibraryItems() {
        if (libraryItemRepository.count() == 0) {
            User user = userRepository.findByUsername("testuser").orElseThrow();
            Game witcher = gameRepository.findByTitleContainingIgnoreCase("The Witcher 3").get(0);
            Game halfLife = gameRepository.findByTitleContainingIgnoreCase("Half-Life 2").get(0);

            // Покупка Witcher 3 пользователем testuser
            LibraryItemPK pk1 = new LibraryItemPK(user.getId(), witcher.getId());
            LibraryItem item1 = new LibraryItem();
            item1.setId(pk1);
            item1.setUser(user);
            item1.setGame(witcher);
            item1.setPurchasePrice(witcher.getPrice());
            libraryItemRepository.save(item1);

            // Покупка Half-Life 2 пользователем testuser
            LibraryItemPK pk2 = new LibraryItemPK(user.getId(), halfLife.getId());
            LibraryItem item2 = new LibraryItem();
            item2.setId(pk2);
            item2.setUser(user);
            item2.setGame(halfLife);
            item2.setPurchasePrice(halfLife.getPrice());
            libraryItemRepository.save(item2);

            System.out.println("✅ Элементы библиотеки созданы");
        }
    }

    private void initComments() {
        if (commentRepository.count() == 0) {
            User user = userRepository.findByUsername("testuser").orElseThrow();
            Game witcher = gameRepository.findByTitleContainingIgnoreCase("The Witcher 3").get(0);
            Game cyberpunk = gameRepository.findByTitleContainingIgnoreCase("Cyberpunk 2077").get(0);

            // Комментарий к Witcher 3
            Comment comment1 = new Comment();
            comment1.setUser(user);
            comment1.setGame(witcher);
            comment1.setCommentText("Великолепная игра! Провел за ней более 200 часов. Сюжет, графика, геймплей - всё на высшем уровне. Особенно впечатлили побочные квесты, каждый из которых проработан как отдельная история.");
            commentRepository.save(comment1);

            // Комментарий к Cyberpunk 2077
            Comment comment2 = new Comment();
            comment2.setUser(user);
            comment2.setGame(cyberpunk);
            comment2.setCommentText("После всех обновлений игра стала значительно лучше. Ночной город выглядит потрясающе, сюжет захватывающий. Рекомендую к покупке со скидкой.");
            commentRepository.save(comment2);

            System.out.println("✅ Комментарии созданы");
        }
    }
}