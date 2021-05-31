package ru.kpfu.itis.voicescom.services;


import ru.kpfu.itis.voicescom.dto.SearchDto;
import ru.kpfu.itis.voicescom.dto.UserDto;

import java.util.Optional;
import java.util.Set;

public interface AccountService {
    Optional<UserDto> findUser(Long userId);
    SearchDto findActors(Set<String> categories, Set<String> styles, Set<String> languages, Set<String> accents);
}