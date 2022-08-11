package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
//    List<User> getAllUsersFromStorage();
//
//    User getUserByIdFromStorage(Long userId);
//
//    User createUserInStorage(User userDtoService);
//
//    User updateUserInStorage(Long userId, User userDtoService);
//
//    void deleteUserByIdInStorage(Long userId);
//
//    boolean isUserExistsById(Long userId);
}
