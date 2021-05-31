package ru.kpfu.itis.voicescom.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.kpfu.itis.voicescom.dto.VoiceDto;
import ru.kpfu.itis.voicescom.models.*;
import ru.kpfu.itis.voicescom.repositories.AccentsRepository;
import ru.kpfu.itis.voicescom.repositories.CategoriesRepository;
import ru.kpfu.itis.voicescom.repositories.LanguagesRepository;
import ru.kpfu.itis.voicescom.repositories.StylesRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class VoiceDtoHandler {
    @Autowired
    private AccentsRepository accentsRepository;
    @Autowired
    private StylesRepository stylesRepository;
    @Autowired
    private CategoriesRepository categoriesRepository;
    @Autowired
    private LanguagesRepository languagesRepository;

    private VoiceDto ALL_PARAMETERS_DTO;
    private Voice ALL_PARAMETERS;

    public VoiceDto toVoiceDto(Voice voice) {
        Set<String> accents = new HashSet<>();
        Set<String> languages = new HashSet<>();
        Set<String> styles = new HashSet<>();
        Set<String> categories = new HashSet<>();
        if(voice.getAccents() != null) {
            for(Accent accent : voice.getAccents()) {
                accents.add(accent.getName());
            }
        }
        if(voice.getLanguages() != null) {
            for(Language language : voice.getLanguages()) {
                languages.add(language.getName());
            }
        }
        if(voice.getStyles() != null) {
            for(Style style : voice.getStyles()) {
                styles.add(style.getName());
            }
        }
        if(voice.getCategories() != null) {
            for(Category category : voice.getCategories()) {
                categories.add(category.getName());
            }
        }
        return VoiceDto.builder()
                .accent(accents)
                .category(categories)
                .language(languages)
                .style(styles).build();
    }

    public Voice toVoice(VoiceDto voiceDto) {
        if(ALL_PARAMETERS == null) {
            getAllParameters();
        }
        Set<Accent> accents = new HashSet<>();
        if(voiceDto.getAccent() != null) {
            for(String accent : voiceDto.getAccent()) {
                for(Accent a : ALL_PARAMETERS.getAccents()) {
                    if(accent.equals(a.getName())) {
                        accents.add(a);
                        break;
                    }
                }
            }
        }
        Set<Language> languages = new HashSet<>();
        if(voiceDto.getLanguage() != null) {
            for(String language : voiceDto.getLanguage()) {
                for(Language l : ALL_PARAMETERS.getLanguages()) {
                    if(language.equals(l.getName())) {
                        languages.add(l);
                        break;
                    }
                }
            }
        }
        Set<Category> categories = new HashSet<>();
        if(voiceDto.getCategory() != null) {
            for(String category : voiceDto.getCategory()) {
                for(Category c : ALL_PARAMETERS.getCategories()) {
                    if(category.equals(c.getName())) {
                        categories.add(c);
                        break;
                    }
                }
            }
        }
        Set<Style> styles = new HashSet<>();
        if(voiceDto.getStyle() != null) {
            for(String style : voiceDto.getStyle()) {
                for(Style s : ALL_PARAMETERS.getStyles()) {
                    if(style.equals(s.getName())) {
                        styles.add(s);
                        break;
                    }
                }
            }
        }
        return Voice.builder()
                .styles(styles)
                .categories(categories)
                .accents(accents)
                .languages(languages).build();
    }

    public VoiceDto getAllParameters() {
        if(ALL_PARAMETERS_DTO != null) {
            return ALL_PARAMETERS_DTO;
        }

        Set<String> accents = new HashSet<>();
        Set<String> languages = new HashSet<>();
        Set<String> styles = new HashSet<>();
        Set<String> categories = new HashSet<>();

        List<Accent> a = accentsRepository.findAll();
        for(Accent accent : a) {
            accents.add(accent.getName());
        }
        List<Language> l = languagesRepository.findAll();
        for(Language language : l) {
            languages.add(language.getName());
        }
        List<Style> s = stylesRepository.findAll();
        for(Style style : s) {
            styles.add(style.getName());
        }
        List<Category> c = categoriesRepository.findAll();
        for(Category category : c) {
            categories.add(category.getName());
        }

        ALL_PARAMETERS = Voice.builder()
                .languages(new HashSet<>(l))
                .accents(new HashSet<>(a))
                .categories(new HashSet<>(c))
                .styles(new HashSet<>(s)).build();

        ALL_PARAMETERS_DTO = VoiceDto.builder()
                .accent(accents)
                .category(categories)
                .language(languages)
                .style(styles).build();
        return ALL_PARAMETERS_DTO;
    }
}
