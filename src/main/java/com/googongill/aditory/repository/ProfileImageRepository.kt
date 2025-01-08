package com.googongill.aditory.repository

import com.googongill.aditory.domain.ProfileImage
import org.springframework.data.jpa.repository.JpaRepository

interface ProfileImageRepository : JpaRepository<ProfileImage?, Long?>
