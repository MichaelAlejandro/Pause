package com.pause.backend.models.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import jakarta.persistence.*

@Entity
@Table(name = "users")
class User(
    @Column(name = "uid", nullable = false, unique = true)
    var uid: String,

    @Column(name = "user_name", nullable = false)
    var userName: String,

    @Column(name = "email", nullable = false, unique = true)
    var email: String
) : BaseEntity() {

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    var pet: Pet? = null

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    var pauses: MutableSet<Pause> = mutableSetOf()

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    var reviews: MutableSet<Review> = mutableSetOf()
}