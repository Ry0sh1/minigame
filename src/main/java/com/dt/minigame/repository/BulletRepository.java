package com.dt.minigame.repository;

import com.dt.minigame.model.Bullet;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BulletRepository extends CrudRepository<Bullet, Integer> {

}
