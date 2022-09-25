package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

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
    @NotNull
    private Mpa mpa;
    private final Set<Genre> genres = new TreeSet<>(Comparator.comparingLong(Genre::getId));

    public void addLike(long id) {
        likes.add(id);
        rate++;
    }

    public void removeLike(long id) {
        if (likes.remove(id)) {
            rate--;
        }
    }

    public void addGenre(Genre genre) {
        genres.add(genre);
    }

    public void deleteGenre(Genre genre) {
        genres.remove(genre);
    }
}