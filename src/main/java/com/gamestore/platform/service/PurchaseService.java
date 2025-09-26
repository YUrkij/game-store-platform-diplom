package com.gamestore.platform.service;

import com.gamestore.platform.model.LibraryItem;
import com.gamestore.platform.model.LibraryItemPK;
import com.gamestore.platform.model.Game;
import com.gamestore.platform.model.User;
import com.gamestore.platform.repository.LibraryItemRepository;
import com.gamestore.platform.repository.GameRepository;
import com.gamestore.platform.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class PurchaseService {

    private final LibraryItemRepository libraryItemRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;

    public PurchaseService(LibraryItemRepository libraryItemRepository, UserRepository userRepository, GameRepository gameRepository) {
        this.libraryItemRepository = libraryItemRepository;
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
    }



    public List<LibraryItem> getUserLibrary(Long userId) {
        return libraryItemRepository.findByUserIdWithGames(userId);
    }

    public boolean hasGameInLibrary(Long userId, Long gameId) {
        return libraryItemRepository.existsById_UserIdAndId_GameId(userId, gameId);
    }

    public LibraryItem purchaseGame(Long userId, Long gameId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        if (user.getIsBanned()) {
            throw new IllegalArgumentException("Заблокированный пользователь не может совершать покупки");
        }

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Игра не найдена"));

        // Проверяем, не куплена ли игра уже
        if (libraryItemRepository.existsById_UserIdAndId_GameId(userId, gameId)) {
            throw new IllegalArgumentException("Игра уже куплена");
        }

        // Проверяем баланс
        if (user.getBalance().compareTo(game.getPrice()) < 0) {
            throw new IllegalArgumentException("Недостаточно средств на балансе");
        }

        // Списываем деньги
        user.setBalance(user.getBalance().subtract(game.getPrice()));
        userRepository.save(user);

        // Создаем запись в библиотеке
        LibraryItemPK pk = new LibraryItemPK(user.getId(), game.getId());
        LibraryItem libraryItem = new LibraryItem();
        libraryItem.setId(pk);
        libraryItem.setPurchasePrice(game.getPrice());
        libraryItem.setUser(user);
        libraryItem.setGame(game);

        return libraryItemRepository.save(libraryItem);
    }

    public BigDecimal getGamePrice(Long gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Игра не найдена"))
                .getPrice();
    }
}