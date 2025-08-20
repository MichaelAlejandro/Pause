package com.pause.backend.models.entities

import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.Lob
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapKeyColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "pets")
class Pet(

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    var user: User,

    @Column(name = "pet_name", nullable = false)
    var petName: String = "PAUSE-Pet",

    @Column(name = "state_level", nullable = false)
    var stateLevel: Int = 3,

    @Column(name = "score", nullable = false)
    var score: Int = 0,

    @Column(name = "last_updated_at", nullable = false)
    var lastUpdatedAt: LocalDateTime = LocalDateTime.now(),

    @Lob
    @Column(name = "customization", columnDefinition = "TEXT")
    var customization: String? = null
) : BaseEntity() {

    @ElementCollection
    @CollectionTable(name = "pet_unlocked_items", joinColumns = [JoinColumn(name = "pet_id")])
    @Column(name = "item_id", nullable = false)
    var unlockedItems: MutableSet<String> = mutableSetOf()

    @ElementCollection
    @CollectionTable(name = "pet_equipped_items", joinColumns = [JoinColumn(name = "pet_id")])
    @MapKeyColumn(name = "slot")
    @Column(name = "item_id")
    var equippedItems: MutableMap<String, String> = mutableMapOf()

    @ElementCollection
    @CollectionTable(name = "pet_state_trend", joinColumns = [JoinColumn(name = "pet_id")])
    @Column(name = "state_level_value")
    var stateTrend: MutableList<Int> = mutableListOf()
}