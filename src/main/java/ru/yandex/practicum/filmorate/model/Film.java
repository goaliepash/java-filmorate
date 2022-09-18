package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class Film {

    private long id;
    @NotBlank
    private String name;
    @Size(max = 200)
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @Positive
    private int duration;
    @PositiveOrZero
    private int rate;
    private final Set<Long> likes = new HashSet<>();

    public void addLike(long id) {
        likes.add(id);
        rate++;
    }

    public boolean removeLike(long id) {
        if (likes.remove(id)) {
            rate--;
            return true;
        }
        return false;
    }
}