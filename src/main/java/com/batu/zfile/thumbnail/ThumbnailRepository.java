package com.batu.zfile.thumbnail;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ThumbnailRepository extends JpaRepository<Thumbnail, UUID>{

}
