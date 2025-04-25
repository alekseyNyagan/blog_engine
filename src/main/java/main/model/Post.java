package main.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import main.model.enums.ModerationStatus;

import java.time.Instant;
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
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ToString.Exclude
    private User user;

    @Column(name = "time")
    @NotNull
    private Instant time;

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

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "tag2post"
            , joinColumns = @JoinColumn(name = "post_id")
            , inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @ToString.Exclude
    private List<Tag> tags;
}
