package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Mpa> getAll() {
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM mpa;");
        List<Mpa> mpa = new ArrayList<>();
        while (sqlRowSet.next()) {
            Mpa currentMpa = new Mpa(sqlRowSet.getInt("id"), sqlRowSet.getString("mpa_name"));
            mpa.add(currentMpa);
        }
        return mpa;
    }

    public Optional<Mpa> get(long id) {
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM mpa WHERE id = ?;", id);
        if (sqlRowSet.next()) {
            Mpa mpa = new Mpa(sqlRowSet.getInt("id"), sqlRowSet.getString("mpa_name"));
            return Optional.of(mpa);
        } else {
            return Optional.empty();
        }
    }
}