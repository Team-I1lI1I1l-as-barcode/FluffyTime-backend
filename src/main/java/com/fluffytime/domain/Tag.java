package com.fluffytime.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Table(name = "tags")
@Entity
@NoArgsConstructor
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id", nullable = false)
    private Long tagId;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(
        mappedBy = "tag",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<TagPost> tagPosts = new ArrayList<>();

    @Builder
    public Tag(Long tagId, String name) {
        this.tagId = tagId;
        this.name = name;
    }
}