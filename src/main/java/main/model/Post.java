package main.model;


import lombok.*;
import main.model.enums.ModerationStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "posts")
public class Post extends AbstractEntity {

    @Column(name = "is_active")
    @NotNull
    private byte isActive;

    @Column(name = "moderation_status")
    @Enumerated(EnumType.STRING)
    @NotNull
    private ModerationStatus moderationStatus;

    @Column(name = "moderator_id")
    private Integer moderatorId;

    @NotNull
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "time")
    @NotNull
    private LocalDateTime time;

    @Column(name = "title")
    @NotNull
    private String title;

    @Column(name = "text")
    @NotNull
    private String text;

    @Column(name = "view_count")
    @NotNull
    private int viewCount;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post")
    @ToString.Exclude
    private List<PostComment> comments;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post")
    @ToString.Exclude
    private List<PostVote> votes;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "tag2post"
            , joinColumns = @JoinColumn(name = "post_id")
            , inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @ToString.Exclude
    private List<Tag> tags;
}
