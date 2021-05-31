package ru.kpfu.itis.voicescom.services;

import ru.kpfu.itis.voicescom.dto.SignInDto;
import ru.kpfu.itis.voicescom.dto.SignUpDto;
import ru.kpfu.itis.voicescom.dto.Status;
import ru.kpfu.itis.voicescom.dto.TokenDto;

import java.util.List;

public interface AuthService {
    TokenDto signIn(SignInDto signInDto);
    Status signUp(SignUpDto signUpDto);
}
