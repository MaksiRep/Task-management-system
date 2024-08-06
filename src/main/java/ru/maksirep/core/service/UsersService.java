package ru.maksirep.core.service;

import org.springframework.stereotype.Service;
import ru.maksirep.core.entity.Users;
import ru.maksirep.core.error.ErrorCode;
import ru.maksirep.core.error.ServiceException;
import ru.maksirep.core.repository.UsersRepository;

@Service
public class UsersService {

    private final UsersRepository usersRepository;

    public UsersService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public Users getUserById(int id) {
        return usersRepository.getUserById(id).orElseThrow(() ->
                new ServiceException(String.format("Пользователь с идентификатором \"%d\" не найден", id), ErrorCode.NOT_FOUND));
    }

    public Users getUserByEmailAndPassword(String email, String password) {
        return usersRepository.getUserByEmailAndPassword(email, password).orElseThrow(() ->
                new ServiceException("Некорректный email или пароль", ErrorCode.NOT_FOUND));
    }

    public boolean isUserExistsById(int id) {
        return usersRepository.isUserExistsById(id);
    }
}
