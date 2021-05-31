package ru.kpfu.itis.voicescom.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.kpfu.itis.voicescom.dto.SearchDto;
import ru.kpfu.itis.voicescom.dto.UserDto;
import ru.kpfu.itis.voicescom.dto.VoiceDto;
import ru.kpfu.itis.voicescom.models.User;
import ru.kpfu.itis.voicescom.repositories.UsersRepository;
import ru.kpfu.itis.voicescom.handlers.VoiceDtoHandler;

import java.util.*;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private VoiceDtoHandler voiceDtoHandler;

    @Override
    public Optional<UserDto> findUser(Long userId) {
        Optional<User> userOptional = usersRepository.findById(userId);
        if(userOptional.isPresent()) {
            User user = userOptional.get();
            VoiceDto voice = null;
            if(user.getVoice() != null) {
                voice = voiceDtoHandler.toVoiceDto(user.getVoice());
            }
            return Optional.of(UserDto.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .name(user.getName())
                    .surname(user.getSurname())
                    .role(user.getRole())
                    .birthday(user.getBirthday())
                    .createdAt(user.getCreatedAt())
                    .bio(user.getBio())
                    .voice(voice).build());
        }
        return Optional.empty();
    }

    @Override
    public SearchDto findActors(Set<String> categories, Set<String> styles, Set<String> languages, Set<String> accents) {
        List<UserDto> actors = new ArrayList<>();
        if(categories != null || styles != null || languages != null || accents != null) {
            List<User> users = usersRepository.findActors(Pageable.unpaged());
            for(User user : users) {
                VoiceDto userVoice = voiceDtoHandler.toVoiceDto(user.getVoice());
                boolean contains = false;
                if(categories != null && !categories.isEmpty()
                        && (!Collections.disjoint(userVoice.getCategory(), categories) || userVoice.getCategory().isEmpty()))
                    contains = true;
                if(accents != null && !accents.isEmpty()
                        && (!Collections.disjoint(userVoice.getAccent(), accents) || userVoice.getAccent().isEmpty()))
                    contains = true;
                if(styles != null && !styles.isEmpty()
                        && (!Collections.disjoint(userVoice.getStyle(), styles) || userVoice.getStyle().isEmpty()))
                    contains = true;
                if(languages != null && !languages.isEmpty()
                        && (!Collections.disjoint(userVoice.getLanguage(), languages) || userVoice.getLanguage().isEmpty()))
                    contains = true;
                if(!contains)
                    continue;

                actors.add(UserDto.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .name(user.getName())
                        .surname(user.getSurname())
                        .role(user.getRole())
                        .birthday(user.getBirthday())
                        .createdAt(user.getCreatedAt())
                        .bio(user.getBio())
                        .voice(voiceDtoHandler.toVoiceDto(user.getVoice())).build());
            }
        }
        return new SearchDto(voiceDtoHandler.getAllParameters(), actors);
    }
}
